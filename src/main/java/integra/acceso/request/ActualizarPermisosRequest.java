package integra.acceso.request;

import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record ActualizarPermisosRequest(@NotNull(message = "Se debe especificar un ID de usuario") Long id,
                                        Set<String> permisos) {
}
