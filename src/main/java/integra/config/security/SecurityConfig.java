package integra.config.security;

import integra.acceso.service.login.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomUserDetailsService userDetailsService;
    private final JwtRequestFilter jwtRequestFilter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    // ===============================
    // 🔐 Beans de seguridad
    // ===============================
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Ajusta la fuerza según tu entorno
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        // Constructor moderno evita deprecated
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // ===============================
    // 🔒 Filtro y reglas de seguridad
    // ===============================
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 🔓 Endpoints públicos
                        .requestMatchers("/auth/login", "/auth/refresh", "/auth/forgot-password",
                                "/auth/validate-reset-token", "/auth/reset-password", "/auth/register-request",
                                "/auth/validate-registration-token", "/auth/register-confirm", "/auth/public/**")
                        .permitAll()
                        .requestMatchers("/estados/**")
                        .permitAll()
                        .requestMatchers("/asistencia/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/unidades/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/empleados/*/avatar/imagen")
                        .permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html")
                        .permitAll()
                        // Públicos específicos de kioscos
                        .requestMatchers(HttpMethod.GET, "/kioscos", "/kioscos/*")
                        .permitAll()
                        .requestMatchers(HttpMethod.POST, "/kioscos/*/codigos/*/usar")
                        .permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/kioscos/*/requiere-codigo")
                        .permitAll()
                        // 🔒 Los demás de kioscos requieren autenticación
                        .requestMatchers("/kioscos/**")
                        .authenticated()
                        // 🔒 Gestión de usuarios protegida
                        .requestMatchers("/auth/register", "/auth/user/**")
                        .authenticated()
                        .anyRequest()
                        .authenticated())
                // Configura nuestro provider moderno
                .authenticationProvider(authenticationProvider())
                // Manejo personalizado para requests sin token
                .exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint))
                // JWT antes de UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}