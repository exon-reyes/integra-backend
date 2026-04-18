package integra.vacacion.dto.response;

import java.time.LocalDate;

public record PeriodoCerradoInfo(
        Integer empleadoId,
        String nombreEmpleado,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        LocalDate fechaCaducidad,
        int diasRestantes
) {}
