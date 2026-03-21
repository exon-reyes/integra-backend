package integra.vacacion.dto.response;

import java.time.LocalDate;
import java.util.List;

public record DescansoAprobacionDTO(
        Integer empleadoId,
        String nombreEmpleado,
        List<LocalDate> fechasPendientes
) {
}
