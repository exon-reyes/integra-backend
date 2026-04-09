package integra.vacacion.dto.request;

import integra.vacacion.domain.model.EstatusSolicitud;
import lombok.Data;

@Data
public class FiltroSolicitud {
    private String estatus;
    private Integer rrhhId;
    private Integer supervisorId;
    private Integer responsableId;
    private Integer unidadId;
    private Integer empleadoId;
    private int currentPage = 0;
    private int pageSize = 10;
}
