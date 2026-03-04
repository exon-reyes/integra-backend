package integra.asistencia.actions;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActualizarPausaDTO {
    @NotNull
    private Integer pausaId;
    private LocalDateTime inicio; // Opcional: solo se actualiza si se proporciona
    private LocalDateTime fin; // Opcional: solo se actualiza si se proporciona
}
