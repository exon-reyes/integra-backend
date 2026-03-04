package integra.acceso.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ActualizarRolRequest(@NotNull(message = "Debe especificar un ID del rol") Long rolId,
                                   @NotNull(message = "Debe especificar un nombre de rol") @NotBlank(message = "El nombre del rol no puede estar vacío") String nombre,
                                   @NotNull(message = "Debe especificar una descripción") String descripcion) {
}
