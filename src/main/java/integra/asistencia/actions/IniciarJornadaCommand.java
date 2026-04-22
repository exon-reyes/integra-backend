package integra.asistencia.actions;

import org.springframework.web.multipart.MultipartFile;
import java.time.LocalTime;

public record IniciarJornadaCommand(Integer empleadoId, MultipartFile foto, Integer unidadId, Integer unidadAsignadaId,
                                    LocalTime hora) {
}
