package integra.vacacion.dto.response;

import integra.vacacion.domain.model.EstatusSolicitud;
import integra.vacacion.domain.model.TipoSolicitud;

import java.time.LocalDate;

public record InfoSolicitudGestion(
        Long id,
        Integer empleadoId,
        String codigoEmpleado,
        String nombreCompleto,
        String unidadNombre,
        Long folio,
        LocalDate fecha,
        EstatusSolicitud estatus,
        EstatusSolicitud estatusJefe,
        EstatusSolicitud estatusRrhh,
        TipoSolicitud tipo
) {
}
