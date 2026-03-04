package integra.acceso.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.Set;

@Entity
@Setter
@Getter
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    private boolean activo = true;

    @NotNull
    @ColumnDefault("1")
    @Column(name = "version", nullable = false)
    private Long version = 1L;

    @ElementCollection
    @CollectionTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id")
    )
    @Column(name = "permission_id")
    private Set<String> permissionIds;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault = false; // 💡 primitivo con valor inicial

    public Role() {
    }

    public Role(Long id) {
        this.id = id;
    }
}