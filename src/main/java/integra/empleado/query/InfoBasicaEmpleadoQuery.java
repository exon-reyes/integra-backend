package integra.empleado.query;

import integra.empleado.entity.EmpleadoEntity;

/**
 * DTO para consulta query de  {@link EmpleadoEntity}
 * Información básica del empleado
 */
public record InfoBasicaEmpleadoQuery(Integer id, String codigoEmpleado, String nombreCompleto) {

}
