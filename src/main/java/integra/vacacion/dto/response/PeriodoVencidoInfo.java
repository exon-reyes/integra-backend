package integra.vacacion.dto.response;

import java.time.LocalDate;

public record PeriodoVencidoInfo(
        Integer empleadoId,
        String nombreEmpleado,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        int diasRestantes
) {}
