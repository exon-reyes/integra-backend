package integra.observacion.exception;

import integra.global.exception.BusinessException;
import integra.global.exception.code.ErrorCode;

/**
 * Excepción base para errores del módulo de observaciones.
 * Extiende BusinessException para mantener consistencia en el manejo de errores de dominio.
 *
 * <p>Ejemplos de uso:</p>
 * <pre>
 * // Observación no encontrada
 * throw ObservacionException.notFound(observacionId);
 *
 * // Estatus final no modificable
 * throw ObservacionException.estatusFinalNoModificable();
 *
 * // Categoría no válida
 * throw ObservacionException.categoriaInvalida(categoria);
 * </pre>
 *
 * @author Integra Development Team
 * @since 1.0.0
 */
public class ObservacionException extends BusinessException {

    /**
     * Constructor con código de error y mensaje.
     *
     * @param errorCode el código de error específico del módulo
     * @param message el mensaje descriptivo del error
     */
    public ObservacionException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    /**
     * Constructor con código de error, mensaje y causa.
     *
     * @param errorCode el código de error específico del módulo
     * @param message el mensaje descriptivo del error
     * @param cause la excepción causa original
     */
    public ObservacionException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    /**
     * Constructor con código de error, mensaje y campo relacionado.
     *
     * @param errorCode el código de error específico del módulo
     * @param message el mensaje descriptivo del error
     * @param field el campo que causó el error
     */
    public ObservacionException(ErrorCode errorCode, String message, String field) {
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
    public ObservacionException(ErrorCode errorCode, String message, String field, Object rejectedValue) {
        super(errorCode, message, field, rejectedValue);
    }

    // ==================== FACTORY METHODS ====================

    /**
     * Factory method para crear excepción de observación no encontrada.
     *
     * @param observacionId el ID de la observación buscada
     * @return ObservacionException configurada
     */
    public static ObservacionException notFound(Integer observacionId) {
        return new ObservacionException(
                ErrorCode.DAT_NOT_FOUND,
                "No existe observación con ID: " + observacionId
        );
    }

    /**
     * Factory method para crear excepción de estatus final no modificable.
     *
     * @return ObservacionException configurada
     */
    public static ObservacionException estatusFinalNoModificable() {
        return new ObservacionException(
                ErrorCode.BUS_INVALID_STATE,
                "El estatus ya no puede ser modificado porque la observación está en estado final"
        );
    }

    /**
     * Factory method para crear excepción de categoría inválida.
     *
     * @param categoria la categoría inválida
     * @return ObservacionException configurada
     */
    public static ObservacionException categoriaInvalida(String categoria) {
        return new ObservacionException(
                ErrorCode.VAL_INVALID_VALUE,
                "La categoría de observación no es válida: " + categoria,
                "categoria",
                categoria
        );
    }

    /**
     * Factory method para crear excepción de estatus inválido.
     *
     * @param estatusId el ID del estatus inválido
     * @return ObservacionException configurada
     */
    public static ObservacionException estatusInvalido(Integer estatusId) {
        return new ObservacionException(
                ErrorCode.VAL_INVALID_VALUE,
                "El estatus de observación no es válido: " + estatusId,
                "estatusId",
                estatusId
        );
    }

    /**
     * Factory method para crear excepción de transición de estatus no permitida.
     *
     * @param estatusActual el estatus actual
     * @param estatusNuevo el estatus al que se intenta cambiar
     * @return ObservacionException configurada
     */
    public static ObservacionException transicionEstatusNoPermitida(String estatusActual, String estatusNuevo) {
        return new ObservacionException(
                ErrorCode.BUS_INVALID_STATE,
                "No se puede cambiar el estatus de '" + estatusActual + "' a '" + estatusNuevo + "'"
        );
    }

    /**
     * Factory method para crear excepción de observación duplicada.
     *
     * @param identificador identificador de la observación duplicada
     * @return ObservacionException configurada
     */
    public static ObservacionException duplicate(String identificador) {
        return new ObservacionException(
                ErrorCode.DAT_DUPLICATE,
                "Ya existe una observación registrada con el identificador: " + identificador,
                "identificador",
                identificador
        );
    }
}
