package integra.vacacion.dto.response;

import java.time.LocalDateTime;

public record HistorialSolicitud(
        Long id,
        Long empleadoTiempoId,
        String tipoEvento,
        LocalDateTime fechaEvento,
        Integer usuarioId,
        String comentario
) {
}
