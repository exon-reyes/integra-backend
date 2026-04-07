package integra.vacacion.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "historial_solicitud_descanso", schema = "integra")
public class HistorialSolicitudDescanso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitud_id")
    private SolicitudDescanso solicitud;

    @ColumnDefault("current_timestamp()")
    @Column(name = "fecha")
    private LocalDateTime fecha;

    @Size(max = 255)
    @Column(name = "descripcion")
    private String descripcion;


}