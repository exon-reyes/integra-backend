package integra.vacacion.entity;

import integra.empleado.entity.EmpleadoEntity;
import integra.vacacion.domain.model.EstatusSolicitud;
import integra.vacacion.domain.model.TipoSolicitud;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NonNull;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "solicitud_descanso", schema = "integra")
public class SolicitudDescanso {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id")
    private EmpleadoEntity empleado;

    @Column(name = "folio_solicitud", length = 20)
    private Long folioSolicitud;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "tipo_solicitud", nullable = false, length = 25)
    private TipoSolicitud tipoSolicitud;

    @Column(name = "fecha_creacion")
    private LocalDate fechaCreacion;

    @Column(name = "dias_solicitados")
    private Integer diasSolicitados;

    @Enumerated(EnumType.STRING)
    @Column(name = "estatus", nullable = false, length = 25)
    private EstatusSolicitud estatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "periodo_id")
    private PeriodoVacacionalEntity periodo;


    @Enumerated(EnumType.STRING)
    @Column(name = "estatus_nivel1", length = 25)
    private EstatusSolicitud estatusNivel1;

    @Enumerated(EnumType.STRING)
    @Column(name = "estatus_nivel2", length = 25)
    private EstatusSolicitud estatusNivel2;



    @OneToMany(mappedBy = "folio")
    private Set<DiasSolicitudDescanso> diasSolicitudDescansos = new LinkedHashSet<>();

    @Column(name = "fecha_accion_nivel1")
    private LocalDate fechaAccionNivel1;
    @Column(name = "fecha_accion_nivel2")
    private LocalDate fechaAccionNivel2;
    @NonNull
    @OneToMany
    @JoinColumn(name = "solicitud_id")
    private Set<HistorialSolicitudDescanso> historialSolicitudDescansos = new LinkedHashSet<>();


}