package integra.vacacion.dto.request;

import java.time.LocalDate;

public record FilaVacacionExcel(
        int numeroFila,
        String codigoEmpleado,
        int diasHabilitados,
        int diasDisfrutados,
        int diasAprobados,
        int diasDisponibles,
        LocalDate fechaRegistroExcel
) {}
