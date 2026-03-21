//package integra.vacacion.service.command;
//
//import integra.empleado.repository.EmpleadoRepository;
//import integra.vacacion.domain.model.EstatusSolicitud;
//import integra.vacacion.domain.service.AuditoriaVacacionService;
//import integra.vacacion.domain.service.CalculoDiasLaboralesService;
//import integra.vacacion.domain.service.GestionSaldoVacacionService;
//import integra.vacacion.domain.service.ValidacionSolicitudService;
//import integra.vacacion.dto.request.SolicitudVacacionRequest;
//import integra.vacacion.dto.response.CalculoDiasDTO;
//import integra.vacacion.dto.response.SolicitudVacacionDTO;
//import integra.vacacion.entity.PeriodoVacacionalEntity;
//import integra.vacacion.entity.SolicitudVacacionEntity;
//import integra.vacacion.exception.VacacionException;
//import integra.vacacion.repository.PeriodoVacacionalRepository;
//import integra.vacacion.repository.SolicitudVacacionRepository;
//import integra.vacacion.service.SolicitudVacacionMapper;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//
//@Service
//@RequiredArgsConstructor
//public class VacacionCommandService {
//
//    private final SolicitudVacacionRepository solicitudRepository;
//    private final PeriodoVacacionalRepository periodoVacacionalRepository;
//    private final EmpleadoRepository empleadoRepository;
//    private final ValidacionSolicitudService validacionService;
//    private final CalculoDiasLaboralesService calculoService;
//    private final GestionSaldoVacacionService gestionSaldoService;
//    private final AuditoriaVacacionService auditoriaService;
//    private final SolicitudVacacionMapper mapper;
//
/// /    @Transactional
/// /    public SolicitudVacacionDTO crearSolicitud(Integer empleadoId, SolicitudVacacionRequest request) {
/// /        validacionService.validarSolicitud(empleadoId, request.fechaInicio(), request.fechaFin());
/// /        PeriodoVacacionalEntity ultimoPeriodo = periodoVacacionalRepository.findByEmpleadoIdAndEstatus(empleadoId, PeriodoVacacionalEntity.EstatusPeriodo.VIGENTE).getFirst();
/// /        int diasSolicitados = calculoService.calcularDiasLaborables(request.fechaInicio(), request.fechaFin(), empleadoId);
/// /
/// /        SolicitudVacacionEntity solicitud = new SolicitudVacacionEntity();
/// /        solicitud.setEmpleadoId(empleadoId);
/// /        solicitud.setFechaInicio(request.fechaInicio());
/// /        solicitud.setFechaFin(request.fechaFin());
/// /        solicitud.setDiasSolicitados(diasSolicitados);
/// /        solicitud.setComentario(request.motivo());
/// /        solicitud.setEstatus(EstatusSolicitud.PENDIENTE);
/// /        solicitud.setPeriodoId(ultimoPeriodo.getId());
/// /
/// /        solicitud = solicitudRepository.save(solicitud);
/// /
/// /        // Descontar saldo al poner en PENDIENTE => bloquea nuevas solicitudes mientras se valida
/// /        gestionSaldoService.descontarDiasDePeriodos(empleadoId, diasSolicitados);
/// /
/// /        auditoriaService.registrar(solicitud.getId(), EstatusSolicitud.CREADA, empleadoId, "Solicitud creada");
/// /
/// /        return mapper.toDTO(solicitud);
/// /    }
//
//    @Transactional
//    public SolicitudVacacionDTO aprobarSolicitud(Long solicitudId, Integer aprobadorId) {
//        SolicitudVacacionEntity solicitud = obtenerSolicitud(solicitudId);
//
//        if (solicitud.getEstatus() != EstatusSolicitud.PENDIENTE) {
//            throw VacacionException.estadoInvalido("aprobar", solicitud.getEstatus().name());
//        }
//
//        solicitud.setEstatus(EstatusSolicitud.APROBADA);
//        solicitud.setAprobadorId(aprobadorId);
//        solicitud.setFechaAprobacion(LocalDateTime.now());
//
//        // El saldo ya fue descontado al crear la solicitud PENDIENTE,
//        // aquí solo se confirma el estado (no se vuelve a descontar).
//
//        solicitud = solicitudRepository.save(solicitud);
//
//        auditoriaService.registrar(solicitudId, EstatusSolicitud.APROBADA, aprobadorId, "Solicitud aprobada");
//
//        return mapper.toDTO(solicitud);
//    }
//
//    @Transactional
//    public SolicitudVacacionDTO rechazarSolicitud(Long solicitudId, Integer aprobadorId, String comentarios) {
//        SolicitudVacacionEntity solicitud = obtenerSolicitud(solicitudId);
//
//        if (solicitud.getEstatus() != EstatusSolicitud.PENDIENTE) {
//            throw VacacionException.estadoInvalido("rechazar", solicitud.getEstatus().name());
//        }
//
//        solicitud.setEstatus(EstatusSolicitud.RECHAZADA);
//        solicitud.setComentariosAprobador(comentarios);
//        solicitud.setAprobadorId(aprobadorId);
//        solicitud.setFechaAprobacion(LocalDateTime.now());
//
//        // Restaurar el saldo que se descontó al crear la solicitud
//        gestionSaldoService.restaurarDiasDePeriodos(solicitud.getEmpleadoId(), solicitud.getDiasSolicitados());
//
//        solicitud = solicitudRepository.save(solicitud);
//
//        auditoriaService.registrar(solicitudId, EstatusSolicitud.RECHAZADA, aprobadorId, "Solicitud rechazada: " + comentarios);
//
//        return mapper.toDTO(solicitud);
//    }
//
//    @Transactional
//    public void cancelarSolicitud(Long solicitudId, Integer empleadoId) {
//        SolicitudVacacionEntity solicitud = obtenerSolicitud(solicitudId);
//
//        if (!solicitud.getEmpleadoId().equals(empleadoId)) {
//            throw VacacionException.sinPermisos("cancelar esta solicitud");
//        }
//
//        if (solicitud.getEstatus() != EstatusSolicitud.PENDIENTE) {
//            throw VacacionException.estadoInvalido("cancelar", solicitud.getEstatus().name());
//        }
//
//        solicitud.setEstatus(EstatusSolicitud.CANCELADA);
//        solicitudRepository.save(solicitud);
//
//        // Restaurar el saldo que se descontó al crear la solicitud
//        gestionSaldoService.restaurarDiasDePeriodos(empleadoId, solicitud.getDiasSolicitados());
//
//        auditoriaService.registrar(solicitudId, EstatusSolicitud.CANCELADA, empleadoId, "Solicitud cancelada por el empleado");
//    }
//
//    public CalculoDiasDTO preCalcular(Integer empleadoId, LocalDate inicio, LocalDate fin) {
//        int saldo = gestionSaldoService.calcularSaldoTotal(empleadoId);
//        return calculoService.calcular(inicio, fin, empleadoId, saldo);
//    }
//
//    private SolicitudVacacionEntity obtenerSolicitud(Long id) {
//        return solicitudRepository.findById(id).orElseThrow(() -> VacacionException.solicitudNoEncontrada(id));
//    }
//}
