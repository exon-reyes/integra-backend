package integra.empleado.exception;

import integra.global.exception.BusinessException;
import integra.global.exception.code.ErrorCode;

/**
 * Excepción base para errores del módulo de empleados.
 * Extiende BusinessException para mantener consistencia en el manejo de errores de dominio.
 *
 * <p>Ejemplos de uso:</p>
 * <pre>
 * // Empleado no encontrado
 * throw new EmpleadoException(
 *     ErrorCode.EMP_NOT_FOUND,
 *     "No existe empleado con ID: " + empleadoId
 * );
 *
 * // NIP duplicado
 * throw new EmpleadoException(
 *     ErrorCode.EMP_DUPLICATE_NIP,
 *     "Ya existe un empleado registrado con el NIP: " + nip,
 *     "nip"
 * );
 * </pre>
 *
 * @author Integra Development Team
 * @since 1.0.0
 */
public class EmpleadoException extends BusinessException {

    /**
     * Constructor con código de error y mensaje.
     *
     * @param errorCode el código de error específico del módulo
     * @param message el mensaje descriptivo del error
     */
    public EmpleadoException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    /**
     * Constructor con código de error, mensaje y causa.
     *
     * @param errorCode el código de error específico del módulo
     * @param message el mensaje descriptivo del error
     * @param cause la excepción causa original
     */
    public EmpleadoException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    /**
     * Constructor con código de error, mensaje y campo relacionado.
     *
     * @param errorCode el código de error específico del módulo
     * @param message el mensaje descriptivo del error
     * @param field el campo que causó el error
     */
    public EmpleadoException(ErrorCode errorCode, String message, String field) {
        super(errorCode, message, field);
    }

    /**
     * Constructor completo con campo y valor rechazado.
     *
     * @param errorCode el código de error específico del módulo
     * @param message el mensaje descriptivo del error
     * @param field el campo que causó el error
     * @param rejectedValue el valor que fue rechazado
     */
    public EmpleadoException(ErrorCode errorCode, String message, String field, Object rejectedValue) {
        super(errorCode, message, field, rejectedValue);
    }

    /**
     * Factory method para crear excepción de empleado no encontrado.
     *
     * @param empleadoId el ID del empleado buscado
     * @return EmpleadoException configurada
     */
    public static EmpleadoException notFound(Long empleadoId) {
        return new EmpleadoException(
                ErrorCode.EMP_NOT_FOUND,
                "No existe empleado con ID: " + empleadoId
        );
    }

    /**
     * Factory method para crear excepción de NIP duplicado.
     *
     * @param nip el NIP duplicado
     * @return EmpleadoException configurada
     */
    public static EmpleadoException duplicateNip(String nip) {
        return new EmpleadoException(
                ErrorCode.EMP_DUPLICATE_NIP,
                "Ya existe un empleado registrado con el NIP: " + nip,
                "nip",
                nip
        );
    }

    /**
     * Factory method para crear excepción de email duplicado.
     *
     * @param email el email duplicado
     * @return EmpleadoException configurada
     */
    public static EmpleadoException duplicateEmail(String email) {
        return new EmpleadoException(
                ErrorCode.EMP_DUPLICATE_EMAIL,
                "Ya existe un empleado registrado con el correo: " + email,
                "email",
                email
        );
    }

    /**
     * Factory method para crear excepción de empleado inactivo.
     *
     * @param empleadoId el ID del empleado
     * @return EmpleadoException configurada
     */
    public static EmpleadoException notActive(Long empleadoId) {
        return new EmpleadoException(
                ErrorCode.EMP_NOT_ACTIVE,
                "El empleado con ID " + empleadoId + " no está activo"
        );
    }

    /**
     * Factory method para crear excepción de empleado ya asignado.
     *
     * @param empleadoId el ID del empleado
     * @return EmpleadoException configurada
     */
    public static EmpleadoException alreadyAssigned(Long empleadoId) {
        return new EmpleadoException(
                ErrorCode.EMP_ALREADY_ASSIGNED,
                "El empleado con ID " + empleadoId + " ya tiene una asignación activa"
        );
    }

    /**
     * Factory method para crear excepción de NIP inválido.
     *
     * @param nip el NIP inválido
     * @return EmpleadoException configurada
     */
    public static EmpleadoException invalidNip(String nip) {
        return new EmpleadoException(
                ErrorCode.EMP_INVALID_NIP,
                "El NIP de empleado es inválido: " + nip,
                "nip",
                nip
        );
    }
}
