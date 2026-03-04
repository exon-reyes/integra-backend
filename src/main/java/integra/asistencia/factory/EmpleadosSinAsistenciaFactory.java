package integra.asistencia.factory;
import integra.asistencia.actions.EmpleadoSinAsistenciaResponse;
import integra.asistencia.actions.EmpleadosSinAsistenciaCommand;
import integra.asistencia.actions.EmpleadosSinAsistenciaRequest;
import integra.empleado.entity.EmpleadoEntity;
public class EmpleadosSinAsistenciaFactory {
    private EmpleadosSinAsistenciaFactory() {
        throw new IllegalStateException("Utility class");
    }
    public static EmpleadosSinAsistenciaCommand mapRequestToCommand(EmpleadosSinAsistenciaRequest request) {
        EmpleadosSinAsistenciaCommand command = new EmpleadosSinAsistenciaCommand();
        command.setFechaInicio(request.getFechaInicio());
        command.setFechaFin(request.getFechaFin());
        command.setUnidadId(request.getUnidadId());
        command.setPuestoId(request.getPuestoId());
        command.setZonaId(request.getZonaId());
        command.setSupervisorId(request.getSupervisorId());
        return command;
    }
    public static EmpleadoSinAsistenciaResponse mapEntityToResponse(EmpleadoEntity empleado) {
        return new EmpleadoSinAsistenciaResponse(
                empleado.getId(),
                empleado.getCodigoEmpleado(),
                empleado.getNombreCompleto(),
                empleado.getPuesto() != null ? empleado.getPuesto().getNombre() : null,
                empleado.getUnidad() != null ? empleado.getUnidad().getNombre() : null,
                empleado.getZonaPrincipal() != null ? empleado.getZonaPrincipal().toString() : null,
                null // supervisor se obtiene de la relación responsables
        );
    }
}