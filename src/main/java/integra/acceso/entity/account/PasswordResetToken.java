package integra.acceso.entity.account;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Size(max = 255)
    @NotNull
    @Column(name = "token_hash", nullable = false)
    private String tokenHash;

    @NotNull
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @ColumnDefault("0")
    @Column(name = "used")
    private Boolean used;

    @ColumnDefault("current_timestamp()")
    @Column(name = "created_at")
    private LocalDateTime createdAt;


}