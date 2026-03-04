package integra.acceso.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ActualizarUsuarioCommand(@NotNull(message = "Se debe especificar un ID rol") Long id,
                                       @NotNull(message = "Se debe especificar un usuario") @NotBlank(message = "El usuario no debe estar vacío") String username,
                                       Integer empleadoId, List<Long> idRoles,String password) {
}
