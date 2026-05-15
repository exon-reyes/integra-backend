package integra.vacacion.dto.response;

import java.time.LocalDate;

public record PeriodoGeneradoInfo(
        Integer empleadoId,
        String nombreEmpleado,
        LocalDate fechaIngreso,
        int aniosAntiguedad,
        int diasHabilitados,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        LocalDate fechaCaducidad
) {
}
