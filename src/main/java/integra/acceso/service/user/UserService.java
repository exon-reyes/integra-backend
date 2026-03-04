package integra.acceso.service.user;

import integra.acceso.command.ActualizarUsuarioCommand;
import integra.acceso.dto.UserPermissionGrant;
import integra.acceso.entity.Role;
import integra.acceso.entity.User;
import integra.acceso.exception.AccesoException;
import integra.acceso.projection.PermissionProjection;
import integra.acceso.projection.UsuarioBasicoDTO;
import integra.acceso.repository.UserRepository;
import integra.acceso.request.ActualizarPermisosRequest;
import integra.acceso.request.CreateUserRequest;
import integra.core.service.ParamsDataProxy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final ParamsDataProxy systemIdProvider;
    private final PasswordEncoder passwordEncoder;

    public Page<UsuarioBasicoDTO> obtenerUsuarios(Pageable pageable) {
        return userRepository.obtenerUsuariosRaw(pageable);
    }

    @Transactional
    public void eliminarUsuario(Long id) {
        if (id.equals(systemIdProvider.getIdUsuarioAdmin())) {
            System.out.println("Usuario admin");
            throw AccesoException.accessDenied("No se puede eliminar el usuario administrador");
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public void actualizarEstatus(Long idUsuario, Boolean newStatus) {
        userRepository.updateActivoById(newStatus, idUsuario);
    }

    @Transactional
    public void actualizarUsuario(ActualizarUsuarioCommand command) {
        User user = userRepository.getReferenceById(command.id());

        if (userRepository.existsByIdNotAndUsername(command.id(), command.username())) {
            throw AccesoException.duplicateUser(command.username());
        }
        user.setUsername(command.username());
        user.setEmpleadoId(command.empleadoId());
        if(command.password()!=null&&!command.password().isBlank()){
            user.setPassword(passwordEncoder.encode(command.password()));
        }
        if (!command.idRoles().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            for (Long rolId : command.idRoles()) {
                Role role = new Role(rolId);
                roles.add(role);
            }
            user.setRoles(roles);
        }
        userRepository.save(user);
    }

    public UserPermissionGrant obtenerPermisos(Long idUsuario) {

        List<PermissionProjection> results = userRepository.findAllPermissionsRaw(idUsuario);

        Set<String> rolePerms = new HashSet<>();
        Set<String> specialPerms = new HashSet<>();

        for (PermissionProjection row : results) {
            if ("ROL".equals(row.getOrigen())) {
                rolePerms.add(row.getPermissionId());
            } else {
                specialPerms.add(row.getPermissionId());
            }
        }

        return new UserPermissionGrant(rolePerms, specialPerms);
    }

    @Transactional
    public void ActualizarPermisos(ActualizarPermisosRequest command) {

        User user = userRepository.getReferenceById(command.id());
        user.setDirectPermissionIds(null);
        user.setDirectPermissionIds(command.permisos());
        userRepository.save(user);
    }

    @Transactional
    public void crearUsuario(CreateUserRequest command) {
        if (userRepository.existsByUsername(command.getUsername())) {
            throw AccesoException.duplicateUser(command.getUsername());
        }
        var user = new User();
        user.setUsername(command.getUsername());
        user.setPassword(passwordEncoder.encode(command.getPassword()));
        user.setEmpleadoId(command.getIdEmpleado());
        user.setActivo(true);
        user.setEmail("");
        user.setCreatedAt(LocalDateTime.now());
        user.setRequierCambioPassword(false);
        Set<Role> roles = new HashSet<>();
        command.getRoles().forEach(rolId -> {
            Role role = new Role(rolId);
            roles.add(role);
        });
        user.setRoles(roles);

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            log.error("Violación de integridad al registrar el usuario: {}", ex.getMessage(), ex);
            String message = ex.getMostSpecificCause().getMessage();
            if (message == null) throw ex;
            if (message.contains("idx_users_empleado") && message.contains("Duplicate entry")) {
                throw AccesoException.duplicateUserByMessage("El colaborador ya se encuentra asociado a un usuario");
            }
            throw ex;
        }
    }
}