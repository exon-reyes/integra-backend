package integra.vacacion.service;

import integra.empleado.exception.EmpleadoException;
import integra.empleado.query.EmpleadoVacacionInfo;
import integra.empleado.repository.EmpleadoRepository;
import integra.utils.FolioGenerator;
import integra.vacacion.core.EstatusPeriodo;
import integra.vacacion.domain.model.EstatusSolicitud;
import integra.vacacion.domain.model.TipoSolicitud;
import integra.vacacion.dto.request.SolicitudDescansoRequest;
import integra.vacacion.dto.response.Festivo;
import integra.vacacion.entity.DiasSolicitudDescanso;
import integra.vacacion.entity.SolicitudDescanso;
import integra.vacacion.exception.VacacionException;
import integra.vacacion.repository.DiasSolicitudRepository;
import integra.vacacion.repository.PeriodoVacacionalRepository;
import integra.vacacion.repository.SolicitudDescansoRepository;
import integra.vacacion.service.query.CalendarioFestivoService;
import integra.vacacion.util.VacacionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class CrearSolicitudVacacion {
    private final DiasSolicitudRepository diasRepository;
    private final SolicitudDescansoRepository solicitudRepository;
    private final PeriodoVacacionalRepository periodoRepository;
    private final EmpleadoRepository empleadoRepository;
    private final CalendarioFestivoService calendarioFestivoService;

    public void crear(SolicitudDescansoRequest solicitud) {
        var periodo = periodoRepository.obtenerPeriodo(solicitud.usuarioId(), EstatusPeriodo.VIGENTE)
                .orElseThrow(VacacionException::sinPeriodoActivo);
        var empleadoInfo = empleadoRepository.findById(solicitud.usuarioId(), EmpleadoVacacionInfo.class)
                .orElseThrow(() -> EmpleadoException.notFound(Long.valueOf(solicitud.usuarioId())));

        LocalDate fechaIngreso = VacacionUtil.obtenerFechaIngreso(empleadoInfo);
        validaAtiguedadMinima(fechaIngreso);

        var festivos = obtenerFestivos(fechaIngreso);
        var fechasReservadas = obtenerFechasSolicitudRservada(solicitud.usuarioId());

        // Filtrar fechas disponibles: omitir festivos y reservadas
        List<LocalDate> fechasValidas = filtrarFechasDisponibles(solicitud.diasSeleccionados(), fechasReservadas, festivos);

        if (fechasValidas.isEmpty()) {
            throw VacacionException.fechaInvalida("No existen fechas válidas para procesar; todas las seleccionadas son festivas o ya están reservadas.");
        }

        periodo.setDiasRestantes(periodo.getDiasRestantes() - fechasValidas.size());
        // Crear Entidad Solicitud (Padre)
        SolicitudDescanso entity = new SolicitudDescanso();
        entity.setEmpleado(empleadoRepository.getReferenceById(solicitud.usuarioId()));
        entity.setFolioSolicitud(FolioGenerator.generarFolioNumericoUnico());
        entity.setTipoSolicitud(TipoSolicitud.VACACION);
        entity.setFechaCreacion(LocalDate.now());
        entity.setEstatus(EstatusSolicitud.PENDIENTE);
        entity.setPeriodo(periodo);
        entity.setEstatusNivel1(EstatusSolicitud.PENDIENTE);
        entity.setEstatusNivel2(EstatusSolicitud.PENDIENTE);

        solicitudRepository.save(entity);

        // Crear Entidades Detalle (Días)
        List<DiasSolicitudDescanso> diasEntities = fechasValidas.stream().map(fecha -> {
            DiasSolicitudDescanso dia = new DiasSolicitudDescanso();
            dia.setFolio(entity);
            dia.setFecha(fecha);
            dia.setEstatusNivel1(EstatusSolicitud.PENDIENTE);
            dia.setEstatusNivel2(EstatusSolicitud.PENDIENTE);
            return dia;
        }).toList();

        diasRepository.saveAll(diasEntities);
    }

    private List<LocalDate> filtrarFechasDisponibles(Set<LocalDate> fechasASolicitar, List<LocalDate> fechasReservadas, List<Festivo> festivos) {
        Set<LocalDate> fechasFestivas = festivos.stream().map(Festivo::fecha).collect(Collectors.toSet());

        return fechasASolicitar.stream()
                .filter(fecha -> !fechasReservadas.contains(fecha))
                .filter(fecha -> !fechasFestivas.contains(fecha))
                .sorted()
                .toList();
    }

    private List<Festivo> obtenerFestivos(LocalDate fechaIngreso) {
        return calendarioFestivoService.obtenerFestivos(LocalDate.now()
                .getYear(), VacacionUtil.proximoAniversario(fechaIngreso).getYear());
    }

    private void validaAtiguedadMinima(LocalDate fechaIngreso) {
        if (fechaIngreso == null || ChronoUnit.YEARS.between(fechaIngreso, LocalDate.now()) < 1) {
            throw VacacionException.antiguedadInsuficiente();
        }
    }

    private List<SolicitudDescanso> obtenerSolicitudesActuales(Integer usuarioId) {
        return solicitudRepository.findProximasSolicitudesPorEmpleado(usuarioId, LocalDate.now());
    }

    private List<LocalDate> obtenerFechasSolicitudRservada(Integer usuarioId) {
        var solicitudes = obtenerSolicitudesActuales(usuarioId);

        // Solo consideramos solicitudes activas: PENDIENTE, APROBADA, VIGENTE, CREADA
        // Omitimos CANCELADA, CADUCADO, CERRADO
        List<EstatusSolicitud> estatusActivos = List.of(EstatusSolicitud.PENDIENTE, EstatusSolicitud.APROBADA, EstatusSolicitud.VIGENTE, EstatusSolicitud.CREADA);

        return solicitudes.stream()
                .filter(s -> estatusActivos.contains(s.getEstatus()))
                .map(SolicitudDescanso::getDiasSolicitudDescansos)
                .flatMap(Collection::stream)
                .map(DiasSolicitudDescanso::getFecha)
                .distinct()
                .toList();
    }


}
