//package integra.vacacion.service.command;
//
//import integra.empleado.entity.EmpleadoEntity;
//import integra.empleado.service.ConsultarCatalogoEmpleados;
//import integra.utils.FolioGenerator;
//import integra.vacacion.domain.model.DashboardSolicitudes;
//import integra.vacacion.domain.model.EstatusSolicitud;
//import integra.vacacion.domain.model.TipoSolicitud;
//import integra.vacacion.dto.request.SolicitudVacacionRequest;
//import integra.vacacion.dto.response.Festivo;
//import integra.vacacion.entity.EmpleadoTiempoEntity;
//import integra.vacacion.entity.PeriodoVacacionalEntity;
//import integra.vacacion.exception.VacacionException;
//import integra.vacacion.repository.EmpleadoTiempoEntityRepository;
//import integra.vacacion.repository.PeriodoVacacionalRepository;
//import integra.vacacion.service.query.CalendarioFestivoService;
//import integra.vacacion.service.query.DashboardService;
//import integra.vacacion.service.validation.SolicitudValidatorService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Set;
//
//
//@RequiredArgsConstructor
//@Service
//public class SolicitudCommandService {
//    private final CalendarioFestivoService calendarioFestivoService;
//    private final EmpleadoTiempoEntityRepository repository;
//    private final ConsultarCatalogoEmpleados catalogoEmpleados;
//    private final DashboardService dashboardService;
//    private final SolicitudValidatorService validatorService;
//    private final PeriodoVacacionalRepository periodoRepository;
//    private final VacacionHistorialService historialCommandService;
//
//    @Transactional
//    public void solicitarVacaciones(Integer empleadoId, SolicitudVacacionRequest request) {
//        DashboardSolicitudes dashboard = dashboardService.obtenerDashboard(empleadoId);
//        validatorService.validarAntiguedadMinima(dashboard.getEmpleado());
//
//        List<Festivo> festivosEnRango = calendarioFestivoService.obtenerFestivos(LocalDate.now()
//                .getYear(), dashboard.getProximoAniversario().getYear());
//
//        Set<LocalDate> fechasDisponibles = validatorService.filtrarCruceSolicitudVacaciones(request.diasSeleccionados(), dashboard, festivosEnRango);
//
//        if (fechasDisponibles.isEmpty()) return;
//
//        LocalDate hoy = LocalDate.now();
//
//        long folioSolicitud = FolioGenerator.generar(empleadoId, 0);
//
//
//        List<EmpleadoTiempoEntity> solicitudes = fechasDisponibles.stream().map(fecha -> {
//            EmpleadoTiempoEntity entity = new EmpleadoTiempoEntity();
//            entity.setEmpleado(new EmpleadoEntity(empleadoId));
//            entity.setFecha(fecha);
//            entity.setActivo(true);
//            entity.setEstatusJefe(EstatusSolicitud.PENDIENTE);
//            entity.setEstatusRrhh(EstatusSolicitud.PENDIENTE);
//            entity.setComentario(request.motivo());
//            entity.setTipo(TipoSolicitud.VACACION);
//            entity.setFolio(folioSolicitud);
//            entity.setPeriodo(new PeriodoVacacionalEntity(dashboard.getPeriodoVacacional().getId()));
//            entity.setEstatus(EstatusSolicitud.PENDIENTE);
//            return entity;
//        }).toList();
//
//        repository.saveAll(solicitudes);
//
//        // 2. Actualización atómica (Evita saldos inconsistentes)
//        int nuevosRestantes = dashboard.getPeriodoVacacional().getDiasRestantes() - fechasDisponibles.size();
//        if (nuevosRestantes < 0) {
//            throw VacacionException.saldoInsuficiente(dashboard.getPeriodoVacacional()
//                    .getDiasRestantes(), fechasDisponibles.size());
//        }
//
//        periodoRepository.actualizarDisponibilidadad(nuevosRestantes, dashboard.getPeriodoVacacional().getId());
//    }
//
//    @Transactional
//    public void solicitarDescansos(Integer empleadoId, Set<LocalDate> diasSolicitados) {
//        DashboardSolicitudes dashboard = dashboardService.obtenerDashboard(empleadoId);
//
//        Set<LocalDate> fechasDisponibles = validatorService.filtrarCruceSolicitudVacaciones(diasSolicitados, dashboard, null);
//
//        if (fechasDisponibles.isEmpty()) {
//            if (diasSolicitados.size() == 1) {
//                throw VacacionException.fechaInvalida("La fecha seleccionada ya se encuentra registrada en una solicitud previa.");
//            } else {
//                throw VacacionException.fechaInvalida("Todas las fechas seleccionadas coinciden con solicitudes previas.");
//            }
//        }
//
//        Long periodoId = dashboard.getPeriodoVacacional() != null ? dashboard.getPeriodoVacacional().getId() : null;
//        long folioSolicitud = FolioGenerator.generar(empleadoId, 1);
//        fechasDisponibles.forEach(localDate -> {
//            EmpleadoTiempoEntity entity = new EmpleadoTiempoEntity();
//            entity.setEmpleado(new EmpleadoEntity(empleadoId));
//            entity.setFecha(localDate);
//            entity.setActivo(true);
//            entity.setComentario("Solicitud de descanso");
//            entity.setTipo(TipoSolicitud.DESCANSO);
//            entity.setFolio(folioSolicitud);
//            entity.setEstatusJefe(EstatusSolicitud.PENDIENTE);
//            entity.setEstatusRrhh(EstatusSolicitud.PENDIENTE);
//            entity.setPeriodo(new PeriodoVacacionalEntity(periodoId));
//            entity.setEstatus(EstatusSolicitud.PENDIENTE);
//            repository.save(entity);
//        });
//    }
//
//    @Transactional
//    public void cancelarSolicitudVacaciones(Long solicitudId, Integer usuarioId) {
//        EmpleadoTiempoEntity solicitud = repository.findById(solicitudId)
//                .orElseThrow(() -> VacacionException.solicitudNoEncontrada(solicitudId));
//
//        // Ya está cancelada — no hay nada que hacer
//        if (solicitud.getEstatus() == EstatusSolicitud.CANCELADA) {
//            throw VacacionException.estadoInvalido("cancelar", "La solicitud ya se encuentra cancelada");
//        }
//
//        // Nivel 2 (RRHH) aprobó — el usuario no puede cancelar, debe contactar a RRHH
//        if (solicitud.getEstatusRrhh() == EstatusSolicitud.APROBADA) {
//            throw VacacionException.cancelacionBloqueadaPorRrhh();
//        }
//
//        // Nivel 1 (jefe directo) aprobó — el usuario no puede cancelar, debe notificar a su jefe
//        if (solicitud.getEstatusJefe() == EstatusSolicitud.APROBADA) {
//            throw VacacionException.cancelacionBloqueadaPorJefe();
//        }
//
//        PeriodoVacacionalEntity periodo = periodoRepository.findById(solicitud.getPeriodo().getId())
//                .orElseThrow(() -> VacacionException.periodoNoEncontrado(solicitudId));
//
//        // Restaurar día al periodo vacacional
//        periodoRepository.actualizarDisponibilidadad(periodo.getDiasRestantes() + 1, periodo.getId());
//        solicitud.setEstatus(EstatusSolicitud.CANCELADA);
//        historialCommandService.registrarEvento(solicitudId, EstatusSolicitud.CANCELADA.name(), usuarioId, "Solicitud cancelada por el empleado");
//    }
//
//    @Transactional
//    public void cancelarSolicitudDescansos(Long solicitudId, Integer usuarioId) {
//        EmpleadoTiempoEntity solicitud = repository.findById(solicitudId)
//                .orElseThrow(() -> VacacionException.solicitudNoEncontrada(solicitudId));
//
//        if (solicitud.getTipo() == TipoSolicitud.DESCANSO && solicitud.getEstatus() == EstatusSolicitud.PENDIENTE) {
//            repository.deleteById(solicitudId);
//        } else {
//            solicitud.setEstatus(EstatusSolicitud.CANCELADA);
//            historialCommandService.registrarEvento(solicitudId, EstatusSolicitud.CANCELADA.name(), usuarioId, "Solicitud de descanso cancelada por el usuario");
//        }
//
//
//    }
//
//    @Transactional
//    public void reactivar(Long solicitudId, Integer usuarioId) {
//        EmpleadoTiempoEntity solicitud = repository.findById(solicitudId)
//                .orElseThrow(() -> VacacionException.solicitudNoEncontrada(solicitudId));
//        if (solicitud.getEstatus() == EstatusSolicitud.CANCELADA && solicitud.getFecha().isBefore(LocalDate.now())) {
//            throw VacacionException.reactivacionRechazada("Cancelada", solicitud.getFecha());
//        }
//        solicitud.setEstatus(EstatusSolicitud.PENDIENTE);
//        solicitud.getPeriodo().setDiasRestantes(solicitud.getPeriodo().getDiasRestantes() - 1);
//        historialCommandService.registrarEvento(solicitudId, "PENDIENTE", usuarioId, "El usuario a reactivado la solicitud");
//    }
//}
