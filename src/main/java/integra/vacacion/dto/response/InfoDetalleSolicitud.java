package integra.vacacion.dto.response;

import integra.vacacion.domain.model.EstatusSolicitud;
import integra.vacacion.domain.model.TipoSolicitud;

import java.time.LocalDate;

public record InfoDetalleSolicitud(
        Long registroId,
        LocalDate fecha,
        EstatusSolicitud estatus,
        EstatusSolicitud estatusJefe,
        EstatusSolicitud estatusRrhh,
        String comentario,
        TipoSolicitud tipo,
        Integer empleadoId,
        String codigoEmpleado,
        String nombreCompleto,
        String unidadNombre,
        Integer primerJefeId,
        String primerJefeNombre,
        Integer segundoJefeId,
        String segundoJefeNombre,
        Integer diasHabilitados,
        Integer diasTomados,
        Integer anioGestion
) {
}
