package integra.acceso.repository.account;

import integra.acceso.entity.account.AccountActivationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AccountRegistrationTokenRepository extends JpaRepository<AccountActivationToken, Long> {
    Optional<AccountActivationToken> findByTokenHash(String tokenHash);

    void deleteByEmpleadoId(Integer empleadoId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO account_registration_tokens (empleado_id, token_hash, expires_at, created_at) " +
            "VALUES (?1, ?2, ?3, ?4) " +
            "ON DUPLICATE KEY UPDATE " +
            "token_hash = VALUES(token_hash), " +
            "expires_at = VALUES(expires_at), " +
            "created_at = VALUES(created_at)", nativeQuery = true)
    void upsertToken(Integer empleadoId, String tokenHash, LocalDateTime expiresAt, LocalDateTime createdAt);
}