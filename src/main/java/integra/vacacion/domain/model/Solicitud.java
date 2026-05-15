package integra.vacacion.domain.model;

import integra.model.Gestor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class Solicitud {
    private Long id;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private int diasSolicitados;
    private String comentario;
    private String estatus;
    private List<Gestor> gestores;
    private String comentarioAprobador;
    private LocalDate fechaAprobacion;
    private LocalDate fechaSolicitud;
    private Long periodoId;

}
