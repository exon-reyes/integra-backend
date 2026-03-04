package integra.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import integra.acceso.service.rol.RoleService;
import integra.model.Rol;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {
    private static final ObjectMapper mapper = new ObjectMapper();
    private final JwtUtil jwtUtil;
    private final RoleService roleService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        try {
            // Este método ahora retorna los Claims, evitando re-parsear el token después.
            io.jsonwebtoken.Claims claims = jwtUtil.validateAndExtract(token);
            String username = claims.getSubject();

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 2. Extraemos autoridades del token (Strings)
                List<String> rawAuthorities = claims.get("authorities", List.class);

                // 3. Expansión de permisos (Roles -> Permisos)
                Set<SimpleGrantedAuthority> authorities = expandPermissions(rawAuthorities);

                // 4. Creación de la autenticación
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        claims.getSubject(), null, authorities);

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

        } catch (io.jsonwebtoken.JwtException e) {
            // Captura Expired, Malformed, Signature y nuestra excepción de Versión Deprecada
            log.warn("Fallo en validación de JWT: {}", e.getMessage());
            sendUnauthorizedResponse(response, e.getMessage());
            return;
        } catch (Exception e) {
            log.error("Error crítico de seguridad: ", e);
            SecurityContextHolder.clearContext();
            sendUnauthorizedResponse(response, "Error interno en la autenticación.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private Set<SimpleGrantedAuthority> expandPermissions(List<String> rawAuthorities) {
        if (rawAuthorities == null || rawAuthorities.isEmpty()) return Set.of();

        // Estimamos el tamaño inicial para evitar resize del HashSet
        Set<SimpleGrantedAuthority> expanded = new HashSet<>(rawAuthorities.size() * 2);

        for (String auth : rawAuthorities) {
            if (auth.startsWith("ROLE_")) {
                try {
                    // Extraemos ID de forma eficiente
                    Long rolId = Long.parseLong(auth.substring(5));
                    Rol rol = roleService.obtenerRolPorId(rolId);

                    if (rol != null && rol.getPermisos() != null) {
                        for (var p : rol.getPermisos()) {
                            expanded.add(new SimpleGrantedAuthority(p.getId())); // O p.getId()
                        }
                    }
                } catch (NumberFormatException e) {
                    log.warn("Formato de ROL inválido: {}", auth);
                }
            } else {
                expanded.add(new SimpleGrantedAuthority(auth));
            }
        }
        return expanded;
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> error = Map.of(
                "success", false,
                "message", message,
                "timestamp", System.currentTimeMillis()
        );

        response.getWriter().write(mapper.writeValueAsString(error));
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/auth/") || path.startsWith("/public/") || path.startsWith("/actuator/");
    }
}