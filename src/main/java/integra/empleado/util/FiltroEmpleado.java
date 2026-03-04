package integra.empleado.util;

import lombok.Data;

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
}