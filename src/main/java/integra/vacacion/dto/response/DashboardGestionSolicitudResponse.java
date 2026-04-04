package integra.vacacion.dto.response;

import integra.model.Empleado;

import java.util.List;

public record DashboardGestionSolicitudResponse(
        int totalSolicitudes,
        int solicitudesPendientes,
        int solicitudesAprobadas,
        int solicitudesRechazadas, List<Empleado>empleados,List<GestionSolicitudResponse>solicitudesRecientes
        ) {
}
