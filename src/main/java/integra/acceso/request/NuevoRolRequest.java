package integra.acceso.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NuevoRolRequest(
        @NotBlank(message = "El nombre es requerido") @NotNull(message = "El nombre es requerido") String nombre,
        String descripcion, Boolean activo, Boolean esDefault) {
}
