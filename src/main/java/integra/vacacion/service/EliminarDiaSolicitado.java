package integra.vacacion.service;

import integra.vacacion.domain.model.EstatusSolicitud;
import integra.vacacion.domain.model.TipoSolicitud;
import integra.vacacion.exception.VacacionException;
import integra.vacacion.repository.DiasSolicitudRepository;
import integra.vacacion.repository.SolicitudDescansoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EliminarDiaSolicitado {
    private final SolicitudDescansoRepository solicitudRepository;
    private final DiasSolicitudRepository diasSolicitudRepository;

    public void eliminar(Long solicitudId, Integer usuarioId, TipoSolicitud tipoSolicitud) {
        var diaSolicitado = diasSolicitudRepository.findById(solicitudId)
                .orElseThrow(() -> VacacionException.solicitudNoEncontrada(solicitudId));
        var solicitud = diaSolicitado.getFolio();
        
        // 1. Validar que el día solicitado esté en PENDIENTE
        if (diaSolicitado.getEstatusNivel1() != EstatusSolicitud.PENDIENTE || diaSolicitado.getEstatusNivel2() != EstatusSolicitud.PENDIENTE) {
            throw VacacionException.estadoInvalido("eliminar", "El día seleccionado ya ha sido procesado o no está pendiente.");
        }

        // 2. Validar que la solicitud global (dependencia) esté en PENDIENTE en todos sus niveles
        if (solicitud.getEstatusNivel1() != EstatusSolicitud.PENDIENTE || solicitud.getEstatusNivel2() != EstatusSolicitud.PENDIENTE || solicitud.getEstatus() != EstatusSolicitud.PENDIENTE) {
            throw VacacionException.estadoInvalido("eliminar", "La solicitud ya ha sido procesada o está en curso a nivel global.");
        }

        // 3. Regresar el día tomado al periodo (Solo si son Vacaciones)
        var periodo = solicitud.getPeriodo();
        if (periodo != null && tipoSolicitud == TipoSolicitud.VACACION) {
            periodo.setDiasRestantes(periodo.getDiasRestantes() + 1);
            if (periodo.getDiasTomados() != null && periodo.getDiasTomados() > 0) {
                periodo.setDiasTomados(periodo.getDiasTomados() - 1);
            }
        }

        // 4. Lógica de eliminación dependiendo del número de días
        if (solicitud.getDiasSolicitados() == 1) {
            solicitudRepository.delete(solicitud);
            // La entidad día será borrada en cascada gracias al orphanRemoval pero por seguridad se fuerza el delete
            diasSolicitudRepository.delete(diaSolicitado);
        } else if (solicitud.getDiasSolicitados() > 1) {
            solicitud.setDiasSolicitados(solicitud.getDiasSolicitados() - 1);
            // IMPORTANTE: Debemos remover el elemento de la colección de la solicitud si hay cascade
            if (solicitud.getDiasSolicitudDescansos() != null) {
                solicitud.getDiasSolicitudDescansos().remove(diaSolicitado);
            }
            diasSolicitudRepository.delete(diaSolicitado);
        }
    }
}