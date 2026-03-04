package integra.operatividad.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public record HorarioOperativoDto(
        @NotNull Integer idOperatividad,
        @NotNull LocalTime apertura,
        @NotNull LocalTime cierre,
        @NotNull Boolean activo) {
}
