package integra.acceso.repository.account;

import integra.acceso.entity.account.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByTokenHash(String tokenHash);

    // 1. Cambio crítico: Usar "Used = true" en lugar de DELETE inmediato
    @Modifying
    @Query("UPDATE PasswordResetToken p SET p.used = true WHERE p.userId = :userId AND p.used = false")
    void markAllAsUsedByUserId(Long userId);

    // 2. Para validar si el token que llega es válido
    Optional<PasswordResetToken> findByTokenHashAndUsedFalseAndExpiresAtAfter(String tokenHash, LocalDateTime now);

    // 3. CONTROL DE SEGURIDAD: Cuenta intentos totales en las últimas 24h (hayan expirado o no)
    // Esto es lo que evita el abuso del botón de reset.
    int countByUserIdAndCreatedAtAfter(Long userId, LocalDateTime after);

    // 4. MANTENIMIENTO: Borrar basura (tokens viejos de hace días)
    @Modifying
    @Query("DELETE FROM PasswordResetToken p WHERE p.expiresAt < :threshold")
    int deleteByExpiresAtBefore(LocalDateTime threshold);

    // 5. Para verificar si ya tiene uno vigente (Cooldown)
    @Query("SELECT COUNT(p) FROM PasswordResetToken p WHERE p.userId = :userId AND p.used = false AND p.expiresAt > :now")
    int countActiveTokensByUserId(Long userId, LocalDateTime now);
}