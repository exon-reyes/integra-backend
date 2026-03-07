package integra.vacacion.domain.service;

import integra.vacacion.entity.VacacionAuditoriaEntity;
import integra.vacacion.entity.VacacionAuditoriaEntity.AccionAuditoria;
import integra.vacacion.repository.VacacionAuditoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditoriaVacacionService {

    private final VacacionAuditoriaRepository auditoriaRepository;

    public void registrar(Long solicitudId, AccionAuditoria accion, Integer usuarioId, String detalles) {
        VacacionAuditoriaEntity auditoria = new VacacionAuditoriaEntity();
        auditoria.setSolicitudId(solicitudId);
        auditoria.setAccion(accion);
        auditoria.setUsuarioId(usuarioId);
        auditoria.setDetalles(detalles);
        auditoriaRepository.save(auditoria);
    }
}
