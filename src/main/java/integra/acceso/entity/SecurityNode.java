package integra.acceso.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "security_node", schema = "integra", indexes = {
        @Index(name = "idx_node_type_activo",
                columnList = "type, activo"),
        @Index(name = "idx_node_parent",
                columnList = "parent_id"),
        @Index(name = "idx_node_hierarchy",
                columnList = "nivel, orden")})
public class SecurityNode {
    @Id
    @Size(max = 50)
    @Column(name = "id", nullable = false, length = 50)
    private String id;

    @Size(max = 120)
    @NotNull
    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NodeType type;

    @Size(max = 50)
    @Column(name = "parent_id", length = 50)
    private String parentId;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "nivel", nullable = false)
    private Integer nivel;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "orden", nullable = false)
    private Integer orden;

    @NotNull
    @ColumnDefault("1")
    @Column(name = "activo", nullable = false)
    private Boolean activo;

}