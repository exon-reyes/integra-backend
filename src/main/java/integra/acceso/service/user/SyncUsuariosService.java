package integra.acceso.service.user;

import integra.acceso.dto.SyncUsuariosResponse;
import integra.acceso.entity.Role;
import integra.acceso.entity.User;
import integra.acceso.repository.UserRepository;
import integra.core.service.ParamsDataProxy;
import integra.empleado.constants.EmpleadoEstatus;
import integra.empleado.entity.EmpleadoEntity;
import integra.empleado.repository.EmpleadoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyncUsuariosService {

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int PASSWORD_LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final EmpleadoRepository empleadoRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ParamsDataProxy paramsDataProxy;
    private final SyncUsuariosExcelService excelService;
    private final SyncUsuariosTxtService txtService;

    @Transactional
    public SyncUsuariosResponse sincronizarUsuarios() {
        List<EmpleadoEntity> activos = empleadoRepository.findByEstatusNot(EmpleadoEstatus.BAJA);

        List<Integer> ids = activos.stream().map(EmpleadoEntity::getId).toList();
        Set<Integer> conUsuario = userRepository.findByEmpleadoIdIn(ids)
                .stream()
                .map(User::getEmpleadoId)
                .collect(Collectors.toSet());

        List<EmpleadoEntity> sinUsuario = activos.stream()
                .filter(e -> !conUsuario.contains(e.getId()))
                .toList();

        Long rolId = paramsDataProxy.getIdRolDefault();
        List<SyncUsuariosExcelService.FilaUsuario> filas = new ArrayList<>();

        for (EmpleadoEntity empleado : sinUsuario) {
            try {
                String username = empleado.getCodigoEmpleado().toLowerCase();
                String rawPassword = generarPassword();

                User user = new User();
                user.setUsername(username);
                user.setPassword(passwordEncoder.encode(rawPassword));
                user.setEmpleadoId(empleado.getId());
                user.setActivo(true);
                user.setEmail(empleado.getEmail() != null ? empleado.getEmail() : "");
                user.setCreatedAt(LocalDateTime.now());
                user.setRequierCambioPassword(true);
                if (rolId != null) {
                    user.setRoles(Set.of(new Role(rolId)));
                }

                userRepository.save(user);
                filas.add(new SyncUsuariosExcelService.FilaUsuario(
                        empleado.getCodigoEmpleado(),
                        empleado.getNombreCompleto(),
                        username,
                        rawPassword,
                        empleado.getEmail() != null ? empleado.getEmail() : ""
                ));
                log.info("[SyncUsuarios] Usuario creado: {} (empleado {})", username, empleado.getId());
            } catch (Exception e) {
                log.error("[SyncUsuarios] Error al crear usuario para empleado {}: {}", empleado.getId(), e.getMessage());
            }
        }

        String archivoTxt = filas.isEmpty() ? null : txtService.generarTxt(filas);
        String archivoExcel = filas.isEmpty() ? null : excelService.generarExcel(filas);

        return new SyncUsuariosResponse(
                activos.size(),
                filas.size(),
                conUsuario.size(),
                archivoTxt // Devolvemos la url del txt como solicitó, o podríamos devolver ambas
        );
    }

    private String generarPassword() {
        StringBuilder sb = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            sb.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }
        return sb.toString();
    }
}
