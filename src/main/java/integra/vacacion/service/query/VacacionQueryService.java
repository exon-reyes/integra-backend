//package integra.vacacion.service.query;
//
//import integra.empleado.entity.EmpleadoEntity;
//import integra.empleado.exception.EmpleadoException;
//import integra.empleado.query.EmpleadoVacacionInfo;
//import integra.empleado.repository.EmpleadoRepository;
//import integra.model.Departamento;
//import integra.model.Empleado;
//import integra.model.Puesto;
//import integra.model.Unidad;
//import integra.vacacion.domain.model.*;
//import integra.vacacion.dto.response.CalendarioEquipoDTO;
//import integra.vacacion.dto.response.DescansoAprobacionDTO;
//import integra.vacacion.dto.response.PeriodoVacacional;
//import integra.vacacion.dto.response.SolicitudVacacionDTO;
//import integra.vacacion.entity.DescansoEmpleadoEntity;
//import integra.vacacion.entity.PeriodoVacacionalEntity;
//import integra.vacacion.entity.SolicitudVacacionEntity;
//import integra.vacacion.exception.VacacionException;
//import integra.vacacion.repository.DescansoEmpleadoRepository;
//import integra.vacacion.repository.PeriodoVacacionalRepository;
//import integra.vacacion.repository.SolicitudVacacionRepository;
//import integra.vacacion.service.SolicitudVacacionMapper;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//import java.time.temporal.ChronoUnit;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//public class VacacionQueryService {
//    private final SolicitudVacacionRepository solicitudRepository;
//    private final PeriodoVacacionalRepository periodoVacacionalRepository;
//    private final EmpleadoRepository empleadoRepository;
//    private final DescansoEmpleadoRepository descansoRepository;
//    private final SolicitudVacacionMapper mapper;
//    private final DescansoQueryService descansoQueryService;
//
/// /    public DashboardVacaciones obtenerDashboard(Integer empleadoId) {
/// /        EmpleadoVacacionInfo dataEmpleado = empleadoRepository.findEmpleadoVacacionInfoById(empleadoId)
/// /                .orElseThrow(() -> EmpleadoException.notFound(Long.valueOf(empleadoId)));
/// /
/// /        LocalDate hoy = LocalDate.now();
/// /
/// /        LocalDate fechaIngreso = dataEmpleado.fechaAlta();
/// /        if (dataEmpleado.fechaReingreso() != null) {
/// /            fechaIngreso = dataEmpleado.fechaReingreso();
/// /        }
/// /
/// /
/// /        Empleado empleado = new Empleado(dataEmpleado.id(), dataEmpleado.nombreCompleto());
/// /        empleado.setAntiguedadAnios((int) ChronoUnit.YEARS.between(fechaIngreso, LocalDate.now()));
/// /
/// /        PeriodoVacacional periodoVacacional = new PeriodoVacacional();
/// /
/// /        PeriodoVacacionalEntity ultimoPeriodo = periodoVacacionalRepository.findByEmpleadoIdAndEstatus(empleadoId, PeriodoVacacionalEntity.EstatusPeriodo.VIGENTE)
/// /                .getFirst();
/// /        periodoVacacional.setDiasRestantes(ultimoPeriodo.getDiasRestantes());
/// /        periodoVacacional.setDiasHabilitados(ultimoPeriodo.getDiasHabilitados());
/// /        periodoVacacional.setAnioGestion(ultimoPeriodo.getAnioGestion());
/// /
/// /        int antiguedadAnios = (int) ChronoUnit.YEARS.between(fechaIngreso, LocalDate.now());
/// /
/// /
/// ///        int diasDisponibles = periodoVacacionalRepository.sumDiasRestantesByEmpleado(empleadoId);
/// /
/// /        // Obtener solo períodos del año actual (no vencidos)
/// ///        List<PeriodoVacacionalEntity> periodosVigentes = periodoVacacionalRepository.findByEmpleadoIdAndEstatus(empleadoId, PeriodoVacacionalEntity.EstatusPeriodo.VIGENTE);
/// /
/// ///        PeriodoVacacionalEntity periodoActual = periodosVigentes.stream()
/// ///                .max(Comparator.comparing(PeriodoVacacionalEntity::getAnioLaboral))
/// ///                .orElse(null);
/// /
/// ///        Integer anioGestion = periodoActual != null ? periodoActual.getAnioGestion() : null;
/// /
/// ///        int diasTotalesAnioActual = periodoActual != null ? periodoActual.getDiasHabilitados() : 0;
/// /
/// /        List<SolicitudVacacionEntity> solicitudes = solicitudRepository.findByEmpleadoIdAndPeriodoId(dataEmpleado.id(), ultimoPeriodo.getId());
/// /        List<Solicitud> aprobadas = solicitudes.stream()
/// /                .filter(s -> s.getEstatus() == EstatusSolicitud.APROBADA)
/// /                .map(mapper::convertFromEntity)
/// /                .toList();
/// /
/// /        List<Solicitud> pendientes = solicitudes.stream()
/// /                .filter(s -> s.getEstatus() == EstatusSolicitud.PENDIENTE)
/// /                .map(mapper::convertFromEntity)
/// /                .toList();
/// /
/// /        List<Solicitud> rechazados = solicitudes.stream()
/// /                .filter(s -> s.getEstatus() == EstatusSolicitud.RECHAZADA)
/// /                .map(mapper::convertFromEntity)
/// /                .toList();
/// /        List<Solicitud> cancelados = solicitudes.stream()
/// /                .filter(s -> s.getEstatus() == EstatusSolicitud.CANCELADA)
/// /                .map(mapper::convertFromEntity)
/// /                .toList();
/// /
/// /        List<Descanso> dataDescansos = this.descansoQueryService.obtenerDescansos(dataEmpleado.id(), ultimoPeriodo.getId());
/// /        Descansos descansos = new Descansos();
/// /        descansos.setPendientes(dataDescansos.stream()
/// /                .filter(descanso -> descanso.getEstatus() == EstatusSolicitud.PENDIENTE)
/// /                .toList());
/// /        descansos.setAprobados(dataDescansos.stream()
/// /                .filter(descanso -> descanso.getEstatus() == EstatusSolicitud.APROBADA)
/// /                .toList());
/// /        descansos.setRechazados(dataDescansos.stream()
/// /                .filter(descanso -> descanso.getEstatus() == EstatusSolicitud.RECHAZADA)
/// /                .toList());
/// /
/// /
/// ///
/// ///
/// ///
/// ///        List<SolicitudVacacionEntity> historial = solicitudRepository.findByEmpleadoIdOrderByCreatedAtDesc(empleadoId);
/// ///        LocalDate hoy = LocalDate.now();
/// ///
/// ///        List<SolicitudVacacionEntity> apro = historial.stream()
/// ///                .filter(s -> s.getEstatus() == EstatusSolicitud.APROBADA)
/// ///                .toList();
/// ///        List<SolicitudVacacionEntity> pend = historial.stream()
/// ///                .filter(s -> s.getEstatus() == EstatusSolicitud.PENDIENTE)
/// ///                .toList();
/// ///        List<SolicitudVacacionEntity> rech = historial.stream()
/// ///                .filter(s -> s.getEstatus() == EstatusSolicitud.RECHAZADA)
/// ///                .toList();
/// ///        List<SolicitudVacacionEntity> canc = historial.stream()
/// ///                .filter(s -> s.getEstatus() == EstatusSolicitud.CANCELADA)
/// ///                .toList();
/// /
/// /        List<Solicitud> disfrutadas = aprobadas.stream().filter(s -> !s.getFechaInicio().isAfter(hoy)).toList();
/// /        List<Solicitud> aprobPorTomar = aprobadas.stream().filter(s -> s.getFechaInicio().isAfter(hoy)).toList();
/// ///        int diasDisfrutados = disfrutadas.stream().mapToInt(Solicitud::getDiasSolicitados).sum();
/// ///        int diasAprobados = aprobPorTomar.stream().mapToInt(Solicitud::getDiasSolicitados).sum();
/// ///        int diasProgramados = pendientes.stream().mapToInt(Solicitud::getDiasSolicitados).sum();
/// ///        int diasRechazados = rechazados.stream().mapToInt(Solicitud::getDiasSolicitados).sum();
/// ///        int diasCancelados = cancelados.stream().mapToInt(Solicitud::getDiasSolicitados).sum();
/// /
/// /        LocalDate proximoAniversario = fechaIngreso.plusYears(antiguedadAnios + 1);
/// ///        LocalDate limiteLegal = proximoAniversario.plusMonths(6);
/// /
/// ///        int diasProximosVencer = 0;
/// ///        LocalDate fechaProximoVencer = null;
/// ///
/// ///        if (LocalDate.now().isAfter(proximoAniversario.minusMonths(1))) {
/// ///            diasProximosVencer = diasDisponibles;
/// ///            fechaProximoVencer = limiteLegal;
/// ///        }
/// ///
/// ///        int anioMin = historial.stream()
/// ///                .map(s -> s.getFechaInicio().getYear())
/// ///                .min(Integer::compareTo)
/// ///                .orElse(LocalDate.now().getYear());
/// ///        int anioMax = historial.stream()
/// ///                .map(s -> s.getFechaFin().getYear())
/// ///                .max(Integer::compareTo)
/// ///                .orElse(LocalDate.now().getYear());
/// /
/// ///        List<LocalDate> todosLosFestivos = festivoRepository.findFestivosBetween(LocalDate.of(anioMin, 1, 1), LocalDate.of(anioMax, 12, 31))
/// ///                .stream()
/// ///                .map(f -> f.getFecha())
/// ///                .toList();
/// ///        // Solo descansos APROBADOS (activo=true) son válidos para el cálculo de días laborables
/// ///        Set<LocalDate> todosLosDescansos = descansoRepository.findFechasDescansoByEmpleado(empleadoId);
/// /
/// ///        List<DashboardVacacionDTO.ProximaVacacionDTO> proximasVacaciones = pend.stream()
/// ///                .map(s -> new DashboardVacacionDTO.ProximaVacacionDTO(s.getId(), s.getFechaInicio(), s.getFechaFin(), obtenerDiasEfectivos(s.getFechaInicio(), s.getFechaFin(), todosLosFestivos, todosLosDescansos), s.getDiasSolicitados(), s.getEstatus()
/// ///                        .name()))
/// ///                .toList();
/// ///
/// ///        List<DashboardVacacionDTO.DescansoDTO> descansosRegistradosList = descansoRepository.findByEmpleadoIdAndEstatus(empleadoId, EstatusSolicitud.APROBADA)
/// ///                .stream()
/// ///                .map(d -> new DashboardVacacionDTO.DescansoDTO(d.getFechaDescanso(), d.getMotivo()))
/// ///                .toList();
/// ///
/// ///        List<DashboardVacacionDTO.DescansoDTO> descansosPendientesList = descansoRepository.findByEmpleadoIdAndEstatus(empleadoId, EstatusSolicitud.PENDIENTE)
/// ///                .stream()
/// ///                .map(d -> new DashboardVacacionDTO.DescansoDTO(d.getFechaDescanso(), d.getMotivo()))
/// ///                .toList();
/// ///
/// ///        List<DashboardVacacionDTO.ProximaVacacionDTO> aprobadosPorTomarList = aprobPorTomar.stream()
/// ///                .map(s -> new DashboardVacacionDTO.ProximaVacacionDTO(s.getId(), s.getFechaInicio(), s.getFechaFin(), obtenerDiasEfectivos(s.getFechaInicio(), s.getFechaFin(), todosLosFestivos, todosLosDescansos), s.getDiasSolicitados(), s.getEstatus()
/// ///                        .name()))
/// ///                .toList();
/// ///        List<DashboardVacacionDTO.ProximaVacacionDTO> disfrutadosList = disfrutadas.stream()
/// ///                .map(s -> new DashboardVacacionDTO.ProximaVacacionDTO(s.getId(), s.getFechaInicio(), s.getFechaFin(), obtenerDiasEfectivos(s.getFechaInicio(), s.getFechaFin(), todosLosFestivos, todosLosDescansos), s.getDiasSolicitados(), s.getEstatus()
/// ///                        .name()))
/// ///                .toList();
/// ///        List<DashboardVacacionDTO.ProximaVacacionDTO> pendientesList = pend.stream()
/// ///                .map(s -> new DashboardVacacionDTO.ProximaVacacionDTO(s.getId(), s.getFechaInicio(), s.getFechaFin(), obtenerDiasEfectivos(s.getFechaInicio(), s.getFechaFin(), todosLosFestivos, todosLosDescansos), s.getDiasSolicitados(), s.getEstatus()
/// ///                        .name()))
/// ///                .toList();
/// ///        List<DashboardVacacionDTO.ProximaVacacionDTO> rechazadasList = rech.stream()
/// ///                .map(s -> new DashboardVacacionDTO.ProximaVacacionDTO(s.getId(), s.getFechaInicio(), s.getFechaFin(), obtenerDiasEfectivos(s.getFechaInicio(), s.getFechaFin(), todosLosFestivos, todosLosDescansos), s.getDiasSolicitados(), s.getEstatus()
/// ///                        .name()))
/// ///                .toList();
/// ///        List<DashboardVacacionDTO.ProximaVacacionDTO> canceladasList = canc.stream()
/// ///                .map(s -> new DashboardVacacionDTO.ProximaVacacionDTO(s.getId(), s.getFechaInicio(), s.getFechaFin(), obtenerDiasEfectivos(s.getFechaInicio(), s.getFechaFin(), todosLosFestivos, todosLosDescansos), s.getDiasSolicitados(), s.getEstatus()
/// ///                        .name()))
/// ///                .toList();
/// /
/// /        String departamento = dataEmpleado.departamentoNombre();
/// /        String puesto = dataEmpleado.puestoNombre();
/// /        String unidad = dataEmpleado.unidadNombreCompleto();
/// /
/// /        empleado.setDepartamento(new Departamento(departamento));
/// /        empleado.setPuesto(new Puesto(puesto));
/// /
/// /        empleado.setUnidad(new Unidad(unidad));
/// /
/// /
/// /        return DashboardVacaciones.builder()
/// /                .empleado(empleado)
/// /                .proximoAniversario(proximoAniversario)
/// /                .periodoVacacional(periodoVacacional)
/// /                .descansos(descansos)
/// /                .vacaciones(new Vacaciones(pendientes, aprobadas, rechazados, cancelados, disfrutadas, aprobPorTomar))
/// /                .build();
/// ///        return new DashboardVacacionDTO(empleado, diasTotalesAnioActual, diasDisponibles, diasDisfrutados, diasProgramados, diasProximosVencer, fechaProximoVencer, proximasVacaciones, diasTotalesAnioActual, proximoAniversario, anioGestion, diasAprobados, diasRechazados, diasCancelados, descansosRegistradosList, descansosPendientesList, aprobadosPorTomarList, disfrutadosList, pendientesList, rechazadasList, canceladasList);
/// /
/// /    }
//
//    public List<SolicitudVacacionDTO> obtenerHistorial(Integer empleadoId) {
//        return solicitudRepository.findByEmpleadoIdOrderByCreatedAtDesc(empleadoId)
//                .stream()
//                .map(mapper::toDTO)
//                .toList();
//    }
//
//    public SolicitudVacacionDTO obtenerSolicitud(Long id) {
//        SolicitudVacacionEntity entity = solicitudRepository.findById(id)
//                .orElseThrow(() -> VacacionException.solicitudNoEncontrada(id));
//        return mapper.toDTO(entity);
//    }
//
//    public List<SolicitudVacacionDTO> obtenerSolicitudesPendientesAprobador(Integer gestorId) {
//        return solicitudRepository.findSolicitudesPendientesPorGestorDirecto(gestorId);
//    }
//
//    public List<DescansoAprobacionDTO> obtenerDescansosPendientes() {
//        List<DescansoEmpleadoEntity> pendientes = descansoRepository.findAllByEstatus(EstatusSolicitud.PENDIENTE);
//        // Agrupar por empleado
//        Map<Integer, List<DescansoEmpleadoEntity>> porEmpleado = pendientes.stream()
//                .collect(Collectors.groupingBy(DescansoEmpleadoEntity::getEmpleadoId));
//
//        return porEmpleado.entrySet().stream().map(e -> {
//            Integer empId = e.getKey();
//            List<LocalDate> fechas = e.getValue().stream().map(DescansoEmpleadoEntity::getFechaDescanso).toList();
//            String nombre = empleadoRepository.findById(empId)
//                    .map(emp -> emp.getNombreCompleto())
//                    .orElse("Empleado #" + empId);
//            return new DescansoAprobacionDTO(empId, nombre, fechas);
//        }).toList();
//    }
//
//    public List<CalendarioEquipoDTO> obtenerCalendarioEquipo(Integer empleadoId) {
//        EmpleadoEntity empleado = empleadoRepository.findById(empleadoId)
//                .orElseThrow(() -> VacacionException.empleadoNoEncontrado(empleadoId));
//
//        List<SolicitudVacacionEntity> solicitudes = solicitudRepository.findByEmpleadoIdAndEstatus(empleadoId, EstatusSolicitud.APROBADA);
//
//        return solicitudes.stream()
//                .map(s -> new CalendarioEquipoDTO(s.getEmpleadoId(), empleado.getNombreCompleto(), empleado.getDepartamento() != null ? empleado.getDepartamento()
//                        .getNombre() : "", s.getFechaInicio(), s.getFechaFin(), s.getDiasSolicitados(), s.getEstatus()
//                        .name()))
//                .toList();
//    }
//
//    public List<CalendarioEquipoDTO> obtenerCalendarioDepartamento(Integer departamentoId) {
//        return solicitudRepository.findCalendarioPorDepartamento(departamentoId);
//    }
//
//    private List<LocalDate> obtenerDiasEfectivos(LocalDate inicio, LocalDate fin, List<LocalDate> festivos, Set<LocalDate> descansos) {
//        return inicio.datesUntil(fin.plusDays(1))
//                .filter(fecha -> !festivos.contains(fecha) && !descansos.contains(fecha))
//                .toList();
//    }
//}
