package integra.vacacion.dto.request;

import lombok.Data;
import lombok.Getter;

@Data
public class FiltroSolicitud {
    private String estatus;
    private Integer rrhhId;
    private Integer jefeId;
    private int currentPage = 0;
    private int pageSize = 10;
}
