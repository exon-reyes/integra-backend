package integra.asistencia.actions;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CrearPausaCompletaDTO {
    @NotNull
    private Integer empleadoId;

    @NotNull
    private LocalDateTime inicio;

    @NotNull
    private LocalDateTime fin;

    @NotNull
    private String tipoPausa;

    @NotNull
    private Integer asistenciaId;

    @NotNull
    private Integer unidadId;
}
