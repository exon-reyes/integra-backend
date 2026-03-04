package integra.empleado.query;

import integra.empleado.entity.EmpleadoEntity;

import java.io.Serializable;

/**
 * DTO for {@link EmpleadoEntity}
 */
public record InfoBasicaResetUsuario(String email, String nombre) implements Serializable {
}