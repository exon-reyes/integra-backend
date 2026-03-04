package integra.global.exception;

import integra.global.exception.code.ErrorCode;
import lombok.Getter;

/**
 * Excepción base abstracta para todas las excepciones de la aplicación Integra.
 * Proporciona una estructura común para el manejo de errores con códigos de error estandarizados.
 *
 * <p>Todas las excepciones de negocio y técnicas deben extender esta clase.</p>
 *
 * @author Integra Development Team
 * @since 1.0.0
 */
@Getter
public abstract class IntegraException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String path;

    /**
     * Constructor con código de error y mensaje personalizado.
     *
     * @param errorCode el código de error estandarizado
     * @param message el mensaje descriptivo del error
     */
    protected IntegraException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.path = null;
    }

    /**
     * Constructor con código de error, mensaje y causa.
     *
     * @param errorCode el código de error estandarizado
     * @param message el mensaje descriptivo del error
     * @param cause la excepción causa original
     */
    protected IntegraException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.path = null;
    }

    /**
     * Constructor completo con código de error, mensaje, causa y path.
     *
     * @param errorCode el código de error estandarizado
     * @param message el mensaje descriptivo del error
     * @param cause la excepción causa original
     * @param path el path de la solicitud que generó el error
     */
    protected IntegraException(ErrorCode errorCode, String message, Throwable cause, String path) {
        super(message, cause);
        this.errorCode = errorCode;
        this.path = path;
    }

    /**
     * Obtiene el código de error como string.
     *
     * @return el código de error
     */
    public String getCode() {
        return errorCode != null ? errorCode.getCode() : "UNKNOWN";
    }

    /**
     * Obtiene el estado HTTP asociado al error.
     *
     * @return el estado HTTP
     */
    public int getHttpStatus() {
        return errorCode != null ? errorCode.getHttpStatus() : 500;
    }

    /**
     * Obtiene el título del error para presentación al usuario.
     *
     * @return el título del error
     */
    public String getTitle() {
        return errorCode != null ? errorCode.getTitle() : "Error del sistema";
    }
}
