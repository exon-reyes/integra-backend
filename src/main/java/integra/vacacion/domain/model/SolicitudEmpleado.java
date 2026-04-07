package integra.vacacion.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter

public class SolicitudEmpleado implements Serializable {
    private Long id;
    private Integer empleadoId;
    private LocalDate fecha;
    private TipoSolicitud tipo;
    private String comentario;
    private EstatusSolicitud estatus;
    private LocalDateTime fechaAprobacion;
    private String comentariosAprobador;
    private Long periodoId;
    private Boolean activo;
    private LocalDate createdAt;
}