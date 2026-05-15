package integra.vacacion.dto.request;

import lombok.Data;

@Data
public class FiltroPeriodo {
    private Integer empleadoId;
    private Integer unidadId;
    private Integer supervisorId;
    private Integer responsableId;
    private String estatus;
    private Integer anioLaboral;
    private int currentPage = 0;
    private int pageSize = 10;
}
