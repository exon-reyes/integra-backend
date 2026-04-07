package integra.vacacion.dto.response;

import integra.vacacion.domain.model.EstatusSolicitud;
import integra.vacacion.domain.model.TipoSolicitud;

public record SolicitudResumen(
        Long id,
        Long folioSolicitud,
        TipoSolicitud tipoSolicitud,
        EstatusSolicitud estatusGeneral,
        EstatusSolicitud estatusNivel1,
        EstatusSolicitud estatusNivel2,
        Integer diasSolicitados,
        Integer empleadoId,
        String empleadoCodigo,
        String empleadoNombre,
        String unidadNombre
) {
}
