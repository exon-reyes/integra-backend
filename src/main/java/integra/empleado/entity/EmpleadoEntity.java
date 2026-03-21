package integra.empleado.entity;

import integra.empresa.entity.DepartamentoEntity;
import integra.empresa.entity.PuestoEntity;
import integra.empresa.entity.UnidadEntity;
import integra.proceso.entity.VinculacionGestionEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "empleado")
public class EmpleadoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 20)
    @NotNull
    @Column(name = "codigo_empleado", nullable = false, length = 20)
    private String codigoEmpleado;

    @Size(max = 10)
    @Column(name = "pin", length = 10)
    private String pin;

    @Size(max = 50)
    @NotNull
    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @Size(max = 50)
    @NotNull
    @Column(name = "apellido_paterno", nullable = false, length = 50)
    private String apellidoPaterno;

    @Size(max = 50)
    @Column(name = "apellido_materno", length = 50)
    private String apellidoMaterno;

    @Size(max = 100)
    @Column(name = "email", length = 100)
    private String email;

    @Size(max = 15)
    @Column(name = "telefono", length = 15)
    private String telefono;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "departamento_id", nullable = false)
    private DepartamentoEntity departamento;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "puesto_id", nullable = false)
    private PuestoEntity puesto;


    @Column(name = "zona_principal_id")
    private Integer zonaPrincipal;

    @NotNull
    @ColumnDefault("'A'")
    @Lob
    @Column(name = "estatus", nullable = false)
    private String estatus;

    @NotNull
    @Column(name = "fecha_alta", nullable = false)
    private LocalDate fechaAlta;

    @Column(name = "fecha_baja")
    private LocalDate fechaBaja;

    @Lob
    @Column(name = "sexo")
    private String sexo;


    @ColumnDefault("current_timestamp()")
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ColumnDefault("current_timestamp()")
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidad_id")
    private UnidadEntity unidad;
    @Column(name = "fecha_reingreso")
    private LocalDate fechaReingreso;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "jefe_id")
    private EmpleadoEntity jefe;


    @OneToMany(mappedBy = "jefe", fetch = FetchType.LAZY)
    private List<EmpleadoEntity> plantilla = new ArrayList<>();
    @OneToMany

    @JoinColumn(name = "empleado_id")
    @OrderBy("nivelAutoridad ASC")
    private Set<VinculacionGestionEntity> vinculacionGestions;

    @Column(name = "path_avatar")
    private String pathAvatar;

    @Column(name = "nombre_completo", insertable = false, updatable = false)
    private String nombreCompleto;


    public EmpleadoEntity(Integer id) {
        this.id = id;
    }

    public EmpleadoEntity() {
    }
}