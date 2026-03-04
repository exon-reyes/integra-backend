package integra.asistencia.query;

import integra.empresa.entity.UnidadEntity;

import java.io.Serializable;

/**
 * DTO for {@link UnidadEntity}
 */
public record UnidadKioscoIncidencia(String nombreCompleto) implements Serializable {
}