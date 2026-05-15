package integra.empleado.query;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link integra.empleado.entity.EmpleadoEntity}
 */
public record EmpleadoAniversarioInfo(Integer id, String codigoEmpleado, String puestoNombre, LocalDate fechaAlta,
                                      String unidadNombreCompleto, LocalDate fechaReingreso,
                                      String nombreCompleto, Integer jefeId,
                                      Integer supervisorUnidadId) implements Serializable {
}