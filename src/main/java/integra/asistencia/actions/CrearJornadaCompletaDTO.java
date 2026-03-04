package integra.asistencia.actions;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CrearJornadaCompletaDTO {
    @NotNull
    private Integer empleadoId;

    @NotNull
    private LocalDateTime inicioJornada;

    @NotNull
    private LocalDateTime finJornada;

    private String comentario;

    @NotNull
    private Integer unidadId;
}
