package integra.acceso.service.account;

import integra.acceso.repository.account.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenSecurityService {

    private final PasswordResetTokenRepository tokenRepository;

    @Transactional
    public void invalidateExpiredTokens() {
        int invalidated = tokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());
        log.info("Invalidated {} expired tokens", invalidated);
    }
}