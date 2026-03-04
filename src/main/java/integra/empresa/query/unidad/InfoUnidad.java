package integra.empresa.query.unidad;

import integra.empresa.entity.UnidadEntity;

import java.io.Serializable;

/**
 * DTO for {@link UnidadEntity}
 */
public record InfoUnidad(Integer id, String clave, String nombreCompleto, Boolean activo, Integer zonaId, String zonaNombre,
                         Integer supervisorId, String supervisorNombreCompleto) implements Serializable {
}