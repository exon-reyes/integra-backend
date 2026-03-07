package integra.vacacion.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record SolicitudVacacionDTO(
        Long id,
        Integer empleadoId,
        String nombreEmpleado,
        String departamento,
        String puesto,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        Integer diasSolicitados,
        String motivo,
        String estatus,
        String comentariosAprobador,
        Integer aprobadorId,
        String nombreAprobador,
        LocalDateTime fechaAprobacion,
        LocalDateTime createdAt
) {
}
