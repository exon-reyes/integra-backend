package integra.vacacion.dto.response;

import java.time.LocalDate;

public record FestivoDTO(
        Long id,
        LocalDate fecha,
        String nombre,
        Boolean activo
) {
}
