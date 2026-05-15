package integra.asistencia.actions;


import integra.asistencia.util.TipoPausa;

import java.time.LocalTime;
import org.springframework.web.multipart.MultipartFile;

public record FinalizarPausaCommand(Integer empleadoId, TipoPausa pausa, MultipartFile foto, Integer unidadId,
                                    Integer unidadAsignadaId, LocalTime hora) {
}
