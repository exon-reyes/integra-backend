package integra.credenciales.exception;

import integra.global.exception.BusinessException;
import integra.global.exception.code.ErrorCode;

/**
 * Excepción base para errores del módulo de credenciales.
 * Extiende BusinessException para mantener consistencia en el manejo de errores de dominio.
 *
 * <p>Ejemplos de uso:</p>
 * <pre>
 * // Cuenta no encontrada
 * throw CredencialesException.cuentaNotFound(cuentaId);
 *
 * // Usuario duplicado
 * throw CredencialesException.duplicateUser(usuario);
 *
 * // Tipo de cuenta no encontrado
 * throw CredencialesException.tipoCuentaNotFound(tipoId);
 * </pre>
 *
 * @author Integra Development Team
 * @since 1.0.0
 */
public class CredencialesException extends BusinessException {

    /**
     * Constructor con código de error y mensaje.
     *
     * @param errorCode el código de error específico del módulo
     * @param message el mensaje descriptivo del error
     */
    public CredencialesException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    /**
     * Constructor con código de error, mensaje y causa.
     *
     * @param errorCode el código de error específico del módulo
     * @param message el mensaje descriptivo del error
     * @param cause la excepción causa original
     */
    public CredencialesException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    /**
     * Constructor con código de error, mensaje y campo relacionado.
     *
     * @param errorCode el código de error específico del módulo
     * @param message el mensaje descriptivo del error
     * @param field el campo que causó el error
     */
    public CredencialesException(ErrorCode errorCode, String message, String field) {
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
    public CredencialesException(ErrorCode errorCode, String message, String field, Object rejectedValue) {
        super(errorCode, message, field, rejectedValue);
    }

    // ==================== FACTORY METHODS ====================

    /**
     * Factory method para crear excepción de cuenta no encontrada.
     *
     * @param cuentaId el ID de la cuenta buscada
     * @return CredencialesException configurada
     */
    public static CredencialesException cuentaNotFound(Integer cuentaId) {
        return new CredencialesException(
                ErrorCode.DAT_NOT_FOUND,
                "No existe cuenta de credenciales con ID: " + cuentaId
        );
    }

    /**
     * Factory method para crear excepción de usuario duplicado.
     *
     * @param usuario el nombre de usuario duplicado
     * @return CredencialesException configurada
     */
    public static CredencialesException duplicateUser(String usuario) {
        return new CredencialesException(
                ErrorCode.DAT_DUPLICATE,
                "Ya existe una cuenta registrada con el usuario: " + usuario,
                "usuario",
                usuario
        );
    }

    /**
     * Factory method para crear excepción de tipo de cuenta no encontrado.
     *
     * @param tipoId el ID del tipo de cuenta
     * @return CredencialesException configurada
     */
    public static CredencialesException tipoCuentaNotFound(Integer tipoId) {
        return new CredencialesException(
                ErrorCode.DAT_NOT_FOUND,
                "No existe tipo de cuenta con ID: " + tipoId
        );
    }

    /**
     * Factory method para crear excepción de tipo de cuenta duplicado.
     *
     * @param nombre el nombre del tipo de cuenta duplicado
     * @return CredencialesException configurada
     */
    public static CredencialesException duplicateTipoCuenta(String nombre) {
        return new CredencialesException(
                ErrorCode.DAT_DUPLICATE,
                "Ya existe un tipo de cuenta registrado con el nombre: " + nombre,
                "nombre",
                nombre
        );
    }

    /**
     * Factory method para crear excepción de referencia inválida a unidad.
     *
     * @param unidadId el ID de la unidad inválida
     * @return CredencialesException configurada
     */
    public static CredencialesException unidadNotFound(Long unidadId) {
        return new CredencialesException(
                ErrorCode.DAT_INVALID_REFERENCE,
                "La unidad con ID '" + unidadId + "' no existe o no es válida",
                "idUnidad",
                unidadId
        );
    }

    /**
     * Factory method para crear excepción de referencia inválida a departamento.
     *
     * @param departamentoId el ID del departamento inválido
     * @return CredencialesException configurada
     */
    public static CredencialesException departamentoNotFound(Integer departamentoId) {
        return new CredencialesException(
                ErrorCode.DAT_INVALID_REFERENCE,
                "El departamento con ID '" + departamentoId + "' no existe o no es válido",
                "idDepartamento",
                departamentoId
        );
    }
}
