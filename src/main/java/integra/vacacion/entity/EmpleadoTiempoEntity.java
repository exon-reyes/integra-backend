package integra.vacacion.entity;

import integra.empleado.entity.EmpleadoEntity;
import integra.vacacion.domain.model.EstatusSolicitud;
import integra.vacacion.domain.model.TipoSolicitud;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "empleado_tiempo", schema = "integra", indexes = {@Index(name = "idx_empleado", columnList = "empleado_id"),
        @Index(name = "idx_fecha", columnList = "fecha"),
        @Index(name = "idx_tipo", columnList = "tipo"),
        @Index(name = "idx_estatus", columnList = "estatus"),
        @Index(name = "idx_folio", columnList = "folio")}, uniqueConstraints = {@UniqueConstraint(name = "uk_empleado_fecha_tipo", columnNames = {
        "empleado_id",
        "fecha",
        "tipo"})})
public class EmpleadoTiempoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

//    @NotNull
//    @Column(name = "empleado_id", nullable = false)
//    private Integer empleadoId;


    @NotNull
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoSolicitud tipo;


    @Size(max = 255)
    @Column(name = "comentario")
    private String comentario;

    @Enumerated(EnumType.STRING)
    @Column(name = "estatus", nullable = false, length = 20)
    private EstatusSolicitud estatus = EstatusSolicitud.PENDIENTE;


    @Column(name = "fecha_aprobacion")
    private LocalDateTime fechaAprobacion;

    @Lob
    @Column(name = "comentarios_aprobador")
    private String comentariosAprobador;


    @ColumnDefault("1")
    @Column(name = "activo")
    private Boolean activo;

    @ColumnDefault("current_timestamp()")
    @Column(name = "created_at")
    private LocalDate createdAt;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empleado_id", nullable = false)
    private EmpleadoEntity empleado;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "periodo_id")
    private PeriodoVacacionalEntity periodo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estatus_jefe", nullable = false, length = 20)
    private EstatusSolicitud estatusJefe;

    @Enumerated(EnumType.STRING)
    @Column(name = "estatus_rrhh", nullable = false, length = 20)
    private EstatusSolicitud estatusRrhh;

    @Column(name = "folio")
    private Long folio;


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
    }

}