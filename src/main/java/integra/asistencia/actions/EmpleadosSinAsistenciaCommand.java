package integra.asistencia.actions;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EmpleadosSinAsistenciaCommand {
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer unidadId;
    private Integer puestoId;
    private Integer zonaId;
    private Integer supervisorId;
}
