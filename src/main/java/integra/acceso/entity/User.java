package integra.acceso.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users", schema = "integra", uniqueConstraints = {@UniqueConstraint(name = "idx_users_username", columnNames = {
        "username"}),
        @UniqueConstraint(name = "idx_users_email", columnNames = {"email"}),
        @UniqueConstraint(name = "idx_users_empleado", columnNames = {"empleado_id"})})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 50)
    @NotNull
    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Size(max = 100)
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Size(max = 255)
    @NotNull
    @Column(name = "password", nullable = false)
    private String password;

    @NotNull
    @ColumnDefault("1")
    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @ColumnDefault("current_timestamp()")
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "empleado_id")
    private Integer empleadoId;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @ColumnDefault("0")
    @Column(name = "requier_cambio_password")
    private Boolean requierCambioPassword;

    @ManyToMany
    @JsonIgnore
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "user_permissions", joinColumns = @JoinColumn(name = "user_id"))
    @JsonIgnore
    @Column(name = "permission_id")
    private Set<String> directPermissionIds = new HashSet<>();

    public User() {
    }

    public User(Long id) {
        this.id = id;
    }
}