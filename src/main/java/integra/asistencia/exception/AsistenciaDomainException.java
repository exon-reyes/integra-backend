package integra.asistencia.exception;

import integra.global.exception.BusinessException;
import integra.global.exception.code.ErrorCode;

/**
 * Excepción base para errores de dominio del módulo de asistencia.
 * Extiende BusinessException para mantener consistencia en el manejo de errores.
 *
 * <p>Esta excepción unifica y reemplaza las excepciones anteriores:</p>
 * <ul>
 *   <li>AsistenciaException</li>
 *   <li>KioscoConfigException</li>
 *   <li>PinKioscoException</li>
 * </ul>
 *
 * <p>Ejemplos de uso:</p>
 * <pre>
 * // Registro no encontrado
 * throw AsistenciaDomainException.notFound(registroId);
 *
 * // Registro duplicado
 * throw AsistenciaDomainException.duplicateEntry(empleadoId, fecha);
 *
 * // Kiosco no configurado
 * throw AsistenciaDomainException.kioscoNotConfigured(kioscoId);
 *
 * // PIN inválido
 * throw AsistenciaDomainException.invalidPin();
 * </pre>
 *
 * @author Integra Development Team
 * @since 1.0.0
 */
public class AsistenciaDomainException extends BusinessException {

    /**
     * Constructor con código de error y mensaje.
     *
     * @param errorCode el código de error específico del módulo
     * @param message   el mensaje descriptivo del error
     */
    public AsistenciaDomainException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    /**
     * Constructor con código de error, mensaje y causa.
     *
     * @param errorCode el código de error específico del módulo
     * @param message   el mensaje descriptivo del error
     * @param cause     la excepción causa original
     */
    public AsistenciaDomainException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    /**
     * Constructor con código de error, mensaje, campo y valor rechazado.
     *
     * @param errorCode     el código de error específico del módulo
     * @param message       el mensaje descriptivo del error
     * @param field         el campo que causó el error
     * @param rejectedValue el valor que fue rechazado
     */
    public AsistenciaDomainException(ErrorCode errorCode, String message, String field, Object rejectedValue) {
        super(errorCode, message, field, rejectedValue);
    }

    // ==================== FACTORY METHODS ====================

    /**
     * Factory method para crear excepción de registro no encontrado.
     *
     * @param registroId el ID del registro buscado
     * @return AsistenciaDomainException configurada
     */
    public static AsistenciaDomainException notFound(Long registroId) {
        return new AsistenciaDomainException(
                ErrorCode.ASI_NOT_FOUND,
                "No existe registro de asistencia con ID: " + registroId
        );
    }

    /**
     * Factory method para crear excepción de registro duplicado.
     *
     * @param empleadoId el ID del empleado
     * @param fecha      la fecha del registro
     * @return AsistenciaDomainException configurada
     */
    public static AsistenciaDomainException duplicateEntry(Long empleadoId, String fecha) {
        return new AsistenciaDomainException(
                ErrorCode.ASI_DUPLICATE_ENTRY,
                "Ya existe un registro de asistencia para el empleado " + empleadoId + " en la fecha " + fecha
        );
    }

    /**
     * Factory method para crear excepción de hora inválida.
     *
     * @param hora la hora inválida
     * @return AsistenciaDomainException configurada
     */
    public static AsistenciaDomainException invalidTime(String hora) {
        return new AsistenciaDomainException(
                ErrorCode.ASI_INVALID_TIME,
                "La hora de registro es inválida: " + hora
        );
    }

    /**
     * Factory method para crear excepción de registro fuera de horario.
     *
     * @param horarioPermitido el horario permitido
     * @return AsistenciaDomainException configurada
     */
    public static AsistenciaDomainException outsideSchedule(String horarioPermitido) {
        return new AsistenciaDomainException(
                ErrorCode.ASI_OUTSIDE_SCHEDULE,
                "El registro está fuera del horario permitido: " + horarioPermitido
        );
    }

    /**
     * Factory method para crear excepción de kiosco no configurado.
     *
     * @param kioscoId el ID del kiosco
     * @return AsistenciaDomainException configurada
     */
    public static AsistenciaDomainException kioscoNotConfigured(Long kioscoId) {
        return new AsistenciaDomainException(
                ErrorCode.ASI_KIOSCO_NOT_CONFIGURED,
                "El kiosco con ID " + kioscoId + " no está configurado"
        );
    }

    /**
     * Factory method para crear excepción de PIN inválido.
     *
     * @return AsistenciaDomainException configurada
     */
    public static AsistenciaDomainException invalidPin() {
        return new AsistenciaDomainException(
                ErrorCode.ASI_KIOSCO_INVALID_PIN,
                "El PIN proporcionado es inválido"
        );
    }

    /**
     * Factory method para crear excepción de kiosco bloqueado.
     *
     * @param kioscoId el ID del kiosco
     * @return AsistenciaDomainException configurada
     */
    public static AsistenciaDomainException kioscoBlocked(Long kioscoId) {
        return new AsistenciaDomainException(
                ErrorCode.ASI_KIOSCO_BLOCKED,
                "El kiosco con ID " + kioscoId + " está bloqueado"
        );
    }

    /**
     * Factory method para crear excepción de rango de fechas inválido.
     *
     * @param fechaInicio fecha de inicio
     * @param fechaFin    fecha de fin
     * @return AsistenciaDomainException configurada
     */
    public static AsistenciaDomainException invalidDateRange(String fechaInicio, String fechaFin) {
        return new AsistenciaDomainException(
                ErrorCode.REP_INVALID_DATE_RANGE,
                "El rango de fechas es inválido: la fecha de inicio (" + fechaInicio + ") no puede ser posterior a la fecha fin (" + fechaFin + ")"
        );
    }

    /**
     * Factory method para crear excepción de pausa activa existente.
     *
     * @param empleadoId el ID del empleado
     * @return AsistenciaDomainException configurada
     */
    public static AsistenciaDomainException pausaActivaExistente(Integer empleadoId) {
        return new AsistenciaDomainException(
                ErrorCode.BUS_INVALID_STATE,
                "Ya existe una pausa activa para el empleado con ID: " + empleadoId
        );
    }

    /**
     * Factory method para crear excepción de jornada no encontrada.
     *
     * @param jornadaId el ID de la jornada
     * @return AsistenciaDomainException configurada
     */
    public static AsistenciaDomainException jornadaNotFound(Integer jornadaId) {
        return new AsistenciaDomainException(
                ErrorCode.ASI_NOT_FOUND,
                "No existe jornada con ID: " + jornadaId
        );
    }

    /**
     * Factory method para crear excepción de tipo de acción inválido.
     *
     * @param tipoAccion el tipo de acción inválido
     * @return AsistenciaDomainException configurada
     */
    public static AsistenciaDomainException invalidActionType(String tipoAccion) {
        return new AsistenciaDomainException(
                ErrorCode.VAL_INVALID_VALUE,
                "Tipo de acción no válido: " + tipoAccion,
                "tipoAccion",
                tipoAccion
        );
    }
}
