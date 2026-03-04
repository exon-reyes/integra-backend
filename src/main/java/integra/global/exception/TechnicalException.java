package integra.global.exception;

import integra.global.exception.code.ErrorCode;

/**
 * Excepción base para errores técnicos (infraestructura).
 * Representa problemas de conectividad, base de datos, servicios externos,
 * o cualquier error técnico que no esté relacionado con reglas de negocio.
 *
 * <p>Estas excepciones NO deben exponer detalles técnicos sensibles a los clientes.
 * El mensaje para el usuario debe ser genérico, mientras que los detalles se registran internamente.</p>
 *
 * <p>Ejemplos de uso:</p>
 * <pre>
 * // Error de conexión a base de datos
 * throw new TechnicalException(
 *     ErrorCode.TECH_DB_CONNECTION,
 *     "No se pudo establecer conexión con la base de datos",
 *     originalSQLException
 * );
 *
 * // Error de servicio externo
 * throw new TechnicalException(
 *     ErrorCode.TECH_EXTERNAL_SERVICE,
 *     "El servicio de notificaciones no está disponible",
 *     httpException
 * );
 * </pre>
 *
 * @author Integra Development Team
 * @since 1.0.0
 */
public class TechnicalException extends IntegraException {

    private final String internalDetails;

    /**
     * Constructor con código de error y mensaje.
     *
     * @param errorCode el código de error estandarizado
     * @param message el mensaje descriptivo (genérico para el usuario)
     */
    public TechnicalException(ErrorCode errorCode, String message) {
        super(errorCode, message);
        this.internalDetails = null;
    }

    /**
     * Constructor con código de error, mensaje y causa.
     *
     * @param errorCode el código de error estandarizado
     * @param message el mensaje descriptivo (genérico para el usuario)
     * @param cause la excepción causa original
     */
    public TechnicalException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
        this.internalDetails = cause != null ? cause.getMessage() : null;
    }

    /**
     * Constructor completo con detalles internos.
     *
     * @param errorCode el código de error estandarizado
     * @param message el mensaje descriptivo (genérico para el usuario)
     * @param cause la excepción causa original
     * @param internalDetails detalles técnicos para logs internos
     */
    public TechnicalException(ErrorCode errorCode, String message, Throwable cause, String internalDetails) {
        super(errorCode, message, cause);
        this.internalDetails = internalDetails;
    }

    /**
     * Obtiene los detalles internos para logging.
     *
     * @return detalles técnicos internos
     */
    public String getInternalDetails() {
        return internalDetails;
    }

    /**
     * Obtiene el mensaje seguro para exponer al cliente.
     * Los errores técnicos 500 no deben exponer detalles internos.
     *
     * @return mensaje seguro para el cliente
     */
    public String getSafeMessage() {
        if (getErrorCode() != null && getErrorCode().getHttpStatus() >= 500) {
            return "Ha ocurrido un error interno. Por favor, contacte al administrador del sistema.";
        }
        return getMessage();
    }
}
