package integra.acceso.service.uinode;

import integra.acceso.service.rol.RoleService;
import integra.model.Rol;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthorityGenerator {

    private final RoleService roleService;

    public AuthorityResult buildAuthorities(List<String> permissions, List<String> roleIds) {
        Set<SimpleGrantedAuthority> tokenAuthorities = new HashSet<>();
        Set<String> uiPermissions = new HashSet<>();

        // Procesar permisos especiales
        permissions.forEach(permission -> {
            tokenAuthorities.add(new SimpleGrantedAuthority(permission));
            uiPermissions.add(permission);
        });

        // Procesar roles
        roleIds.forEach(roleIdStr -> {
            Long roleId = Long.valueOf(roleIdStr);
            Rol rol = roleService.obtenerRolPorId(roleId);

            if (rol != null) {
                tokenAuthorities.add(new SimpleGrantedAuthority("ROLE_" + rol.getId()));
                rol.getPermisos().forEach(p -> uiPermissions.add(p.getId()));
            }
        });

        return new AuthorityResult(tokenAuthorities, uiPermissions);
    }

    public record AuthorityResult(Set<SimpleGrantedAuthority> tokenAuthorities, Set<String> uiPermissions) {
    }
}