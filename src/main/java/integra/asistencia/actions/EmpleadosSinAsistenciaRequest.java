package integra.asistencia.actions;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EmpleadosSinAsistenciaRequest {

    @NotNull(message = "La fecha de inicio es requerida")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es requerida")
    private LocalDate fechaFin;

    private Integer unidadId;
    private Integer puestoId;
    private Integer zonaId;
    private Integer supervisorId;
}
