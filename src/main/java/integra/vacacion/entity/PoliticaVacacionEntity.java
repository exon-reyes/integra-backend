package integra.vacacion.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "politicas_vacaciones", schema = "integra")
public class PoliticaVacacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "dias_primer_anio", nullable = false)
    private Integer diasPrimerAnio;

    @Column(name = "incremento_anual", nullable = false)
    private Integer incrementoAnual;

    @Column(name = "anios_incremento_hasta", nullable = false)
    private Integer aniosIncrementoHasta;

    @Column(name = "incremento_post_limite", nullable = false)
    private Integer incrementoPostLimite;

    @Column(name = "anios_bloque_post_limite", nullable = false)
    private Integer aniosBloquePostLimite;

    @Column(name = "porcentaje_prima", nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentajePrima = new BigDecimal("25.00");

    @ColumnDefault("1")
    @Column(name = "activa", nullable = false)
    private Boolean activa;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "dias_para_reclamo")
    private Integer diasParaReclamo;
}
