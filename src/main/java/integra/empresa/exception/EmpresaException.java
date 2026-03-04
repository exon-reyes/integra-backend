package integra.empresa.exception;

import integra.global.exception.BusinessException;
import integra.global.exception.code.ErrorCode;

/**
 * Excepción base para errores del módulo de empresa.
 * Extiende BusinessException para mantener consistencia en el manejo de errores de dominio.
 *
 * <p>Ejemplos de uso:</p>
 * <pre>
 * // Empresa no encontrada
 * throw new EmpresaException(
 *     ErrorCode.EMR_NOT_FOUND,
 *     "No existe empresa con ID: " + empresaId
 * );
 *
 * // RFC duplicado
 * throw new EmpresaException(
 *     ErrorCode.EMR_DUPLICATE_RFC,
 *     "Ya existe una empresa registrada con el RFC: " + rfc,
 *     "rfc"
 * );
 * </pre>
 *
 * @author Integra Development Team
 * @since 1.0.0
 */
public class EmpresaException extends BusinessException {

    /**
     * Constructor con código de error y mensaje.
     *
     * @param errorCode el código de error específico del módulo
     * @param message el mensaje descriptivo del error
     */
    public EmpresaException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    /**
     * Constructor con código de error, mensaje y causa.
     *
     * @param errorCode el código de error específico del módulo
     * @param message el mensaje descriptivo del error
     * @param cause la excepción causa original
     */
    public EmpresaException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    /**
     * Constructor con código de error, mensaje y campo relacionado.
     *
     * @param errorCode el código de error específico del módulo
     * @param message el mensaje descriptivo del error
     * @param field el campo que causó el error
     */
    public EmpresaException(ErrorCode errorCode, String message, String field) {
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
    public EmpresaException(ErrorCode errorCode, String message, String field, Object rejectedValue) {
        super(errorCode, message, field, rejectedValue);
    }

    /**
     * Factory method para crear excepción de empresa no encontrada.
     *
     * @param empresaId el ID de la empresa buscada
     * @return EmpresaException configurada
     */
    public static EmpresaException notFound(Long empresaId) {
        return new EmpresaException(
                ErrorCode.EMR_NOT_FOUND,
                "No existe empresa con ID: " + empresaId
        );
    }

    /**
     * Factory method para crear excepción de RFC duplicado.
     *
     * @param rfc el RFC duplicado
     * @return EmpresaException configurada
     */
    public static EmpresaException duplicateRfc(String rfc) {
        return new EmpresaException(
                ErrorCode.EMR_DUPLICATE_RFC,
                "Ya existe una empresa registrada con el RFC: " + rfc,
                "rfc",
                rfc
        );
    }

    /**
     * Factory method para crear excepción de empresa con departamentos asociados.
     *
     * @param empresaId el ID de la empresa
     * @return EmpresaException configurada
     */
    public static EmpresaException hasDepartments(Long empresaId) {
        return new EmpresaException(
                ErrorCode.EMR_HAS_DEPARTMENTS,
                "No se puede eliminar la empresa con ID " + empresaId + " porque tiene departamentos asociados"
        );
    }

    /**
     * Factory method para crear excepción de RFC inválido.
     *
     * @param rfc el RFC inválido
     * @return EmpresaException configurada
     */
    public static EmpresaException invalidRfc(String rfc) {
        return new EmpresaException(
                ErrorCode.EMR_INVALID_RFC,
                "El RFC proporcionado no es válido: " + rfc,
                "rfc",
                rfc
        );
    }
}
