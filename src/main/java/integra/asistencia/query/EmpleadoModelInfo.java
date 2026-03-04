package integra.asistencia.query;

import java.io.Serializable;

public record EmpleadoModelInfo(Integer id, Integer puestoId, String puestoNombre, Integer unidadId, String nombre,
                                String codigoEmpleado,
                                String apellidoPaterno,
                                String apellidoMaterno, String estatus) implements Serializable {
}