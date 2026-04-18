package integra.vacacion.entity;

import integra.vacacion.domain.model.EstatusSolicitud;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "dias_solicitud_descanso")
public class DiasSolicitudDescanso {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folio_id")
    private SolicitudDescanso folio;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "estatus_nivel1", length = 25)
    @Enumerated(EnumType.STRING)
    private EstatusSolicitud estatusNivel1;


    @Column(name = "estatus_nivel2", length = 25)
    @Enumerated(EnumType.STRING)
    private EstatusSolicitud estatusNivel2;

    @Column(name = "fecha_accion_nivel1")
    private LocalDate fechaAccionNivel1;

    @Column(name = "fecha_accion_nivel2")
    private LocalDate fechaAccionNivel2;


}