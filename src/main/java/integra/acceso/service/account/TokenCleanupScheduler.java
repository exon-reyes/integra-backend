package integra.acceso.service.account;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenCleanupScheduler {

    private final TokenSecurityService tokenSecurityService;

    @Scheduled(fixedRate = 3600000) // Every hour
    public void cleanupExpiredTokens() {
        log.debug("Starting token cleanup task");
        tokenSecurityService.invalidateExpiredTokens();
    }
}