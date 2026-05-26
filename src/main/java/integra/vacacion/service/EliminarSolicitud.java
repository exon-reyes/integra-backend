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
        var periodo = solicitud.getPeriodo();
        if (solicitud.getTipoSolicitud() == TipoSolicitud.VACACION) {
            solicitud.getDiasSolicitudDescansos().forEach(d -> {
                EstatusSolicitud n2 = d.getEstatusNivel2();
                // Devolver diasRestantes por cada día que no estaba cancelado
                // (los cancelados ya fueron devueltos cuando se cancelaron)
                if (n2 != EstatusSolicitud.CANCELADA) {
                    periodo.setDiasRestantes(periodo.getDiasRestantes() + 1);
                }
                // Decrementar diasTomados si el día estaba aprobado
                if (n2 == EstatusSolicitud.APROBADA && periodo.getDiasTomados() > 0) {
                    periodo.setDiasTomados(periodo.getDiasTomados() - 1);
                }
            });
        }
        solicitudRepository.deleteById(id);
    }
}
