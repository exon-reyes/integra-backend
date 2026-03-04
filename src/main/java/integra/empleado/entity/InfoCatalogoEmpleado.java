package integra.empleado.entity;

import java.io.Serializable;
import java.time.LocalDate;

public record InfoCatalogoEmpleado(Integer empleadoId, String empleadoCodigoEmpleado, String empleadoPuestoNombre,
                                   Integer empleadoPuestoId, String empleadoNombreCompleto, LocalDate empleadoFechaBaja,
                                   Integer empleadoUnidadId, String empleadoUnidadNombreCompleto,
                                   LocalDate empleadoFechaAlta, String empleadoEstatus,
                                   LocalDate empleadoFechaReingreso) implements Serializable {
}