package integra.acceso.service.account;

import integra.acceso.entity.Role;
import integra.acceso.entity.User;
import integra.acceso.entity.account.AccountActivationToken;
import integra.acceso.exception.AccesoException;
import integra.acceso.repository.UserRepository;
import integra.acceso.repository.account.AccountRegistrationTokenRepository;
import integra.config.mail.EmailService;
import integra.core.service.ParamsDataProxy;
import integra.empleado.query.InfoNuevoUsuario;
import integra.empleado.repository.EmpleadoRepository;
import integra.global.exception.code.ErrorCode;
import integra.utils.FolioBase36;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountRegistrationService {

    private final EmpleadoRepository empleadoRepository;
    private final UserRepository userRepository;
    private final AccountRegistrationTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final ParamsDataProxy dataProxy;

    public String initiateRegistration(String codigoEmpleado) {
        InfoNuevoUsuario empleado = empleadoRepository.findByCodigoEmpleado(codigoEmpleado, InfoNuevoUsuario.class)
                .orElse(null);

        if (empleado == null || empleado.email() == null || empleado.email().isEmpty()) {
            return null;
        }

        if (userRepository.existsByEmpleadoId(empleado.id())) {
            throw AccesoException.duplicateUser(empleado.email());
        }

        // Envío asíncrono sin esperar
        enviarEmailRegistroAsync(empleado);

        return "Para finalizar el proceso, consulta tu correo " + empleado.email();
    }

    @Transactional
    public String registrarToken(Integer empleadoId) {
        String token = FolioBase36.generar();
        String tokenHash = hashToken(token);
        LocalDateTime now = LocalDateTime.now();

        // Operación atómica UPSERT
        tokenRepository.upsertToken(empleadoId, tokenHash, now.plusHours(24), now);

        return token;
    }

    public void enviarEmailRegistroAsync(InfoNuevoUsuario empleado) {
        String token = registrarToken(empleado.id());
        String link = "https://sci.ddns.me:4200/auth/register-confirm?token=" + token;

        String html = """
                <div style='background-color: #f1f5f9; padding: 50px 20px; font-family: -apple-system, "Segoe UI", Helvetica, Arial, sans-serif;'>
                  <div style='max-width: 480px; margin: 0 auto; background-color: #ffffff; border-top: 6px solid #0f172a; padding: 48px; box-shadow: 0 20px 25px -5px rgba(0,0,0,0.1); border-radius: 4px;'>
                    <div style='margin-bottom: 40px; border-left: 2px solid #0f172a; padding-left: 16px;'>
                      <div style='font-size: 14px; font-weight: 800; color: #0f172a; letter-spacing: 4px; text-transform: uppercase;'>INTEGRA</div>
                      <div style='font-size: 10px; color: #64748b; letter-spacing: 1px; margin-top: 4px; text-transform: uppercase;'>CREAR CUENTA DE ACCESO WEB</div>
                    </div>
                    <h2 style='font-size: 20px; font-weight: 700; color: #0f172a; margin-bottom: 20px; letter-spacing: -0.025em;'>Confirmación de registro</h2>
                    <p style='font-size: 14px; line-height: 1.6; color: #334155; margin-bottom: 32px;'>
                      Estimado/a <strong>%s</strong>, se ha generado una solicitud de alta en el sistema.
                      Para finalizar tu registro, configura tus credenciales de acceso en el siguiente enlace.
                    </p>
                    <div style='margin-bottom: 40px;'>
                      <a href='%s' style='background-color: #0f172a; color: #ffffff; display: block; text-align: center; padding: 18px; text-decoration: none; font-size: 12px; font-weight: 700; letter-spacing: 2px; text-transform: uppercase; border-radius: 2px;'>
                        Activar mi cuenta
                      </a>
                    </div>
                    <div style='margin-top: 32px; padding-top: 24px; border-top: 1px solid #f1f5f9;'>
                      <p style='font-size: 13px; color: #94a3b8; line-height: 1.6; margin: 0;'>
                        <span style='color: #d97706; margin-right: 4px;'>•</span>
                        Este enlace es válido por 24 horas.
                      </p>
                    </div>
                  </div>
                  <div style='max-width: 480px; margin: 30px auto 0; text-align: left;'>
                    <p style='font-size: 10px; color: #94a3b8; line-height: 1.5;'>
                      Esto es un mensaje automático. No responder. Integra © 2026
                    </p>
                  </div>
                </div>
                """.formatted(empleado.nombre(), link);

        try {
            emailService.sendHtmlEmail(empleado.email(), "Finalizar registro", html);
        } catch (Exception e) {
            log.error("Fallo al enviar correo de registro: {}", e.getMessage());
        }
    }

    public void validateToken(String token) {
        String tokenHash = hashToken(token);
        AccountActivationToken registrationToken = tokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(AccesoException::invalidResetToken);

        if (registrationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw AccesoException.invalidResetToken();
        }
    }

    @Transactional
    public void completeRegistration(String token, String username, String password) {
        String tokenHash = hashToken(token);
        AccountActivationToken registrationToken = tokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(AccesoException::invalidResetToken);

        if (registrationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw AccesoException.invalidResetToken();
        }

        if (userRepository.existsByUsername(username)) {
            throw AccesoException.duplicateUser(username);
        }

        if (userRepository.existsByEmpleadoId(registrationToken.getEmpleadoId())) {
            throw new AccesoException(ErrorCode.ACC_USER_DUPLICATE, "Este colaborador ya tiene una cuenta registrada");
        }
        tokenRepository.delete(registrationToken);
        var user = new User();
        user.setEmpleadoId(registrationToken.getEmpleadoId());
        user.setUsername(username);
        user.setActivo(true);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(Set.of(new Role(dataProxy.getIdRolDefault())));
        user.setCreatedAt(LocalDateTime.now());
        user.setRequierCambioPassword(false);
        try {
            userRepository.save(user);
        } catch (Exception e) {
            log.error("Error creando usuario", e);
            throw new AccesoException(ErrorCode.GEN_INTERNAL_ERROR, "No se pudo crear el usuario. Reintente más tarde");
        }
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing token", e);
        }
    }
}
