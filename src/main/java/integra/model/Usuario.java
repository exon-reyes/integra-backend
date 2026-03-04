package integra.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class Usuario {
    private Long id;
    private String username;
    private String password;
    private Set<Rol> roles;
    private Set<String> permisosEspeciales;
    private Boolean activo;
    private Empleado empleado;

    public Usuario(Long id, Set<String> permisosEspeciales, Set<Rol> roles) {
        this.id = id;
        this.permisosEspeciales = permisosEspeciales;
        this.roles = roles;
    }

    public Usuario(Long id, String username, Boolean activo) {
        this.id = id;
        this.username = username;
        this.activo = activo;
    }
}
