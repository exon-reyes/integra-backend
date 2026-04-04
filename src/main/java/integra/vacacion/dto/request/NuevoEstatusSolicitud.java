package integra.vacacion.dto.request;

import integra.vacacion.domain.model.EstatusSolicitud;
import integra.vacacion.domain.model.TipoSolicitud;
import lombok.Data;

@Data
public class NuevoEstatusSolicitud {
    private Integer empleadoId;
    private Long folioSolicitud;
    private Long idSolicitud;
    private EstatusSolicitud nuevoEstatus;
    private TipoSolicitud tipoSolicitud;
    private int nivel;
}
