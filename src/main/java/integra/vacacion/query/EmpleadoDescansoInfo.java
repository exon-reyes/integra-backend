package integra.vacacion.query;

import java.io.Serializable;
import java.time.LocalDate;

public record EmpleadoDescansoInfo(Integer id, LocalDate fechaAlta, LocalDate fechaBaja, LocalDate fechaReingreso,
                                   String nombreCompleto) implements Serializable {
}