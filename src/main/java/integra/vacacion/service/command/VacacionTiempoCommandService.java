package integra.vacacion.service.command;

import integra.empleado.service.ConsultarCatalogoEmpleados;
import integra.vacacion.domain.model.DashboardSolicitudTiempo;
import integra.vacacion.domain.model.EstatusSolicitud;
import integra.vacacion.domain.model.TipoSolicitud;
import integra.vacacion.dto.request.SolicitudVacacionRequest;
import integra.vacacion.dto.response.Festivo;
import integra.vacacion.entity.EmpleadoTiempoEntity;
import integra.vacacion.entity.PeriodoVacacionalEntity;
import integra.vacacion.exception.VacacionException;
import integra.vacacion.repository.EmpleadoTiempoEntityRepository;
import integra.vacacion.repository.PeriodoVacacionalRepository;
import integra.vacacion.service.query.CalendarioFestivoService;
import integra.vacacion.service.query.DashboardService;
import integra.vacacion.service.validation.SolicitudValidatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Transactional
@RequiredArgsConstructor
@Service
public class VacacionTiempoCommandService {
    private final CalendarioFestivoService calendarioFestivoService;
    private final EmpleadoTiempoEntityRepository repository;
    private final ConsultarCatalogoEmpleados catalogoEmpleados;
    private final DashboardService dashboardService;
    private final SolicitudValidatorService validatorService;
    private final PeriodoVacacionalRepository periodoRepository;
    private final VacacionHistorialCommandService historialCommandService;

    public void solicitarVacaciones(Integer empleadoId, SolicitudVacacionRequest request) {

        DashboardSolicitudTiempo dashboard = dashboardService.obtenerDashboard(empleadoId);

        validatorService.validarAntiguedadMinima(dashboard.getEmpleado());

        List<Festivo> festivosEnRango = calendarioFestivoService.obtenerFestivos(LocalDate.now()
                .getYear(), dashboard.getProximoAniversario().getYear());
        Set<LocalDate> fechasDisponibles = validatorService.filtrarCruceSolicitudVacaciones(request.diasSeleccionados(), dashboard, festivosEnRango);

        fechasDisponibles.forEach(localDate -> {
            EmpleadoTiempoEntity entity = new EmpleadoTiempoEntity();
            entity.setEmpleadoId(empleadoId);
            entity.setFecha(localDate);
            entity.setActivo(true);
            entity.setComentario(request.motivo());
            entity.setTipo(TipoSolicitud.VACACION);
            entity.setPeriodoId(dashboard.getPeriodoVacacional().getId());
            entity.setEstatus(EstatusSolicitud.PENDIENTE);
            repository.save(entity);
        });
        periodoRepository.actualizarDisponibilidadad(dashboard.getPeriodoVacacional()
                .getDiasRestantes() - fechasDisponibles.size(), dashboard.getPeriodoVacacional().getId());
    }

    public void solicitarDescansos(Integer empleadoId, Set<LocalDate> diasSolicitados) {
        DashboardSolicitudTiempo dashboard = dashboardService.obtenerDashboard(empleadoId);

        Set<LocalDate> fechasDisponibles = validatorService.filtrarCruceSolicitudVacaciones(diasSolicitados, dashboard, null);

        if (fechasDisponibles.isEmpty()) {
            if (diasSolicitados.size() == 1) {
                throw VacacionException.fechaInvalida("La fecha seleccionada ya se encuentra registrada en una solicitud previa.");
            } else {
                throw VacacionException.fechaInvalida("Todas las fechas seleccionadas coinciden con solicitudes previas.");
            }
        }

        Long periodoId = dashboard.getPeriodoVacacional() != null ? dashboard.getPeriodoVacacional().getId() : null;

        fechasDisponibles.forEach(localDate -> {
            EmpleadoTiempoEntity entity = new EmpleadoTiempoEntity();
            entity.setEmpleadoId(empleadoId);
            entity.setFecha(localDate);
            entity.setActivo(true);
            entity.setComentario("Solicitud de descanso");
            entity.setTipo(TipoSolicitud.DESCANSO);
            entity.setPeriodoId(periodoId);
            entity.setEstatus(EstatusSolicitud.PENDIENTE);
            repository.save(entity);
        });
    }

    public void cancelarSolicitudVacaciones(Long solicitudId, Integer usuarioId) {
        EmpleadoTiempoEntity solicitud = repository.findById(solicitudId)
                .orElseThrow(() -> VacacionException.solicitudNoEncontrada(solicitudId));

        if (solicitud.getEstatus() == EstatusSolicitud.PENDIENTE) {
            repository.deleteById(solicitudId);
            return;
        }
        // 2. No cancelar si ya pasó la fecha (disfrutado implícito)
        if (solicitud.getFecha().isBefore(LocalDate.now()) && solicitud.getEstatus() == EstatusSolicitud.APROBADA) {
            throw VacacionException.estadoInvalido("Cancelar", "La solicitud ya fue disfrutada");
        }

        // 3. Cancelación válida
        PeriodoVacacionalEntity periodo = periodoRepository.findById(solicitud.getPeriodoId())
                .orElseThrow(() -> VacacionException.periodoNoEncontrado(solicitudId));

        periodoRepository.actualizarDisponibilidadad(periodo.getDiasRestantes() + 1, periodo.getId());

        solicitud.setEstatus(EstatusSolicitud.CANCELADA);
        repository.save(solicitud);

        historialCommandService.registrarEvento(solicitudId, EstatusSolicitud.CANCELADA.name(), usuarioId, "Solicitud de vacaciones cancelada por el usuario");
    }

    public void cancelarSolicitudDescansos(Long solicitudId, Integer usuarioId) {
        EmpleadoTiempoEntity solicitud = repository.findById(solicitudId)
                .orElseThrow(() -> VacacionException.solicitudNoEncontrada(solicitudId));

        if (solicitud.getTipo() == TipoSolicitud.DESCANSO && solicitud.getEstatus() == EstatusSolicitud.PENDIENTE) {
            repository.deleteById(solicitudId);
        } else {
            solicitud.setEstatus(EstatusSolicitud.CANCELADA);
            historialCommandService.registrarEvento(solicitudId, EstatusSolicitud.CANCELADA.name(), usuarioId, "Solicitud de descanso cancelada por el usuario");
        }


    }

    public void reactivar(Long solicitudId, Integer usuarioId) {
        EmpleadoTiempoEntity solicitud = repository.findById(solicitudId)
                .orElseThrow(() -> VacacionException.solicitudNoEncontrada(solicitudId));
        if (solicitud.getEstatus() == EstatusSolicitud.CANCELADA && solicitud.getFecha().isBefore(LocalDate.now())) {
            throw VacacionException.reactivacionRechazada("Cancelada", solicitud.getFecha());
        }
        solicitud.setEstatus(EstatusSolicitud.PENDIENTE);
        historialCommandService.registrarEvento(solicitudId, "PENDIENTE", usuarioId, "El usuario a reactivado la solicitud");
    }
}
