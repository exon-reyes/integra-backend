package integra.config.security;

import integra.acceso.dto.UsuarioAcceso;
import integra.acceso.service.account.TokenVersionService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.*;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtil {

    private static final String CLAIM_VER = "ver";
    private static final String CLAIM_AUTHORITIES = "authorities";
    private static final String ISSUER = "integra-auth-server";
    private final TokenVersionService tokenVersionService;
    @Value("${security.jwt.private-key}")
    private RSAPrivateKey privateKey;
    @Value("${security.jwt.public-key}")
    private RSAPublicKey publicKey;
    @Value("${security.jwt.expiration}")
    private Long jwtExpirationInSeconds;

    // --- GENERACIÓN ---

    public String generateToken(UsuarioAcceso user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());

        Optional.ofNullable(user.getEsSupervisor()).ifPresent(s -> claims.put("sup", s));
        Optional.ofNullable(user.getEmpleadoId()).ifPresent(e -> claims.put("empleadoId", e));

        // Mapeo de authorities a lista de Strings para el JSON
        List<String> authList = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        claims.put(CLAIM_AUTHORITIES, authList);

        // Versión del token para invalidación dinámica
        claims.put(CLAIM_VER, tokenVersionService.getVersion(user.getUsername()));

        return createToken(claims, user.getUsername(), jwtExpirationInSeconds);
    }

    private String createToken(Map<String, Object> claims, String subject, Long expiration) {
        Date now = new Date();
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiration * 1000))
                .id(UUID.randomUUID().toString())
                .issuer(ISSUER)
                .signWith(privateKey)
                .compact();
    }

    // --- VALIDACIÓN Y EXTRACCIÓN ---

    /**
     * Valida firma, expiración y versión de token.
     * Retorna los Claims si es válido, lanza excepción si no.
     */
    public Claims validateAndExtract(String token) throws JwtException {
        Claims claims = extractAllClaims(token);

        if (isVersionDeprecated(claims)) {
            throw new JwtException("La versión del token ya no es válida.");
        }

        return claims;
    }

    private boolean isVersionDeprecated(Claims claims) {
        Integer tokenVersion = claims.get(CLAIM_VER, Integer.class);
        if (tokenVersion == null) return true;

        int currentVersion = tokenVersionService.getVersion(claims.getSubject());
        return tokenVersion < currentVersion;
    }

    /**
     * Convierte los claims del token directamente en un objeto de Autenticación de Spring.
     * Esto evita procesar el token múltiples veces en el filtro.
     */
    public Authentication getAuthentication(Claims claims) {
        List<?> rawAuthorities = claims.get(CLAIM_AUTHORITIES, List.class);

        List<SimpleGrantedAuthority> authorities = (rawAuthorities == null)
                ? Collections.emptyList()
                : rawAuthorities.stream()
                .map(String::valueOf)
                .map(SimpleGrantedAuthority::new)
                .toList();

        return new UsernamePasswordAuthenticationToken(claims.getSubject(), null, authorities);
    }

    // --- MÉTODOS BASE ---

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(publicKey)
                .requireIssuer(ISSUER)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T extractClaim(Claims claims, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(claims);
    }
}