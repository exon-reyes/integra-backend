package integra.vacacion.service.command;

import integra.empleado.repository.EmpleadoRepository;
import integra.vacacion.domain.service.AuditoriaVacacionService;
import integra.vacacion.domain.service.CalculoDiasLaboralesService;
import integra.vacacion.domain.service.GestionSaldoVacacionService;
import integra.vacacion.domain.service.ValidacionSolicitudService;
import integra.vacacion.dto.request.SolicitudVacacionRequest;
import integra.vacacion.dto.response.CalculoDiasDTO;
import integra.vacacion.dto.response.SolicitudVacacionDTO;
import integra.vacacion.entity.SolicitudVacacionEntity;
import integra.vacacion.entity.SolicitudVacacionEntity.EstatusSolicitud;
import integra.vacacion.entity.VacacionAuditoriaEntity.AccionAuditoria;
import integra.vacacion.exception.VacacionException;
import integra.vacacion.repository.PeriodoVacacionalRepository;
import integra.vacacion.repository.SolicitudVacacionRepository;
import integra.vacacion.service.SolicitudVacacionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VacacionCommandService {

    private final SolicitudVacacionRepository solicitudRepository;
    private final PeriodoVacacionalRepository periodoVacacionalRepository;
    private final EmpleadoRepository empleadoRepository;
    private final ValidacionSolicitudService validacionService;
    private final CalculoDiasLaboralesService calculoService;
    private final GestionSaldoVacacionService gestionSaldoService;
    private final AuditoriaVacacionService auditoriaService;
    private final SolicitudVacacionMapper mapper;

    @Transactional
    public SolicitudVacacionDTO crearSolicitud(Integer empleadoId, SolicitudVacacionRequest request) {
        validacionService.validarSolicitud(empleadoId, request.fechaInicio(), request.fechaFin());

        int diasSolicitados = calculoService.calcularDiasLaborables(request.fechaInicio(), request.fechaFin(), empleadoId);

        SolicitudVacacionEntity solicitud = new SolicitudVacacionEntity();
        solicitud.setEmpleadoId(empleadoId);
        solicitud.setFechaInicio(request.fechaInicio());
        solicitud.setFechaFin(request.fechaFin());
        solicitud.setDiasSolicitados(diasSolicitados);
        solicitud.setMotivo(request.motivo());
        solicitud.setEstatus(EstatusSolicitud.PENDIENTE);

        solicitud = solicitudRepository.save(solicitud);

        auditoriaService.registrar(solicitud.getId(), AccionAuditoria.CREADA, empleadoId, "Solicitud creada");

        return mapper.toDTO(solicitud);
    }

    @Transactional
    public SolicitudVacacionDTO aprobarSolicitud(Long solicitudId, Integer aprobadorId) {
        SolicitudVacacionEntity solicitud = obtenerSolicitud(solicitudId);

        if (solicitud.getEstatus() != EstatusSolicitud.PENDIENTE) {
            throw VacacionException.estadoInvalido("aprobar", solicitud.getEstatus().name());
        }

        solicitud.setEstatus(EstatusSolicitud.APROBADA);
        solicitud.setAprobadorId(aprobadorId);
        solicitud.setFechaAprobacion(LocalDateTime.now());

        gestionSaldoService.descontarDiasDePeriodos(solicitud.getEmpleadoId(), solicitud.getDiasSolicitados());

        solicitud = solicitudRepository.save(solicitud);

        auditoriaService.registrar(solicitudId, AccionAuditoria.APROBADA, aprobadorId, "Solicitud aprobada");

        return mapper.toDTO(solicitud);
    }

    @Transactional
    public SolicitudVacacionDTO rechazarSolicitud(Long solicitudId, Integer aprobadorId, String comentarios) {
        SolicitudVacacionEntity solicitud = obtenerSolicitud(solicitudId);

        if (solicitud.getEstatus() != EstatusSolicitud.PENDIENTE) {
            throw VacacionException.estadoInvalido("rechazar", solicitud.getEstatus().name());
        }

        solicitud.setEstatus(EstatusSolicitud.RECHAZADA);
        solicitud.setComentariosAprobador(comentarios);
        solicitud.setAprobadorId(aprobadorId);
        solicitud.setFechaAprobacion(LocalDateTime.now());

        solicitud = solicitudRepository.save(solicitud);

        auditoriaService.registrar(solicitudId, AccionAuditoria.RECHAZADA, aprobadorId, "Solicitud rechazada: " + comentarios);

        return mapper.toDTO(solicitud);
    }

    @Transactional
    public void cancelarSolicitud(Long solicitudId, Integer empleadoId) {
        SolicitudVacacionEntity solicitud = obtenerSolicitud(solicitudId);

        if (!solicitud.getEmpleadoId().equals(empleadoId)) {
            throw VacacionException.sinPermisos("cancelar esta solicitud");
        }

        if (solicitud.getEstatus() != EstatusSolicitud.PENDIENTE) {
            throw VacacionException.estadoInvalido("cancelar", solicitud.getEstatus().name());
        }

        solicitud.setEstatus(EstatusSolicitud.CANCELADA);
        solicitudRepository.save(solicitud);

        auditoriaService.registrar(solicitudId, AccionAuditoria.CANCELADA, empleadoId, "Solicitud cancelada por el empleado");
    }

    public CalculoDiasDTO preCalcular(Integer empleadoId, LocalDate inicio, LocalDate fin) {
        int saldo = gestionSaldoService.calcularSaldoTotal(empleadoId);
        return calculoService.calcular(inicio, fin, empleadoId, saldo);
    }

    private SolicitudVacacionEntity obtenerSolicitud(Long id) {
        return solicitudRepository.findById(id).orElseThrow(() -> VacacionException.solicitudNoEncontrada(id));
    }
}
