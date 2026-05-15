package integra.vacacion.dto.response;

import integra.vacacion.core.EstatusPeriodo;

import java.time.LocalDate;

public record PeriodoVacacionalResumen(
        Long id,
        String codigoEmpleado,
        String nombreEmpleado,
        String puesto,
        String unidad,
        Integer anioLaboral,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        Integer diasHabilitados,
        Integer diasTomados,
        EstatusPeriodo estatus
) {
}
