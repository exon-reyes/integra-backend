package integra.vacacion.service.query;

import integra.empleado.entity.EmpleadoEntity;
import integra.empleado.exception.EmpleadoException;
import integra.empleado.query.EmpleadoVacacionInfo;
import integra.empleado.repository.EmpleadoRepository;
import integra.vacacion.dto.response.CalendarioEquipoDTO;
import integra.vacacion.dto.response.DashboardVacacionDTO;
import integra.vacacion.dto.response.FestivoDTO;
import integra.vacacion.dto.response.SolicitudVacacionDTO;
import integra.vacacion.entity.PeriodoVacacionalEntity;
import integra.vacacion.entity.SolicitudVacacionEntity;
import integra.vacacion.entity.SolicitudVacacionEntity.EstatusSolicitud;
import integra.vacacion.exception.VacacionException;
import integra.vacacion.repository.DescansoEmpleadoRepository;
import integra.vacacion.repository.FestivoRepository;
import integra.vacacion.repository.PeriodoVacacionalRepository;
import integra.vacacion.repository.SolicitudVacacionRepository;
import integra.vacacion.service.SolicitudVacacionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VacacionQueryService {

    private final SolicitudVacacionRepository solicitudRepository;
    private final PeriodoVacacionalRepository periodoVacacionalRepository;
    private final EmpleadoRepository empleadoRepository;
    private final FestivoRepository festivoRepository;
    private final DescansoEmpleadoRepository descansoRepository;
    private final SolicitudVacacionMapper mapper;


    public DashboardVacacionDTO obtenerDashboard(Integer empleadoId) {
        EmpleadoVacacionInfo dataEmpleado = empleadoRepository.findEmpleadoVacacionInfoById(empleadoId)
                .orElseThrow(() -> EmpleadoException.notFound(Long.valueOf(empleadoId)));


        LocalDate fechaIngreso = dataEmpleado.fechaAlta();
        if (dataEmpleado.fechaReingreso() != null) {
            fechaIngreso = dataEmpleado.fechaReingreso();
        }

        int antiguedadAnios = (int) ChronoUnit.YEARS.between(fechaIngreso, LocalDate.now());


        int diasDisponibles = periodoVacacionalRepository.sumDiasRestantesByEmpleado(empleadoId);

        // Obtener solo períodos del año actual (no vencidos)
        List<PeriodoVacacionalEntity> periodosVigentes = periodoVacacionalRepository.findByEmpleadoIdAndEstatus(empleadoId, PeriodoVacacionalEntity.EstatusPeriodo.VIGENTE);

        PeriodoVacacionalEntity periodoActual = periodosVigentes.stream()
                .max(Comparator.comparing(PeriodoVacacionalEntity::getAnioLaboral))
                .orElse(null);

        Integer anioGestion = periodoActual != null ? periodoActual.getAnioGestion() : null;
        int diasTotalesAnioActual = periodoActual != null ? periodoActual.getDiasHabilitados() : 0;

        List<SolicitudVacacionEntity> historial = solicitudRepository.findByEmpleadoIdOrderByCreatedAtDesc(empleadoId);
        LocalDate hoy = LocalDate.now();

        List<SolicitudVacacionEntity> aprobadas = historial.stream()
                .filter(s -> s.getEstatus() == EstatusSolicitud.APROBADA)
                .toList();
        List<SolicitudVacacionEntity> pend = historial.stream()
                .filter(s -> s.getEstatus() == EstatusSolicitud.PENDIENTE)
                .toList();
        List<SolicitudVacacionEntity> rech = historial.stream()
                .filter(s -> s.getEstatus() == EstatusSolicitud.RECHAZADA)
                .toList();
        List<SolicitudVacacionEntity> canc = historial.stream()
                .filter(s -> s.getEstatus() == EstatusSolicitud.CANCELADA)
                .toList();

        List<SolicitudVacacionEntity> disfrutadas = aprobadas.stream()
                .filter(s -> !s.getFechaInicio().isAfter(hoy))
                .toList();
        List<SolicitudVacacionEntity> aprobPorTomar = aprobadas.stream()
                .filter(s -> s.getFechaInicio().isAfter(hoy))
                .toList();

        int diasDisfrutados = disfrutadas.stream().mapToInt(SolicitudVacacionEntity::getDiasSolicitados).sum();
        int diasAprobados = aprobPorTomar.stream().mapToInt(SolicitudVacacionEntity::getDiasSolicitados).sum();
        int diasProgramados = pend.stream().mapToInt(SolicitudVacacionEntity::getDiasSolicitados).sum();
        int diasRechazados = rech.stream().mapToInt(SolicitudVacacionEntity::getDiasSolicitados).sum();
        int diasCancelados = canc.stream().mapToInt(SolicitudVacacionEntity::getDiasSolicitados).sum();

        LocalDate proximoAniversario = fechaIngreso.plusYears(antiguedadAnios + 1);
        LocalDate limiteLegal = proximoAniversario.plusMonths(6);

        int diasProximosVencer = 0;
        LocalDate fechaProximoVencer = null;

        if (LocalDate.now().isAfter(proximoAniversario.minusMonths(1))) {
            diasProximosVencer = diasDisponibles;
            fechaProximoVencer = limiteLegal;
        }

        int anioMin = historial.stream()
                .map(s -> s.getFechaInicio().getYear())
                .min(Integer::compareTo)
                .orElse(LocalDate.now().getYear());
        int anioMax = historial.stream()
                .map(s -> s.getFechaFin().getYear())
                .max(Integer::compareTo)
                .orElse(LocalDate.now().getYear());

        List<LocalDate> todosLosFestivos = festivoRepository.findFestivosBetween(LocalDate.of(anioMin, 1, 1), LocalDate.of(anioMax, 12, 31))
                .stream().map(f -> f.getFecha()).toList();
        Set<LocalDate> todosLosDescansos = descansoRepository.findFechasDescansoByEmpleado(empleadoId);

        List<DashboardVacacionDTO.ProximaVacacionDTO> proximasVacaciones = pend.stream()
                .map(s -> new DashboardVacacionDTO.ProximaVacacionDTO(s.getId(), s.getFechaInicio(), s.getFechaFin(), obtenerDiasEfectivos(s.getFechaInicio(), s.getFechaFin(), todosLosFestivos, todosLosDescansos), s.getDiasSolicitados(), s.getEstatus()
                        .name()))
                .toList();

        List<DashboardVacacionDTO.DescansoDTO> descansosRegistradosList = descansoRepository.findByEmpleadoIdAndActivoTrue(empleadoId)
                .stream()
                .map(d -> new DashboardVacacionDTO.DescansoDTO(d.getFechaDescanso(), d.getMotivo()))
                .toList();

        List<DashboardVacacionDTO.ProximaVacacionDTO> aprobadosPorTomarList = aprobPorTomar.stream()
                .map(s -> new DashboardVacacionDTO.ProximaVacacionDTO(s.getId(), s.getFechaInicio(), s.getFechaFin(), obtenerDiasEfectivos(s.getFechaInicio(), s.getFechaFin(), todosLosFestivos, todosLosDescansos), s.getDiasSolicitados(), s.getEstatus()
                        .name())).toList();
        List<DashboardVacacionDTO.ProximaVacacionDTO> disfrutadosList = disfrutadas.stream()
                .map(s -> new DashboardVacacionDTO.ProximaVacacionDTO(s.getId(), s.getFechaInicio(), s.getFechaFin(), obtenerDiasEfectivos(s.getFechaInicio(), s.getFechaFin(), todosLosFestivos, todosLosDescansos), s.getDiasSolicitados(), s.getEstatus()
                        .name())).toList();
        List<DashboardVacacionDTO.ProximaVacacionDTO> pendientesList = pend.stream()
                .map(s -> new DashboardVacacionDTO.ProximaVacacionDTO(s.getId(), s.getFechaInicio(), s.getFechaFin(), obtenerDiasEfectivos(s.getFechaInicio(), s.getFechaFin(), todosLosFestivos, todosLosDescansos), s.getDiasSolicitados(), s.getEstatus()
                        .name())).toList();
        List<DashboardVacacionDTO.ProximaVacacionDTO> rechazadasList = rech.stream()
                .map(s -> new DashboardVacacionDTO.ProximaVacacionDTO(s.getId(), s.getFechaInicio(), s.getFechaFin(), obtenerDiasEfectivos(s.getFechaInicio(), s.getFechaFin(), todosLosFestivos, todosLosDescansos), s.getDiasSolicitados(), s.getEstatus()
                        .name())).toList();
        List<DashboardVacacionDTO.ProximaVacacionDTO> canceladasList = canc.stream()
                .map(s -> new DashboardVacacionDTO.ProximaVacacionDTO(s.getId(), s.getFechaInicio(), s.getFechaFin(), obtenerDiasEfectivos(s.getFechaInicio(), s.getFechaFin(), todosLosFestivos, todosLosDescansos), s.getDiasSolicitados(), s.getEstatus()
                        .name())).toList();

        String departamento = dataEmpleado.departamentoNombre();
        String puesto = dataEmpleado.puestoNombre();
        String unidad = dataEmpleado.unidadNombreCompleto();

        return new DashboardVacacionDTO(
                empleadoId, dataEmpleado.nombreCompleto(), antiguedadAnios, diasTotalesAnioActual, diasDisponibles,
                diasDisfrutados, diasProgramados, diasProximosVencer, fechaProximoVencer, proximasVacaciones,
                diasTotalesAnioActual, proximoAniversario, departamento, puesto, unidad, anioGestion, diasAprobados,
                diasRechazados, diasCancelados, descansosRegistradosList, aprobadosPorTomarList, disfrutadosList,
                pendientesList, rechazadasList, canceladasList
        );
    }

    public List<SolicitudVacacionDTO> obtenerHistorial(Integer empleadoId) {
        return solicitudRepository.findByEmpleadoIdOrderByCreatedAtDesc(empleadoId)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    public SolicitudVacacionDTO obtenerSolicitud(Long id) {
        SolicitudVacacionEntity entity = solicitudRepository.findById(id)
                .orElseThrow(() -> VacacionException.solicitudNoEncontrada(id));
        return mapper.toDTO(entity);
    }

    public List<SolicitudVacacionDTO> obtenerSolicitudesPendientesAprobador(Integer gestorId) {
        return solicitudRepository.findSolicitudesPendientesPorGestorDirecto(gestorId);
    }

    public List<CalendarioEquipoDTO> obtenerCalendarioEquipo(Integer empleadoId) {
        EmpleadoEntity empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> VacacionException.empleadoNoEncontrado(empleadoId));

        List<SolicitudVacacionEntity> solicitudes = solicitudRepository.findByEmpleadoIdAndEstatus(empleadoId, EstatusSolicitud.APROBADA);

        return solicitudes.stream()
                .map(s -> new CalendarioEquipoDTO(s.getEmpleadoId(), empleado.getNombreCompleto(), empleado.getDepartamento() != null ? empleado.getDepartamento()
                        .getNombre() : "", s.getFechaInicio(), s.getFechaFin(), s.getDiasSolicitados(), s.getEstatus()
                        .name()))
                .toList();
    }

    public List<CalendarioEquipoDTO> obtenerCalendarioDepartamento(Integer departamentoId) {
        return solicitudRepository.findCalendarioPorDepartamento(departamentoId);
    }

    private List<LocalDate> obtenerDiasEfectivos(LocalDate inicio, LocalDate fin, List<LocalDate> festivos, Set<LocalDate> descansos) {
        return inicio.datesUntil(fin.plusDays(1))
                .filter(fecha -> !festivos.contains(fecha) && !descansos.contains(fecha))
                .toList();
    }

    public List<FestivoDTO> obtenerCalendarioFestivo(Integer anio) {
     return   festivoRepository.findFestivosDelAnio(anio)
                .stream().map(f -> new FestivoDTO(f.getId(), f.getFecha(), f.getNombre(), f.getActivo())).toList();
    }
}
