package integra.acceso.dto;

import integra.model.Empleado;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JWTResponse {
    private String token;
    private Empleado employeeName;
    private List<String> uiPermissions;
}
