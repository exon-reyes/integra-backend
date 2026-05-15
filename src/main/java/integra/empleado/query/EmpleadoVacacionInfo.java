package integra.empleado.query;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link integra.empleado.entity.EmpleadoEntity}
 */
public record EmpleadoVacacionInfo(Integer id, LocalDate fechaAlta, LocalDate fechaReingreso,
                                   LocalDate fechaBaja) implements Serializable {
}