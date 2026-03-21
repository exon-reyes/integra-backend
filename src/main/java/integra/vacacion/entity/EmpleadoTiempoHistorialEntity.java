package integra.vacacion.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "empleado_tiempo_historial", schema = "integra")
public class EmpleadoTiempoHistorialEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "empleado_tiempo_id", nullable = false)
    private Long empleadoTiempoId;

    @NotNull
    @Size(max = 50)
    @Column(name = "tipo_evento", nullable = false, length = 50)
    private String tipoEvento;

    @NotNull
    @Column(name = "fecha_evento", nullable = false)
    private LocalDateTime fechaEvento;

    @NotNull
    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    @Lob
    @Column(name = "comentario")
    private String comentario;

    @PrePersist
    protected void onCreate() {
        if (fechaEvento == null) {
            fechaEvento = LocalDateTime.now();
        }
    }
}
