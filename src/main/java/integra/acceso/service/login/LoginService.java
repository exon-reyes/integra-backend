package integra.acceso.service.login;

import integra.acceso.dto.JWTResponse;
import integra.acceso.dto.UsuarioAcceso;
import integra.acceso.projection.InfoLoginEmpleado;
import integra.acceso.repository.UserRepository;
import integra.acceso.request.AccesoRequest;
import integra.acceso.service.uinode.AuthorityGenerator;
import integra.config.security.JwtUtil;
import integra.model.Empleado;
import integra.utils.JsonParseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoginService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final AuthorityGenerator authorityService;
    private final JwtUtil jwtUtil;

    public JWTResponse login(AccesoRequest request) {
        Authentication authentication = authenticate(request);
        UsuarioAcceso user = (UsuarioAcceso) authentication.getPrincipal();

        InfoLoginEmpleado empleadoData = getEmpleadoData(user.getUsername());

        AuthorityGenerator.AuthorityResult authorities = buildUserAuthorities(empleadoData);

        user.setAuthorities(new HashSet<>(authorities.tokenAuthorities()));
        String token = jwtUtil.generateToken(user);

        Empleado empleado = new Empleado(empleadoData.getEmpleado_id(), empleadoData.getNombre_completo());
        empleado.setAvatar(empleadoData.getAvatar());

        return new JWTResponse(token, empleado, authorities.uiPermissions().stream().toList());
    }

    private Authentication authenticate(AccesoRequest request) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
    }

    private InfoLoginEmpleado getEmpleadoData(String username) {
        return userRepository.findInfoLoginByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Empleado no encontrado"));
    }

    private AuthorityGenerator.AuthorityResult buildUserAuthorities(InfoLoginEmpleado empleadoData) {
        List<String> permissions = JsonParseUtil.parseJsonArray(empleadoData.getPermisos_json());
        List<String> roleIds = JsonParseUtil.parseJsonArray(empleadoData.getRoles_json());

        return authorityService.buildAuthorities(permissions, roleIds);
    }
}
