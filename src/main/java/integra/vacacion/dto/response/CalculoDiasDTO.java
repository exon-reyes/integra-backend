package integra.vacacion.dto.response;

import java.time.LocalDate;
import java.util.List;

public record CalculoDiasDTO(
        LocalDate fechaInicio,
        LocalDate fechaFin,
        Integer diasNaturales,
        Integer diasLaborables,
        Integer diasFestivosExcluidos,
        Integer diasDescansoExcluidos,
        Integer saldoDisponible,
        Boolean puedeSolicitar,
        List<LocalDate> diasFestivosEnRango,
        List<LocalDate> diasDescansoEnRango,
        String mensajeError
) {
}
