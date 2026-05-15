package integra.vacacion.dto.request;

import integra.vacacion.domain.model.EstatusSolicitud;
import integra.vacacion.domain.model.TipoSolicitud;
import lombok.Data;

@Data
public class NuevoEstatusSolicitud {
    private Integer empleadoId;
    private Long folioSolicitud;
    private Long id;
    private EstatusSolicitud nuevoEstatus;
    private TipoSolicitud tipoSolicitud;
    private int nivel;
    private java.util.List<Long> diasIds;
}
