package integra.vacacion.domain.model;

import integra.vacacion.entity.EmpleadoTiempoEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for {@link EmpleadoTiempoEntity}
 */
@Getter
@Setter

public class SolicitudEmpleado implements Serializable {
    private Long id;
    private Integer empleadoId;
    private LocalDate fecha;
    private TipoSolicitud tipo;
    private String comentario;
    private EstatusSolicitud estatus;
    private Integer aprobadorId;
    private LocalDateTime fechaAprobacion;
    private String comentariosAprobador;
    private Long periodoId;
    private Boolean activo;
    private LocalDateTime createdAt;
}