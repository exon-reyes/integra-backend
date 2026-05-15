package integra.asistencia.actions;

import org.springframework.web.multipart.MultipartFile;
import java.time.LocalTime;

public record FinalizarJornadaCommand(Integer empleadoId, MultipartFile foto, Integer unidadId, Boolean finDeposito,
                                      Integer unidadAsignadaId, LocalTime hora) {
}
