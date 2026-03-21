package integra.vacacion.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDate;
import java.util.Set;

public record ConfiguracionDescansoRequest(
        @NotEmpty(message = "Debe especificar al menos una fecha de descanso")
        Set<LocalDate> diasDescanso,
        String comentario
) {
}
