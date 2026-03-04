package integra.asistencia.actions;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActualizarJornadaDTO {
    @NotNull
    private Integer jornadaId;
    private LocalDateTime inicioJornada; // Opcional: solo se actualiza si se proporciona
    private LocalDateTime finJornada; // Opcional: solo se actualiza si se proporciona
    private String comentario; // Opcional: solo se actualiza si se proporciona
}
