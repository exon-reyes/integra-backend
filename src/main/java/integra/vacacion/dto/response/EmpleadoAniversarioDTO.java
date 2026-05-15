package integra.vacacion.dto.response;

import java.time.LocalDate;

public record EmpleadoAniversarioDTO(
        Integer id,
        String codigoEmpleado,
        String nombreCompleto,
        LocalDate fechaIngreso,
        LocalDate fechaReingreso,
        int aniosCumplidos,
        LocalDate proximoAniversario,
        int mesAniversario,
        String unidad,
        String puesto
) {
}
