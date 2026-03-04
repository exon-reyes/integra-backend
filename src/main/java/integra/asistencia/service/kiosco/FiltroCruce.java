package integra.asistencia.service.kiosco;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class FiltroCruce {
    private Integer empleadoId;
    private Integer unidadRegistroId;
    private Integer unidadEsperadaId;
    private Integer asistenciaId;
    private LocalDateTime desde;
    private LocalDateTime hasta;
}
