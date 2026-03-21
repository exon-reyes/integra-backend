package integra.vacacion.dto.request;

import integra.vacacion.domain.model.TipoSolicitud;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDate;
import java.util.Set;

public record SolicitudVacacionRequest(
        @NotEmpty(message = "Debe especificar al menos una fecha de vacaciones") Set<LocalDate> diasSeleccionados,
        Integer usuarioId,
        String motivo, TipoSolicitud tipoSolicitud) {
}
