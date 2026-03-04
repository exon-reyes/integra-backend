package integra.acceso.service.account;

import integra.acceso.entity.account.PasswordResetToken;
import integra.acceso.exception.AccesoException;
import integra.acceso.repository.account.PasswordResetTokenRepository;
import integra.acceso.util.TokenGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ValidateTokenUseCase {

    private final PasswordResetTokenRepository tokenRepository;
    private final TokenGenerator tokenGenerator;

    public void execute(String token) {
        String tokenHash = tokenGenerator.hash(token);
        PasswordResetToken resetToken = findToken(tokenHash);

        validateTokenExpiry(resetToken);
        validateTokenUsage(resetToken);
    }

    private PasswordResetToken findToken(String tokenHash) {
        return tokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(AccesoException::invalidResetToken);
    }

    private void validateTokenExpiry(PasswordResetToken token) {
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw AccesoException.invalidResetToken();
        }
    }

    private void validateTokenUsage(PasswordResetToken token) {
        if (Boolean.TRUE.equals(token.getUsed())) {
            throw new AccesoException(integra.global.exception.code.ErrorCode.AUTH_INVALID_RESET_TOKEN, "El token ya fue utilizado");
        }
    }
}