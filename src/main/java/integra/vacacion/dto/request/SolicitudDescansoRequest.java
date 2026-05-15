package integra.vacacion.dto.request;

import integra.vacacion.domain.model.TipoSolicitud;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDate;
import java.util.Set;

public record SolicitudDescansoRequest(
        @NotEmpty(message = "Debe especificar al menos una fecha en la solicitud") Set<LocalDate> diasSeleccionados,
        Integer usuarioId,
        String motivo, TipoSolicitud tipoSolicitud) {
}
