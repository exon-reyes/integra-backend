package integra.acceso.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

@Data
public class UsuarioAcceso implements UserDetails {
    private Long id;
    private String username;
    @JsonIgnore
    private String password;
    private String fullName;
    private Boolean activo;
    private Integer empleadoId;
    private String pin;
    private Boolean esSupervisor;
    private Integer tokenVersion;
    private Set<? extends GrantedAuthority> authorities;

    public UsuarioAcceso(Long id, String username, String password, Boolean activo) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.activo = activo;
    }

    @Override
    public Set<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isEnabled() {
        return this.activo;
    }
}
