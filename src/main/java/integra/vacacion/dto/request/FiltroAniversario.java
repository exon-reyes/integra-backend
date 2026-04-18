package integra.vacacion.dto.request;

import lombok.Data;

@Data
public class FiltroAniversario {
    private Integer responsableId;
    private Integer supervisorId;
    private int anio, mes;
}
