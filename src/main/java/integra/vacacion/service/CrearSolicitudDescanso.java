package integra.vacacion.service;

import integra.empleado.repository.EmpleadoRepository;
import integra.utils.FolioGenerator;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CrearSolicitudDescanso {

    private final DiasSolicitudRepository diasRepository;
    private final SolicitudDescansoRepository solicitudRepository;
    private final PeriodoVacacionalRepository periodoRepository;
    private final EmpleadoRepository empleadoRepository;
    private final CalendarioFestivoService calendarioFestivoService;

    private static final List<EstatusSolicitud> ESTATUS_ACTIVOS = List.of(
            EstatusSolicitud.PENDIENTE, EstatusSolicitud.APROBADA,
            EstatusSolicitud.VIGENTE, EstatusSolicitud.CREADA
    );

    public void crearSolicitud(SolicitudDescansoRequest request) {
        var periodo = periodoRepository.obtenerMejorPeriodo(request.usuarioId())
                .orElseThrow(VacacionException::sinPeriodoActivo);

        int anioActual = LocalDate.now().getYear();
        var festivos = calendarioFestivoService.obtenerFestivos(anioActual, anioActual + 1);
        var fechasVacacionesReservadas = obtenerFechasVacacionesReservadas(request.usuarioId());

        List<LocalDate> fechasValidas = filtrarFechasDisponibles(
                request.diasSeleccionados(), fechasVacacionesReservadas, festivos
        );

        if (fechasValidas.isEmpty()) {
            throw VacacionException.fechaInvalida(
                    "No existen fechas válidas para procesar; todas las seleccionadas son festivas o ya están reservadas por vacaciones."
            );
        }

        SolicitudDescanso entity = new SolicitudDescanso();
        entity.setEmpleado(empleadoRepository.getReferenceById(request.usuarioId()));
        entity.setFolioSolicitud(FolioGenerator.generarFolioNumericoUnico());
        entity.setTipoSolicitud(TipoSolicitud.DESCANSO);
        entity.setFechaCreacion(LocalDate.now());
        entity.setDiasSolicitados(fechasValidas.size());
        entity.setEstatus(EstatusSolicitud.PENDIENTE);
        entity.setPeriodo(periodo);
        entity.setEstatusNivel1(EstatusSolicitud.PENDIENTE);
        entity.setEstatusNivel2(EstatusSolicitud.PENDIENTE);

        solicitudRepository.save(entity);

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

    private List<LocalDate> filtrarFechasDisponibles(Set<LocalDate> fechasSolicitadas,
                                                     List<LocalDate> fechasVacaciones,
                                                     List<Festivo> festivos) {
        Set<LocalDate> fechasFestivas = festivos.stream()
                .map(Festivo::fecha)
                .collect(Collectors.toSet());

        Set<LocalDate> fechasReservadas = Set.copyOf(fechasVacaciones);

        return fechasSolicitadas.stream()
                .filter(fecha -> !fechasReservadas.contains(fecha))
                .filter(fecha -> !fechasFestivas.contains(fecha))
                .sorted()
                .toList();
    }

    /**
     * Obtiene las fechas reservadas exclusivamente por solicitudes de VACACION activas.
     */
    private List<LocalDate> obtenerFechasVacacionesReservadas(Integer empleadoId) {
        return solicitudRepository.findProximasSolicitudesPorEmpleado(empleadoId, LocalDate.now())
                .stream()
                .filter(s -> s.getTipoSolicitud() == TipoSolicitud.VACACION)
                .filter(s -> ESTATUS_ACTIVOS.contains(s.getEstatus()))
                .map(SolicitudDescanso::getDiasSolicitudDescansos)
                .flatMap(Collection::stream)
                .map(DiasSolicitudDescanso::getFecha)
                .distinct()
                .toList();
    }
}
