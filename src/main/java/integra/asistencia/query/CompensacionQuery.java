package integra.asistencia.query;

import integra.empresa.entity.UnidadEntity;

import java.io.Serializable;
import java.time.LocalTime;

/**
 * DTO for {@link UnidadEntity}
 */
public record CompensacionQuery(Integer id, LocalTime tiempoCompensacion) implements Serializable {
}