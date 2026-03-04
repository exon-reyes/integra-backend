package integra.empleado.query;

import integra.empleado.entity.EmpleadoEntity;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link EmpleadoEntity}
 */
public record InfoCatalogoEmpleados(Integer id, String codigoEmpleado, Integer puestoId, String puestoNombre,
                                    String estatus,
                                    String nombreCompleto, Integer unidadId, String unidadNombreCompleto,
                                    LocalDate fechaAlta,
                                    LocalDate fechaBaja, LocalDate fechaReingreso) implements Serializable {
}