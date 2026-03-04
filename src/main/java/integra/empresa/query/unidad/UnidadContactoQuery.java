package integra.empresa.query.unidad;

import integra.empresa.entity.UnidadEntity;

import java.io.Serializable;

/**
 * DTO for {@link UnidadEntity}
 */

public record UnidadContactoQuery(Integer id, String clave,String nombre, String email, String nombreCompleto,
                                  String direccion, String localizacion, Boolean activo, String telefono,
                                  Integer zonaId, String zonaNombre, Integer estadoId,
                                  String estadoNombre, String supervisorNombreCompleto) implements Serializable {
}