package integra.vacacion.service.command;

import integra.vacacion.core.EstatusPeriodo;
import integra.vacacion.domain.model.EstatusSolicitud;
import integra.vacacion.domain.model.TipoSolicitud;
import integra.vacacion.dto.request.NuevoEstatusSolicitud;
import integra.vacacion.repository.EmpleadoTiempoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class GestionSolicitudesCommandService {
    private final VacacionHistorialService historialService;
    private final EmpleadoTiempoEntityRepository solicitudRepository;

    @Transactional
    public void actualizarBloqueSolicitudes(NuevoEstatusSolicitud dictamen) {
        var solicitudes = solicitudRepository.findAllByFolio(dictamen.getFolioSolicitud());
        if (solicitudes.isEmpty()) {
            return;
        }
        Integer revisorId = dictamen.getEmpleadoId();
        int nivel = dictamen.getNivel();

        for (var solicitud : solicitudes) {
            EstatusSolicitud estatusGlobalAnterior = solicitud.getEstatus();
            EstatusSolicitud nuevoEstatus = dictamen.getNuevoEstatus();

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
            historialService.registrarEvento(solicitud.getId(), dictamen.getNuevoEstatus().name(), revisorId, comentarioHistorial);

            // Los descansos no afectan días del periodo vacacional
            if (!TipoSolicitud.VACACION.equals(solicitud.getTipo())) {
                continue;
            }

            var periodo = solicitud.getPeriodo();
            if (periodo == null) {
                continue;
            }

            /*
             * Ajuste de días según transición del estatus GLOBAL (solo VACACION):
             *
             * La frontera clave es CANCELADA vs. activo (PENDIENTE/APROBADA).
             *
             *  activo  → CANCELADA : +1 diasRestantes, y si era APROBADA: -1 diasTomados
             *  CANCELADA → activo  : -1 diasRestantes, y si llega a APROBADA: +1 diasTomados
             *  activo  ↔ activo    : solo diasTomados cambia (±1) según cruce APROBADA
             *  X → X  (sin cambio) : sin efecto
             */
            if (estatusGlobalAnterior == estatusGlobalNuevo) {
                continue; // Sin cambio global: nada que ajustar
            }

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
                    // Era aprobada: revertir día tomado
                    periodo.setDiasTomados(periodo.getDiasTomados() - 1);
                }

            } else if (anteriorCancelada && !nuevoCancelada) {
                // CANCELADA → activo: consumir el día de nuevo
                periodo.setDiasRestantes(periodo.getDiasRestantes() - 1);
                if (nuevoAprobada) {
                    // Llega a aprobada: registrar como tomado
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
