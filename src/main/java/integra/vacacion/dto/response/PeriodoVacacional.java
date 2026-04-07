package integra.vacacion.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PeriodoVacacional {
    private Long id;
    private Integer anioLaboral;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer diasHabilitados;
    private Integer diasTomados;
    private Integer diasRestantes;
    private LocalDate fechaCaducidad;
    private String estatus;
    private Integer anioGestion;
}
