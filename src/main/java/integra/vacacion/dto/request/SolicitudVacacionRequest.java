package integra.vacacion.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record SolicitudVacacionRequest(
        @NotNull(message = "La fecha de inicio es requerida")
        LocalDate fechaInicio,

        @NotNull(message = "La fecha de fin es requerida")
        LocalDate fechaFin,

        String motivo
) {
}
