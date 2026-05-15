package integra.vacacion.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class FiltroSolicitud {
    private String estatus;
    private Integer rrhhId;
    private Integer supervisorId;
    private Integer responsableId;
    private Integer unidadId;
    private Integer empleadoId;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private int currentPage = 0;
    private int pageSize = 50;
}
