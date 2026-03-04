package integra.acceso.service.account;

import integra.acceso.command.PasswordResetCompletion;
import integra.acceso.entity.User;
import integra.acceso.entity.account.PasswordResetToken;
import integra.acceso.repository.UserRepository;
import integra.acceso.repository.account.PasswordResetTokenRepository;
import integra.acceso.util.TokenGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ComplearPasswordReset {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final TokenGenerator tokenGenerator;
    private final PasswordEncoder passwordEncoder;

    public void execute(PasswordResetCompletion cmd) {
        PasswordResetToken token = tokenRepository
                .findByTokenHashAndUsedFalseAndExpiresAtAfter(
                        tokenGenerator.hash(cmd.token()),
                        LocalDateTime.now()
                )
                .orElseThrow(() -> new IllegalStateException("Token inválido, expirado o usado"));

        User user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));

        user.setPassword(passwordEncoder.encode(cmd.newPassword()));
        token.setUsed(true);
    }
}
