package integra.proceso.entity;

import integra.empleado.entity.EmpleadoEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "vinculacion_gestion", schema = "integra", indexes = {@Index(name = "idx_busqueda_vinculo",
        columnList = "empleado_id, tipo_proceso")})
public class VinculacionGestionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "gestor_id", nullable = false)
    private EmpleadoEntity gestor;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_proceso_id", nullable = false)
    private TipoProcesoEntity tipoProceso;

    @NotNull
    @ColumnDefault("1")
    @Column(name = "nivel_autoridad", nullable = false)
    private Integer nivelAutoridad;

    @NotNull
    @Column(name = "empleado_id", nullable = false)
    private Integer empleadoId;

    @ColumnDefault("current_timestamp()")
    @Column(name = "fecha_asignacion")
    private LocalDateTime fechaAsignacion;

    @ColumnDefault("1")
    @Column(name = "activo")
    private Boolean activo;


}