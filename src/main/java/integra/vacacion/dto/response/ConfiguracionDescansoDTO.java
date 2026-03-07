package integra.vacacion.dto.response;

import java.time.LocalDate;
import java.util.Set;

public record ConfiguracionDescansoDTO(
        Integer empleadoId,
        Set<LocalDate> diasDescanso,
        boolean configurado
) {
}
