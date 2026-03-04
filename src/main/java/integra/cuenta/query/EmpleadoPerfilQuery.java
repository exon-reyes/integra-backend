package integra.cuenta.query;

import integra.empleado.entity.EmpleadoEntity;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link EmpleadoEntity}
 */
public record EmpleadoPerfilQuery(Integer id, String nombre, String apellidoPaterno, String apellidoMaterno,
                                  String email, String telefono, Integer departamentoId, String departamentoNombre,
                                  Integer puestoId, String puestoNombre, String estatus, LocalDate fechaAlta,
                                  Integer unidadId, String unidadNombreCompleto) implements Serializable {
}