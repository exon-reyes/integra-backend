package integra.vacacion.dto.response;

import java.time.LocalDate;

public record Festivo(
        Long id,
        LocalDate fecha,
        String nombre,
        Boolean activo
) {
}
