package integra.acceso.command;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ActualizarPermisosRolRequest {
    @NotNull(message = "Debe especificar un ID del rol")
    private Long rolId;
    private List<String> permisosIds;

    public ActualizarPermisosRolRequest(Long rolId, List<String> permisosIds) {
        this.rolId = rolId;
        this.permisosIds = permisosIds;
    }
}