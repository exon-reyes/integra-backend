package integra.empresa.unidad.exception;

import integra.global.exception.BusinessException;
import integra.global.exception.code.ErrorCode;

/**
 * Excepción base para errores del módulo de unidades.
 * Extiende BusinessException para mantener consistencia en el manejo de errores de dominio.
 *
 * <p>Ejemplos de uso:</p>
 * <pre>
 * // Unidad no encontrada
 * throw new UnidadException(
 *     ErrorCode.UND_NOT_FOUND,
 *     "No existe unidad con ID: " + unidadId
 * );
 *
 * // Código duplicado
 * throw new UnidadException(
 *     ErrorCode.UND_DUPLICATE_CODE,
 *     "Ya existe una unidad con el código: " + codigo,
 *     "codigo"
 * );
 * </pre>
 *
 * @author Integra Development Team
 * @since 1.0.0
 */
public class UnidadException extends BusinessException {

    /**
     * Constructor con código de error y mensaje.
     *
     * @param errorCode el código de error específico del módulo
     * @param message el mensaje descriptivo del error
     */
    public UnidadException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    /**
     * Constructor con código de error, mensaje y causa.
     *
     * @param errorCode el código de error específico del módulo
     * @param message el mensaje descriptivo del error
     * @param cause la excepción causa original
     */
    public UnidadException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    /**
     * Constructor con código de error, mensaje y campo relacionado.
     *
     * @param errorCode el código de error específico del módulo
     * @param message el mensaje descriptivo del error
     * @param field el campo que causó el error
     */
    public UnidadException(ErrorCode errorCode, String message, String field) {
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
    public UnidadException(ErrorCode errorCode, String message, String field, Object rejectedValue) {
        super(errorCode, message, field, rejectedValue);
    }

    /**
     * Factory method para crear excepción de unidad no encontrada.
     *
     * @param unidadId el ID de la unidad buscada
     * @return UnidadException configurada
     */
    public static UnidadException notFound(Long unidadId) {
        return new UnidadException(
                ErrorCode.UND_NOT_FOUND,
                "No existe unidad con ID: " + unidadId
        );
    }

    /**
     * Factory method para crear excepción de código duplicado.
     *
     * @param codigo el código duplicado
     * @return UnidadException configurada
     */
    public static UnidadException duplicateCode(String codigo) {
        return new UnidadException(
                ErrorCode.UND_DUPLICATE_CODE,
                "Ya existe una unidad registrada con el código: " + codigo,
                "codigo",
                codigo
        );
    }

    /**
     * Factory method para crear excepción de unidad con empleados asignados.
     *
     * @param unidadId el ID de la unidad
     * @return UnidadException configurada
     */
    public static UnidadException hasEmployees(Long unidadId) {
        return new UnidadException(
                ErrorCode.UND_HAS_EMPLOYEES,
                "No se puede eliminar la unidad con ID " + unidadId + " porque tiene empleados asignados"
        );
    }

    /**
     * Factory method para crear excepción de unidad padre no encontrada.
     *
     * @param parentId el ID de la unidad padre
     * @return UnidadException configurada
     */
    public static UnidadException parentNotFound(Long parentId) {
        return new UnidadException(
                ErrorCode.UND_PARENT_NOT_FOUND,
                "No existe la unidad padre con ID: " + parentId
        );
    }

    /**
     * Factory method para crear excepción de referencia circular.
     *
     * @param unidadId el ID de la unidad
     * @param parentId el ID de la unidad padre que causaría circularidad
     * @return UnidadException configurada
     */
    public static UnidadException circularReference(Long unidadId, Long parentId) {
        return new UnidadException(
                ErrorCode.UND_CIRCULAR_REFERENCE,
                "No se puede asignar la unidad " + parentId + " como padre de " + unidadId + " porque crearía una referencia circular"
        );
    }

    /**
     * Factory method para crear excepción de código de unidad inválido.
     *
     * @param codigo el código inválido
     * @return UnidadException configurada
     */
    public static UnidadException invalidCode(String codigo) {
        return new UnidadException(
                ErrorCode.UND_INVALID_CODE,
                "El código de unidad no es válido: " + codigo,
                "codigo",
                codigo
        );
    }

    /**
     * Factory method para crear excepción de email duplicado.
     *
     * @param email el email duplicado
     * @return UnidadException configurada
     */
    public static UnidadException duplicateEmail(String email) {
        return new UnidadException(
                ErrorCode.DAT_DUPLICATE,
                "Ya existe una unidad registrada con el email: " + email,
                "email",
                email
        );
    }

    /**
     * Factory method para crear excepción de teléfono duplicado.
     *
     * @param telefono el teléfono duplicado
     * @return UnidadException configurada
     */
    public static UnidadException duplicateTelefono(String telefono) {
        return new UnidadException(
                ErrorCode.DAT_DUPLICATE,
                "Ya existe una unidad registrada con el teléfono: " + telefono,
                "telefono",
                telefono
        );
    }

    /**
     * Factory method para crear excepción de nombre duplicado.
     *
     * @param nombre el nombre duplicado
     * @return UnidadException configurada
     */
    public static UnidadException duplicateNombre(String nombre) {
        return new UnidadException(
                ErrorCode.DAT_DUPLICATE,
                "Ya existe una unidad registrada con el nombre: " + nombre,
                "nombre",
                nombre
        );
    }

    /**
     * Factory method para crear excepción de código de autorización duplicado.
     *
     * @param codigoAutorizacion el código de autorización duplicado
     * @return UnidadException configurada
     */
    public static UnidadException duplicateCodigoAutorizacion(String codigoAutorizacion) {
        return new UnidadException(
                ErrorCode.DAT_DUPLICATE,
                "El código de autorización ya está en uso: " + codigoAutorizacion,
                "codigoAutorizacion",
                codigoAutorizacion
        );
    }

    /**
     * Factory method para crear excepción de referencia inválida a zona.
     *
     * @param zonaId el ID de la zona inválida
     * @return UnidadException configurada
     */
    public static UnidadException zonaNotFound(Integer zonaId) {
        return new UnidadException(
                ErrorCode.DAT_INVALID_REFERENCE,
                "La zona con ID '" + zonaId + "' no existe o no es válida",
                "idZona",
                zonaId
        );
    }

    /**
     * Factory method para crear excepción de referencia inválida a estado.
     *
     * @param estadoId el ID del estado inválido
     * @return UnidadException configurada
     */
    public static UnidadException estadoNotFound(Integer estadoId) {
        return new UnidadException(
                ErrorCode.DAT_INVALID_REFERENCE,
                "El estado con ID '" + estadoId + "' no existe o no es válido",
                "idEstado",
                estadoId
        );
    }
}
