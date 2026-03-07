package integra.vacacion.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "configuracion_descanso_empleado", schema = "integra")
public class ConfiguracionDescansoEmpleadoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "empleado_id", nullable = false)
    private Integer empleadoId;

    @Column(name = "dia_descanso", nullable = false)
    private Integer diaDescanso;

    @ColumnDefault("1")
    @Column(name = "activo")
    private Boolean activo;
}
