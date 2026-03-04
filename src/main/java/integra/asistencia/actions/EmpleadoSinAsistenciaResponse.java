package integra.asistencia.actions;
public record EmpleadoSinAsistenciaResponse(
        Integer id,
        String codigoEmpleado,
        String nombreCompleto,
        String puesto,
        String unidad,
        String zona,
        String supervisor
) {
}