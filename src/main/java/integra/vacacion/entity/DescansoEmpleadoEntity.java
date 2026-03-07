package integra.vacacion.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "descansos_empleado", schema = "integra")
public class DescansoEmpleadoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "empleado_id", nullable = false)
    private Integer empleadoId;

    @Column(name = "fecha_descanso", nullable = false)
    private LocalDate fechaDescanso;

    @Column(name = "motivo", length = 100)
    private String motivo;

    @Column(name = "activo")
    private Boolean activo = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
