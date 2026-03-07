package integra.vacacion.dto.response;

import java.time.LocalDate;

public record CalendarioEquipoDTO(
        Integer empleadoId,
        String nombreCompleto,
        String departamento,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        Integer diasLaborables,
        String estado
) {
}
