package integra.empleado.query;


import java.io.Serializable;

/**
 * DTO para consulta query de  {@link integra.empleado.entity.EmpleadoResponsable}
 * Información básica del empleado
 */
public record InfoBasicaEmpleadoResponsable(Integer empleadoId, String empleadoCodigoEmpleado,
                                            String empleadoNombreCompleto) implements Serializable {
}