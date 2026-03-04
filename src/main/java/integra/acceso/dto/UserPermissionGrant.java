package integra.acceso.dto;

import java.util.Set;

public record UserPermissionGrant(Set<String> fromRoles, Set<String> special) {
}