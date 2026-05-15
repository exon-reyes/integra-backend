package integra.vacacion.domain.model;

import integra.model.Empleado;
import integra.model.Gestor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class Descanso {
    private Long id;
    private Empleado empleado;
    private LocalDate fecha;
    private String comentario;
    private Boolean activo;
    private EstatusSolicitud estatus;
    private Gestor gestor;
    private LocalDateTime fechaAprobacion;
    private String comentarioAprobador;
}
