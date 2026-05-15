package integra.vacacion.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "politicas_vacaciones_escalas")
public class PoliticaVacacionEscalaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "fecha_vigencia_inicio", nullable = false)
    private LocalDate fechaVigenciaInicio;

    @Column(name = "fecha_vigencia_fin")
    private LocalDate fechaVigenciaFin;

    @Column(nullable = false)
    private Boolean activa = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "politica", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EscalaVacacionEntity> escalas = new ArrayList<>();

    public PoliticaVacacionEscalaEntity() {
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public int getDiasVacacionesPorAnio(int anioAntiguedad) {
        return escalas.stream()
                .filter(e -> e.getAnioAntiguedad() == anioAntiguedad)
                .findFirst()
                .map(EscalaVacacionEntity::getDiasVacaciones)
                .orElseGet(() -> escalas.stream()
                        .filter(e -> e.getAnioAntiguedad() <= anioAntiguedad)
                        .max((e1, e2) -> Integer.compare(e1.getAnioAntiguedad(), e2.getAnioAntiguedad()))
                        .map(EscalaVacacionEntity::getDiasVacaciones)
                        .orElse(12));
    }
}
