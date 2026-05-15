package integra.empleado.util;

import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Data
public class FiltroEmpleado {
    private Integer idSupervisor;
    private Integer idPuesto;
    private Integer idZona;
    private String clave;
    private Integer unidadId;
    private String estatus;
    private Integer idResponsable;
    private Boolean activos;
    private Integer id;
    private int page = 0;
    private int size = 20;

    public Pageable toPageable() {
        return PageRequest.of(page, size);
    }
}