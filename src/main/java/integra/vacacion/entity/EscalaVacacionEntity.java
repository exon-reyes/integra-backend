package integra.vacacion.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "escalas_vacaciones", schema = "integra",
        uniqueConstraints = @UniqueConstraint(columnNames = {"politica_id", "anio_antiguedad"}))
public class EscalaVacacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "politica_id", nullable = false)
    private PoliticaVacacionEscalaEntity politica;

    @Column(name = "anio_antiguedad", nullable = false)
    private Integer anioAntiguedad;

    @Column(name = "dias_vacaciones", nullable = false)
    private Integer diasVacaciones;

    public EscalaVacacionEntity() {
    }

    public EscalaVacacionEntity(Integer anioAntiguedad, Integer diasVacaciones) {
        this.anioAntiguedad = anioAntiguedad;
        this.diasVacaciones = diasVacaciones;
    }
}
