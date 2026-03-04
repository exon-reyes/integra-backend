package integra.acceso.projection;

import java.io.Serializable;

/**
 * DTO for {@link integra.acceso.entity.User}
 */
public record InfoUserLogin(Long id, Integer empleadoId, String username, String password,
                            Boolean activo) implements Serializable {
}