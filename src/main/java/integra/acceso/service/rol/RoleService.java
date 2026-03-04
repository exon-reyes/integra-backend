package integra.acceso.service.rol;

import integra.acceso.command.ActualizarPermisosRolRequest;
import integra.acceso.command.ActualizarRolRequest;
import integra.acceso.entity.Role;
import integra.acceso.projection.InfoRolBasico;
import integra.acceso.repository.RoleRepository;
import integra.acceso.request.NuevoRolRequest;
import integra.model.Permiso;
import integra.model.Rol;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleService {
    private final RoleRepository roleRepository;
    private final InvalidarTokensRolService tokensRolService;


    @Cacheable(value = "rolPorId", key = "#id", unless = "#result == null")
    public Rol obtenerRolPorId(Long id) {
        return roleRepository.findById(id) // Query optimizada con JOIN FETCH
                .map(this::toDto).orElse(null);
    }

    public List<Rol> obtenerCatalagoRoles() {
        return roleRepository.findBy(InfoRolBasico.class)
                .stream()
                .map(rol -> new Rol(rol.getId(), rol.getName(), rol.getDescription(), rol.isActivo(), rol.getIsDefault(), rol.getVersion()))
                .toList();
    }

    @Transactional
    @CacheEvict(value = "rolPorId", key = "#permisos.rolId")
    public void actualizarPermisos(ActualizarPermisosRolRequest permisos) {

        Role role = roleRepository.getReferenceById(permisos.getRolId());
        role.setPermissionIds(new HashSet<>(permisos.getPermisosIds()));
        role.setVersion(role.getVersion() + 1);
        roleRepository.save(role);
        tokensRolService.invalidarTokensPorRol(permisos.getRolId());

    }

    private Rol toDto(Role role) {
        Rol dto = new Rol(role.getId(), role.getName(), role.getDescription());
        dto.setPermisos(role.getPermissionIds().stream().map(Permiso::new).collect(Collectors.toSet()));
        return dto;
    }

    @Transactional
    @CacheEvict(value = "rolPorId", key = "#rol.rolId")
    public void actualizar(@Valid ActualizarRolRequest rol) {
        roleRepository.updateNameAndDescriptionById(rol.nombre(), rol.descripcion(), rol.rolId());
    }

    @Transactional
    public Rol agregarRol(NuevoRolRequest command) {
        var role = new Role();
        role.setName(command.nombre());
        role.setDescription(command.descripcion());
        if (command.esDefault() != null && command.esDefault()) {
            role.setDefault(true);
        }
        if (command.activo() != null && command.activo()) {
            role.setActivo(true);
        }
        role.setVersion(1L);
        var result = roleRepository.save(role);
        return new Rol(result.getId(), result.getName(), result.getDescription(), result.isActivo(), result.isDefault(), result.getVersion());
    }

    @Transactional
    @CacheEvict(value = "rolPorId", key = "#id")
    public void eliminarRol(Long id) {
        roleRepository.deleteById(id);
    }
}
