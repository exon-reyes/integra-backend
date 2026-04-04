package integra.vacacion.service.command;

import integra.vacacion.core.EstatusPeriodo;
import integra.vacacion.domain.model.EstatusSolicitud;
import integra.vacacion.domain.model.TipoSolicitud;
import integra.vacacion.dto.request.NuevoEstatusSolicitud;
import integra.vacacion.exception.VacacionException;
import integra.vacacion.repository.EmpleadoTiempoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class GestiónSolicitudGranularService {
    private final EmpleadoTiempoEntityRepository solicitudRepository;
    private final VacacionHistorialService historialService;

    @Transactional
    public void actualizarEstatusSolicitud(NuevoEstatusSolicitud dictamen) {
        var solicitud = solicitudRepository.findById(dictamen.getIdSolicitud())
                .orElseThrow(() -> VacacionException.solicitudNoEncontrada(dictamen.getIdSolicitud()));

        Integer revisorId = dictamen.getEmpleadoId();
        int nivel = dictamen.getNivel();
        EstatusSolicitud nuevoEstatus = dictamen.getNuevoEstatus();
        EstatusSolicitud estatusGlobalAnterior = solicitud.getEstatus();

        // 1. Actualizar el campo del nivel correspondiente
        if (nivel == 1) {
            solicitud.setEstatusJefe(nuevoEstatus);
        } else if (nivel == 2) {
            solicitud.setEstatusRrhh(nuevoEstatus);
        }

        // 2. Derivar el estatus global a partir de la matriz N1 x N2
        solicitud.setEstatus(resolverEstatusGlobal(solicitud.getEstatusJefe(), solicitud.getEstatusRrhh()));

        EstatusSolicitud estatusGlobalNuevo = solicitud.getEstatus();

        // Registrar fecha de aprobación si corresponde
        if (estatusGlobalAnterior != EstatusSolicitud.APROBADA && estatusGlobalNuevo == EstatusSolicitud.APROBADA) {
            solicitud.setFechaAprobacion(LocalDateTime.now());
        }
        // Limpiar fecha si se degrada de APROBADA a PENDIENTE
        if (estatusGlobalAnterior == EstatusSolicitud.APROBADA && estatusGlobalNuevo == EstatusSolicitud.PENDIENTE) {
            solicitud.setFechaAprobacion(null);
        }

        // Registrar trazabilidad
        String rolRevisor = nivel == 1 ? "Jefe directo" : "Segundo Jefe (RRHH)";
        String comentarioHistorial = String.format("Estatus actualizado a %s por %s", nuevoEstatus.name(), rolRevisor);
        historialService.registrarEvento(solicitud.getId(), nuevoEstatus.name(), revisorId, comentarioHistorial);

        // Los descansos no afectan días del periodo vacacional
        if (!TipoSolicitud.VACACION.equals(solicitud.getTipo())) {
            return;
        }

        var periodo = solicitud.getPeriodo();
        if (periodo == null || estatusGlobalAnterior == estatusGlobalNuevo) {
            return;
        }

        /*
         * Ajuste de días según transición del estatus GLOBAL (solo VACACION):
         *
         *  activo  → CANCELADA : +1 diasRestantes, y si era APROBADA: -1 diasTomados
         *  CANCELADA → activo  : -1 diasRestantes, y si llega a APROBADA: +1 diasTomados
         *  activo  ↔ activo    : solo diasTomados cambia (±1) según cruce APROBADA
         */
        boolean anteriorCancelada = estatusGlobalAnterior == EstatusSolicitud.CANCELADA;
        boolean nuevoCancelada    = estatusGlobalNuevo    == EstatusSolicitud.CANCELADA;
        boolean nuevoAprobada     = estatusGlobalNuevo    == EstatusSolicitud.APROBADA;
        boolean anteriorAprobada  = estatusGlobalAnterior == EstatusSolicitud.APROBADA;

        if (!anteriorCancelada && nuevoCancelada) {
            // activo → CANCELADA: liberar el día
            periodo.setDiasRestantes(periodo.getDiasRestantes() + 1);
            if (periodo.getDiasRestantes() > 0 && periodo.getEstatus() == EstatusPeriodo.CONSUMIDO) {
                periodo.setEstatus(EstatusPeriodo.VIGENTE);
            }
            if (anteriorAprobada) {
                periodo.setDiasTomados(periodo.getDiasTomados() - 1);
            }

        } else if (anteriorCancelada && !nuevoCancelada) {
            // CANCELADA → activo: consumir el día de nuevo
            periodo.setDiasRestantes(periodo.getDiasRestantes() - 1);
            if (nuevoAprobada) {
                periodo.setDiasTomados(periodo.getDiasTomados() + 1);
            }

        } else {
            // activo ↔ activo (PENDIENTE ↔ APROBADA): solo diasTomados
            if (nuevoAprobada) {
                periodo.setDiasTomados(periodo.getDiasTomados() + 1);
            } else {
                periodo.setDiasTomados(periodo.getDiasTomados() - 1);
            }
        }
    }

    /**
     * Deriva el estatus global a partir de los estatusJefe (n1) y estatusRrhh (n2).
     *
     * Matriz N1 × N2:
     *   N2 = APROBADA  → APROBADA  (siempre)
     *   N2 = CANCELADA → CANCELADA (siempre)
     *   N2 = PENDIENTE:
     *     N1 = CANCELADA → CANCELADA
     *     cualquier otro → PENDIENTE
     */
    private EstatusSolicitud resolverEstatusGlobal(EstatusSolicitud n1, EstatusSolicitud n2) {
        return switch (n2) {
            case APROBADA  -> EstatusSolicitud.APROBADA;
            case CANCELADA -> EstatusSolicitud.CANCELADA;
            default        -> n1 == EstatusSolicitud.CANCELADA
                    ? EstatusSolicitud.CANCELADA
                    : EstatusSolicitud.PENDIENTE;
        };
    }
}
