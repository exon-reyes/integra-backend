package integra.acceso.exception;

import integra.global.exception.BusinessException;
import integra.global.exception.code.ErrorCode;

/**
 * Excepción base para errores del módulo de acceso (autenticación y autorización).
 * Extiende BusinessException para mantener consistencia en el manejo de errores de dominio.
 *
 * <p>Esta excepción unifica y reemplaza las excepciones anteriores:</p>
 * <ul>
 *   <li>CreateUserException</li>
 *   <li>InvalidPasswordResetTokenException</li>
 *   <li>PasswordResetBlockedException</li>
 * </ul>
 *
 * <p>Ejemplos de uso:</p>
 * <pre>
 * // Usuario no encontrado
 * throw AccesoException.userNotFound(username);
 *
 * // Usuario duplicado
 * throw AccesoException.duplicateUser(username);
 *
 * // Token inválido
 * throw AccesoException.invalidResetToken();
 * </pre>
 *
 * @author Integra Development Team
 * @since 1.0.0
 */
public class AccesoException extends BusinessException {

    /**
     * Constructor con código de error y mensaje.
     *
     * @param errorCode el código de error específico del módulo
     * @param message el mensaje descriptivo del error
     */
    public AccesoException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    /**
     * Constructor con código de error, mensaje y causa.
     *
     * @param errorCode el código de error específico del módulo
     * @param message el mensaje descriptivo del error
     * @param cause la excepción causa original
     */
    public AccesoException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    /**
     * Constructor con código de error, mensaje y campo relacionado.
     *
     * @param errorCode el código de error específico del módulo
     * @param message el mensaje descriptivo del error
     * @param field el campo que causó el error
     */
    public AccesoException(ErrorCode errorCode, String message, String field) {
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
    public AccesoException(ErrorCode errorCode, String message, String field, Object rejectedValue) {
        super(errorCode, message, field, rejectedValue);
    }

    // ==================== FACTORY METHODS ====================

    /**
     * Factory method para crear excepción de usuario no encontrado.
     *
     * @param username el nombre de usuario buscado
     * @return AccesoException configurada
     */
    public static AccesoException userNotFound(String username) {
        return new AccesoException(
                ErrorCode.ACC_USER_NOT_FOUND,
                "No existe usuario con identificador: " + username
        );
    }

    /**
     * Factory method para crear excepción de usuario duplicado.
     *
     * @param username el nombre de usuario duplicado
     * @return AccesoException configurada
     */
    public static AccesoException duplicateUser(String username) {
        return new AccesoException(
                ErrorCode.ACC_USER_DUPLICATE,
                "Ya existe un usuario registrado con el identificador: " + username,
                "username",
                username
        );
    }

    /**
     * Factory method para crear excepción de usuario duplicado con mensaje personalizado.
     *
     * @param message mensaje personalizado del error
     * @return AccesoException configurada
     */
    public static AccesoException duplicateUserByMessage(String message) {
        return new AccesoException(
                ErrorCode.ACC_USER_DUPLICATE,
                message
        );
    }

    /**
     * Factory method para crear excepción de rol no encontrado.
     *
     * @param roleId el ID del rol buscado
     * @return AccesoException configurada
     */
    public static AccesoException roleNotFound(Long roleId) {
        return new AccesoException(
                ErrorCode.ACC_ROLE_NOT_FOUND,
                "No existe rol con ID: " + roleId
        );
    }

    /**
     * Factory method para crear excepción de rol duplicado.
     *
     * @param roleName el nombre del rol duplicado
     * @return AccesoException configurada
     */
    public static AccesoException duplicateRole(String roleName) {
        return new AccesoException(
                ErrorCode.ACC_ROLE_DUPLICATE,
                "Ya existe un rol registrado con el nombre: " + roleName,
                "nombre",
                roleName
        );
    }

    /**
     * Factory method para crear excepción de rol con usuarios asignados.
     *
     * @param roleId el ID del rol
     * @return AccesoException configurada
     */
    public static AccesoException roleHasUsers(Long roleId) {
        return new AccesoException(
                ErrorCode.ACC_ROLE_ASSIGNED,
                "No se puede eliminar el rol con ID " + roleId + " porque tiene usuarios asignados"
        );
    }

    /**
     * Factory method para crear excepción de contraseñas no coincidentes.
     *
     * @return AccesoException configurada
     */
    public static AccesoException passwordMismatch() {
        return new AccesoException(
                ErrorCode.ACC_PASSWORD_MISMATCH,
                "Las contraseñas proporcionadas no coinciden"
        );
    }

    /**
     * Factory method para crear excepción de contraseña débil.
     *
     * @param details detalles de los requisitos no cumplidos
     * @return AccesoException configurada
     */
    public static AccesoException weakPassword(String details) {
        return new AccesoException(
                ErrorCode.ACC_PASSWORD_WEAK,
                "La contraseña no cumple con los requisitos de seguridad: " + details
        );
    }

    /**
     * Factory method para crear excepción de token de restablecimiento inválido.
     *
     * @return AccesoException configurada
     */
    public static AccesoException invalidResetToken() {
        return new AccesoException(
                ErrorCode.AUTH_INVALID_RESET_TOKEN,
                "El token de restablecimiento es inválido, ha expirado o ya fue utilizado"
        );
    }

    /**
     * Factory method para crear excepción de restablecimiento bloqueado.
     *
     * @param reason razón del bloqueo
     * @return AccesoException configurada
     */
    public static AccesoException passwordResetBlocked(String reason) {
        return new AccesoException(
                ErrorCode.AUTH_PASSWORD_RESET_BLOCKED,
                "Restablecimiento de contraseña bloqueado: " + reason
        );
    }

    /**
     * Factory method para crear excepción de sesión expirada.
     *
     * @return AccesoException configurada
     */
    public static AccesoException sessionExpired() {
        return new AccesoException(
                ErrorCode.ACC_SESSION_EXPIRED,
                "La sesión ha expirado. Por favor, inicie sesión nuevamente"
        );
    }

    /**
     * Factory method para crear excepción de acceso denegado.
     *
     * @param reason razón del acceso denegado
     * @return AccesoException configurada
     */
    public static AccesoException accessDenied(String reason) {
        return new AccesoException(
                ErrorCode.AUTH_ACCESS_DENIED,
                reason
        );
    }
}
