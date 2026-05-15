package integra.asistencia.actions;


import integra.asistencia.util.TipoPausa;

import java.time.LocalTime;
import org.springframework.web.multipart.MultipartFile;

public record IniciarPausaCommand(Integer empleadoId, TipoPausa tipo, MultipartFile foto, Integer unidadId,
                                  Integer unidadAsignadaId, LocalTime hora) {
}
