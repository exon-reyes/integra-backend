package integra.empleado.query;

import java.io.Serializable;

/**
 * DTO for {@link integra.empleado.entity.EmpleadoEntity}
 */
public record EmpleadoAsignacionInfo(Integer id, String codigoEmpleado, String puestoNombre,
                                     String unidadNombreCompleto, String nombreCompleto,
                                     String jefeNombreCompleto, String segundoJefeNombreCompleto) implements Serializable {
}