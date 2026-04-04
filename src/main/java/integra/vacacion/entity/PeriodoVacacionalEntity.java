package integra.vacacion.entity;

import integra.empleado.entity.EmpleadoEntity;
import integra.vacacion.core.EstatusPeriodo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "periodos_vacacionales", schema = "integra")
public class PeriodoVacacionalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;



    @Column(name = "anio_laboral", nullable = false)
    private Integer anioLaboral;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "dias_habilitados", nullable = false)
    private Integer diasHabilitados;

    @ColumnDefault("0")
    @Column(name = "dias_tomados", nullable = false)
    private Integer diasTomados;

    @Column(name = "dias_restantes", nullable = false)
    private Integer diasRestantes;

    @Column(name = "fecha_caducidad", nullable = false)
    private LocalDate fechaCaducidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "estatus", length = 20)
    private EstatusPeriodo estatus = EstatusPeriodo.VIGENTE;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "periodo_numero")
    private Integer periodoNumero;

    @Column(name = "anio_gestion")
    private Integer anioGestion;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empleado_id", nullable = false)
    private EmpleadoEntity empleado;

    public PeriodoVacacionalEntity(Long id) {
        this.id = id;
    }

    public PeriodoVacacionalEntity() {
    }

}
