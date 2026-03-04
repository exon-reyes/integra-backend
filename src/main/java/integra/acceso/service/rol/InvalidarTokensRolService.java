package integra.acceso.service.rol;


import integra.acceso.repository.UserRepository;
import integra.acceso.service.account.TokenVersionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class InvalidarTokensRolService {
    private final UserRepository userRepository;
    private final TokenVersionService tokenVersionService;

    public void invalidarTokensPorRol(Long rolId) {
        // Buscar todos los usuarios que tienen este rol
        var usuarios = userRepository.findUsernamesByRoleId(rolId);

        // Incrementar versión de token para cada usuario
        usuarios.forEach(tokenVersionService::incrementVersion);
    }
}