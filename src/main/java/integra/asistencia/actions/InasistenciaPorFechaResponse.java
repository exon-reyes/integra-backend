package integra.asistencia.actions;
import java.time.LocalDate;
import java.util.List;
public record InasistenciaPorFechaResponse(
        LocalDate fecha,
        List<EmpleadoSinAsistenciaResponse> empleados
) {
}