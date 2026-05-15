package integra.vacacion.service;

import integra.vacacion.domain.model.EstatusSolicitud;
import integra.vacacion.domain.model.TipoSolicitud;
import integra.vacacion.exception.VacacionException;
import integra.vacacion.repository.SolicitudDescansoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EliminarSolicitud {
    private final SolicitudDescansoRepository solicitudRepository;

    public void eliminarSolicitud(Long id) {
        var solicitud = solicitudRepository.findById(id).orElseThrow(() -> VacacionException.solicitudNoEncontrada(id));
        if (solicitud.getEstatus() == EstatusSolicitud.APROBADA) {
            throw VacacionException.estadoInvalido("eliminar", solicitud.getEstatus().name());
        }
        var periodo=solicitud.getPeriodo();
        if(solicitud.getTipoSolicitud() == TipoSolicitud.VACACION){
            periodo.setDiasRestantes(periodo.getDiasRestantes() + solicitud.getDiasSolicitados());
        }
        solicitudRepository.deleteById(id);
    }
}
