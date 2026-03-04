package integra.asistencia.entity;

import integra.empleado.entity.EmpleadoEntity;
import integra.empresa.entity.UnidadEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "cruce_empleado_kiosco", schema = "integra", indexes = {
        @Index(name = "idx_accion_fecha",
                columnList = "accion, fecha"),
        @Index(name = "fk_unidad_cruce_fecha",
                columnList = "fecha, unidad_registro_id"),
        @Index(name = "idx_fecha_cruce_asistencia",
                columnList = "fecha")})
public class
CruceEmpleadoKiosco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "asistencia_id", nullable = false)
    private Integer asistenciaId;

    @Lob
    @Column(name = "accion")
    private String accion;

    @Size(max = 255)
    @Column(name = "path_img")
    private String pathImg;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidad_registro_id")
    private UnidadEntity unidadRegistro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidad_esperada_id")
    private UnidadEntity unidadEsperada;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id")
    private EmpleadoEntity empleado;


}