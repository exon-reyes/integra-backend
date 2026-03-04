package integra.acceso.service.account;

import integra.acceso.command.PasswordReset;
import integra.acceso.entity.User;
import integra.acceso.entity.account.PasswordResetToken;
import integra.acceso.exception.AccesoException;
import integra.acceso.repository.UserRepository;
import integra.acceso.repository.account.PasswordResetTokenRepository;
import integra.acceso.util.TokenGenerator;
import integra.empleado.query.InfoBasicaResetUsuario;
import integra.empleado.repository.EmpleadoRepository;
import integra.global.exception.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InitiatePasswordResetUseCase {

    private final EmpleadoRepository empleadoRepository;
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final TokenGenerator tokenGenerator;
    private final PasswordResetNotifier notifier;

    @Transactional
    public void execute(PasswordReset request) {
        // 1. Identificación
        User user = validarUsuario(request.username());
        InfoBasicaResetUsuario employee = obtenerEmpleado(user.getEmpleadoId());

        // 2. Seguridad: Límite de intentos por día
        validarLimiteIntentos(user.getId());

        String plainToken = tokenGenerator.generate(); // UUID o similar
        String tokenHash = tokenGenerator.hash(plainToken); // BCrypt o SHA-256

        try {
            // Invalidamos los anteriores para que solo el último link funcione
            tokenRepository.markAllAsUsedByUserId(user.getId());
            guardarNuevoToken(user.getId(), tokenHash);

            String resetLink = construirLinkResetPassword(plainToken);
            notifier.enviarLink(employee.email(), employee.nombre(), resetLink);

        } catch (Exception e) {
            log.error("Error enviando correo de reseteo de contraseña", e);
            throw new AccesoException(ErrorCode.TEC_EMAIL_SENDING, "No pudimos enviar el correo. Reintente más tarde");
        }
    }

    private void validarLimiteIntentos(Long userId) {
        LocalDateTime limite24h = LocalDateTime.now().minusHours(24);
        int intentos = tokenRepository.countByUserIdAndCreatedAtAfter(userId, limite24h);

        if (intentos >= 3) {
            log.warn("Usuario {} excedió el límite de solicitudes de reset en las últimas 24h", userId);
            throw AccesoException.passwordResetBlocked("Límite superado. Has solicitado demasiados enlaces de recuperación");
        }
    }

    private User validarUsuario(String username) {
        return userRepository.findByUsername(username, User.class)
                .orElseThrow(() -> {
                    log.warn("Intento fallido de resetear contraseña para el usuario: {}", username);
                    return new AccesoException(ErrorCode.ACC_USER_NOT_FOUND, "Usuario no encontrado");
                });
    }

    private InfoBasicaResetUsuario obtenerEmpleado(Integer empleadoId) {
        Optional<InfoBasicaResetUsuario> employee = empleadoRepository.findById(empleadoId, InfoBasicaResetUsuario.class);
        if (employee.isEmpty()) {
            throw new AccesoException(ErrorCode.EMP_NOT_FOUND, "Empleado no encontrado");
        }
        return employee.get();
    }

    private void guardarNuevoToken(Long userId, String tokenHash) {
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUserId(userId);
        resetToken.setTokenHash(tokenHash);
        resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        resetToken.setUsed(false);
        resetToken.setCreatedAt(LocalDateTime.now());
        tokenRepository.save(resetToken);
    }

    private String construirLinkResetPassword(String token) {
        return "https://sci.ddns.me:4200/auth/reset-password?token=" + token;
    }
}