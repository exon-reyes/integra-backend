package integra.vacacion.entity;

import integra.empleado.entity.EmpleadoEntity;
import integra.vacacion.domain.model.EstatusSolicitud;
import integra.vacacion.domain.model.TipoSolicitud;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "solicitud_descanso")
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


    // Cambio 1: Asegurar que el mappedBy apunte al campo correcto en DiasSolicitudDescanso (parece ser 'folio')
    @OneToMany(mappedBy = "folio", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DiasSolicitudDescanso> diasSolicitudDescansos = new LinkedHashSet<>();
    @Column(name = "fecha_accion_nivel1")
    private LocalDate fechaAccionNivel1;
    @Column(name = "fecha_accion_nivel2")
    private LocalDate fechaAccionNivel2;


    // Cambio 2: El historial TAMBIÉN debe borrarse en cascada si eliminas la solicitud
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "solicitud_id") // Esto indica que el ID de esta tabla está en la otra
    private Set<HistorialSolicitudDescanso> historialSolicitudDescansos = new LinkedHashSet<>();

}