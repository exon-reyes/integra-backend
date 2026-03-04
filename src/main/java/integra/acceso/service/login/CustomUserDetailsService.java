package integra.acceso.service.login;

import integra.acceso.dto.UsuarioAcceso;
import integra.acceso.projection.InfoUserLogin;
import integra.acceso.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        InfoUserLogin user = userRepository.findByUsername(username, InfoUserLogin.class)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        UsuarioAcceso usuarioAcceso = new UsuarioAcceso(user.id(), user.username(), user.password(), user.activo());
        usuarioAcceso.setEmpleadoId(user.empleadoId());
        usuarioAcceso.setAuthorities(new HashSet<>());
        return usuarioAcceso;
    }
}