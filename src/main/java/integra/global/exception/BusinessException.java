package integra.global.exception;

import integra.global.exception.code.ErrorCode;
import lombok.Getter;

/**
 * Excepción base para errores de negocio (dominio).
 * Representa violaciones de reglas de negocio, validaciones fallidas,
 * o cualquier condición que impida completar una operación por razones de negocio.
 *
 * <p>Estas excepciones son seguras para exponer a los clientes de la API.</p>
 *
 * <p>Ejemplos de uso:</p>
 * <pre>
 * // Saldo insuficiente
 * throw new BusinessException(
 *     ErrorCode.BUSINESS_INSUFFICIENT_BALANCE,
 *     "El saldo de la cuenta no es suficiente para esta transacción"
 * );
 *
 * // Operación no permitida
 * throw new BusinessException(
 *     ErrorCode.BUSINESS_OPERATION_NOT_ALLOWED,
 *     "No se puede cancelar una orden que ya ha sido enviada"
 * );
 * </pre>
 *
 * @author Integra Development Team
 * @since 1.0.0
 */
@Getter
public class BusinessException extends IntegraException {

    private final String field;
    private final Object rejectedValue;

    /**
     * Constructor con código de error y mensaje.
     *
     * @param errorCode el código de error estandarizado
     * @param message el mensaje descriptivo del error
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(errorCode, message);
        this.field = null;
        this.rejectedValue = null;
    }

    /**
     * Constructor con código de error, mensaje y causa.
     *
     * @param errorCode el código de error estandarizado
     * @param message el mensaje descriptivo del error
     * @param cause la excepción causa original
     */
    public BusinessException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
        this.field = null;
        this.rejectedValue = null;
    }

    /**
     * Constructor con código de error, mensaje y campo relacionado.
     *
     * @param errorCode el código de error estandarizado
     * @param message el mensaje descriptivo del error
     * @param field el campo que causó el error
     */
    public BusinessException(ErrorCode errorCode, String message, String field) {
        super(errorCode, message);
        this.field = field;
        this.rejectedValue = null;
    }

    /**
     * Constructor completo con campo y valor rechazado.
     *
     * @param errorCode el código de error estandarizado
     * @param message el mensaje descriptivo del error
     * @param field el campo que causó el error
     * @param rejectedValue el valor que fue rechazado
     */
    public BusinessException(ErrorCode errorCode, String message, String field, Object rejectedValue) {
        super(errorCode, message);
        this.field = field;
        this.rejectedValue = rejectedValue;
    }
}
