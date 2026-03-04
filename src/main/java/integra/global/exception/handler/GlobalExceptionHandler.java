package integra.global.exception.handler;

import integra.global.exception.BusinessException;
import integra.global.exception.IntegraException;
import integra.global.exception.TechnicalException;
import integra.global.exception.code.ErrorCode;
import integra.global.exception.response.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manejador global de excepciones para la aplicación Integra.
 *
 * <p>Implementa una estrategia centralizada de manejo de errores utilizando:
 * <ul>
 *   <li>RFC 7807 (Problem Details) para respuestas estandarizadas</li>
 *   <li>Jerarquía de excepciones por dominio</li>
 *   <li>Códigos de error centralizados</li>
 *   <li>Logging estructurado de errores</li>
 * </ul>
 *
 * <p>Este handler reemplaza y unifica los handlers anteriores:</p>
 * <ul>
 *   <li>LoginExceptionHandler</li>
 *   <li>KioscoHandlerException</li>
 *   <li>DataExceptionHandler</li>
 *   <li>GeneraProcessException</li>
 *   <li>UserAdminExceptionHandler</li>
 *   <li>SQLExceptionHandler</li>
 * </ul>
 *
 * @author Integra Development Team
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ============================================================================
    // EXCEPCIONES DE DOMINIO (IntegraException)
    // ============================================================================

    /**
     * Maneja excepciones base de Integra (BusinessException y TechnicalException).
     */
    @ExceptionHandler(IntegraException.class)
    public ResponseEntity<ProblemDetail> handleIntegraException(
            IntegraException ex,
            HttpServletRequest request) {

        log.warn("Excepción de dominio: [{}] {} - Path: {}",
                ex.getCode(), ex.getMessage(), request.getRequestURI());

        ApiError apiError = ApiError.fromErrorCode(
                ex.getErrorCode(),
                ex.getMessage(),
                request.getRequestURI()
        );

        // Agregar campo y valor rechazado si están disponibles
        if (ex instanceof BusinessException businessEx) {
            if (businessEx.getField() != null) {
                Map<String, Object> details = new HashMap<>();
                details.put("field", businessEx.getField());
                if (businessEx.getRejectedValue() != null) {
                    details.put("rejectedValue", businessEx.getRejectedValue());
                }
                apiError = ApiError.fromErrorCode(
                        ex.getErrorCode(),
                        ex.getMessage(),
                        request.getRequestURI(),
                        details
                );
            }
        }

        // Para errores técnicos 500, usar mensaje seguro
        String message = ex instanceof TechnicalException techEx
                ? techEx.getSafeMessage()
                : ex.getMessage();

        ProblemDetail problemDetail = apiError.toProblemDetail();
        problemDetail.setDetail(message);

        return ResponseEntity.status(ex.getHttpStatus()).body(problemDetail);
    }

    // ============================================================================
    // ERRORES DE VALIDACIÓN
    // ============================================================================

    /**
     * Maneja errores de validación de @Valid en request bodies.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() != null
                                ? fieldError.getDefaultMessage()
                                : "Valor inválido"
                ));

        log.warn("Error de validación en {}: {}", request.getRequestURI(), fieldErrors);

        Map<String, Object> details = new HashMap<>();
        details.put("fieldErrors", fieldErrors);

        ApiError apiError = ApiError.fromErrorCode(
                ErrorCode.VAL_INVALID_REQUEST,
                "La solicitud contiene errores de validación",
                request.getRequestURI(),
                details
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError.toProblemDetail());
    }

    /**
     * Maneja errores de validación de parámetros de método.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request) {

        Map<String, String> violations = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage
                ));

        log.warn("Violación de restricciones en {}: {}", request.getRequestURI(), violations);

        Map<String, Object> details = new HashMap<>();
        details.put("violations", violations);

        ApiError apiError = ApiError.fromErrorCode(
                ErrorCode.VAL_CONSTRAINT_VIOLATION,
                "La solicitud viola restricciones de validación",
                request.getRequestURI(),
                details
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError.toProblemDetail());
    }

    /**
     * Maneja errores de tipo de argumento (ej: String en lugar de Long).
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        String message = String.format("El parámetro '%s' debe ser de tipo %s",
                ex.getName(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "desconocido");

        log.warn("Error de tipo en {}: {}", request.getRequestURI(), message);

        ApiError apiError = ApiError.fromErrorCode(
                ErrorCode.VAL_INVALID_FORMAT,
                message,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError.toProblemDetail());
    }

    // ============================================================================
    // ERRORES DE SEGURIDAD
    // ============================================================================

    /**
     * Maneja errores de credenciales inválidas.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ProblemDetail> handleBadCredentials(
            BadCredentialsException ex,
            HttpServletRequest request) {

        log.warn("Credenciales inválidas en: {}", request.getRequestURI());

        ApiError apiError = ApiError.fromErrorCode(
                ErrorCode.AUTH_INVALID_CREDENTIALS,
                "Las credenciales proporcionadas son incorrectas",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError.toProblemDetail());
    }

    /**
     * Maneja errores de autenticación general.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ProblemDetail> handleAuthentication(
            AuthenticationException ex,
            HttpServletRequest request) {

        log.warn("Error de autenticación en {}: {}", request.getRequestURI(), ex.getMessage());

        ApiError apiError = ApiError.fromErrorCode(
                ErrorCode.AUTH_INVALID_CREDENTIALS,
                "No se pudo autenticar el usuario",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError.toProblemDetail());
    }

    /**
     * Maneja errores de acceso denegado (autorización).
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request) {

        log.warn("Acceso denegado en {}: {}", request.getRequestURI(), ex.getMessage());

        ApiError apiError = ApiError.fromErrorCode(
                ErrorCode.AUTH_ACCESS_DENIED,
                "No tiene permisos para acceder a este recurso",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiError.toProblemDetail());
    }

    // ============================================================================
    // ERRORES DE BASE DE DATOS
    // ============================================================================

    /**
     * Maneja violaciones de integridad de datos de base de datos.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {

        String causeMessage = ex.getMostSpecificCause() != null
                ? ex.getMostSpecificCause().getMessage()
                : "";

        String lowerCause = causeMessage.toLowerCase();

        if (lowerCause.contains("foreign key")) {
            log.warn("Violación de FK en {}: {}", request.getRequestURI(), causeMessage);

            ApiError apiError = ApiError.fromErrorCode(
                    ErrorCode.DAT_FK_VIOLATION,
                    "Este registro está siendo referenciado por otros datos. " +
                            "Elimine o actualice las referencias primero.",
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(apiError.toProblemDetail());
        }

        if (lowerCause.contains("duplicate entry") || lowerCause.contains("unique constraint")) {
            log.warn("Entrada duplicada en {}: {}", request.getRequestURI(), causeMessage);

            ApiError apiError = ApiError.fromErrorCode(
                    ErrorCode.DAT_DUPLICATE,
                    "Ya existe un registro con este valor. Por favor, ingrese un valor diferente.",
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(apiError.toProblemDetail());
        }

        log.error("Violación de integridad en {}: {}", request.getRequestURI(), causeMessage);

        ApiError apiError = ApiError.fromErrorCode(
                ErrorCode.TEC_DB_INTEGRITY,
                "Error de integridad de datos. Por favor, verifique la información.",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiError.toProblemDetail());
    }

    // ============================================================================
    // ERRORES DE RECURSOS
    // ============================================================================

    /**
     * Maneja recursos no encontrados (Spring 6+).
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ProblemDetail> handleNoResourceFound(
            NoResourceFoundException ex,
            HttpServletRequest request) {

        log.warn("Recurso no encontrado: {} {}", ex.getHttpMethod(), ex.getResourcePath());

        ApiError apiError = ApiError.fromErrorCode(
                ErrorCode.DAT_NOT_FOUND,
                "El recurso solicitado no existe: " + ex.getResourcePath(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError.toProblemDetail());
    }

    // ============================================================================
    // EXCEPCIONES NO CONTROLADAS
    // ============================================================================

    /**
     * Manejador de último recurso para excepciones no controladas.
     * NO expone detalles técnicos sensibles.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        log.error("Error no controlado en {}: {}", request.getRequestURI(), ex.getMessage(), ex.getCause());

        ApiError apiError = ApiError.fromErrorCode(
                ErrorCode.GEN_INTERNAL_ERROR,
                "Ha ocurrido un error interno. Por favor, contacte al administrador del sistema.",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError.toProblemDetail());
    }
}
