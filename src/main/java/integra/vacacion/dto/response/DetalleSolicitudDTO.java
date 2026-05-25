package integra.vacacion.dto.response;

import integra.model.Empleado;
import integra.vacacion.domain.model.EstatusSolicitud;
import integra.vacacion.domain.model.TipoSolicitud;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class DetalleSolicitudDTO {
    private Long id;
    private Empleado empleado;
    private int diasHabilitados;
    private int anioGestion;
    private int diasSolicitados;       // Global: todas las solicitudes activas del periodo
    private int diasEstaSolicitud;     // Solo los días de esta solicitud específica (no cancelados)
    private int diasAprobadosEstaSolicitud;   // Días aprobados granularmente en esta solicitud
    private int diasPendientesEstaSolicitud;  // Días pendientes granularmente en esta solicitud
    private int diasCanceladosEstaSolicitud;  // Días cancelados granularmente en esta solicitud
    private int diasTomados;
    private LocalDate fechaCreacion;
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
