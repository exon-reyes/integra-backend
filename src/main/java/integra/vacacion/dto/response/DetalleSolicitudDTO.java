package integra.vacacion.dto.response;

import integra.model.Empleado;
import integra.vacacion.domain.model.EstatusSolicitud;
import integra.vacacion.domain.model.TipoSolicitud;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class DetalleSolicitudDTO {
    private Empleado empleado;
    private int diasHabilitados;
    private int anioGestion;
    private int diasSolicitados;
    private int diasTomados;
    private int restanteSiAprueba;
    private EstatusSolicitud estatusPrimerResponsable;
    private EstatusSolicitud estatusSegundoResponsable;
    private Empleado primerJefe;
    private EstatusSolicitud estatusGlobal;
    private long folioSolicitud;
    private LocalDate fecha;
    private TipoSolicitud tipoSolicitud;
    private Empleado segundoJefe;
    private List<FechaSolicitud>fechaSolicituds;
}
