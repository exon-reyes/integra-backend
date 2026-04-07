package integra.vacacion.domain.model;

import integra.model.Empleado;
import integra.vacacion.dto.response.PeriodoVacacional;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class DashboardSolicitudes {
    private Empleado empleado;
    private PeriodoVacacional periodoVacacional;
    private SolicitudesDescanso descansos;
    private SolicitudesVacaciones vacaciones;
    private LocalDate proximoAniversario;
}
