package integra.vacacion.dto.response;

import integra.vacacion.domain.model.EstatusSolicitud;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PeriodoVacacional {
    private Long id;
    private Integer empleadoId;
    private Integer anioLaboral;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer diasHabilitados;
    private Integer diasTomados;
    private Integer diasRestantes;
    private LocalDate fechaCaducidad;
    private EstatusSolicitud estatus;
    private Integer periodoNumero;
    private Integer anioGestion;
}
