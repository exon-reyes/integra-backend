package integra.global.exception.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import integra.global.exception.code.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ProblemDetail;

import java.net.URI;
import java.time.Instant;
import java.util.Map;

/**
 * Modelo estándar de respuesta de error para la API.
 * Implementa RFC 7807 (Problem Details) usando Spring 6+ ProblemDetail.
 *
 * <p>Estructura de respuesta:</p>
 * <pre>
 * {
 *   "errorCode": "EMP-001",
 *   "title": "Empleado no encontrado",
 *   "message": "No existe empleado con ID: 12345",
 *   "httpStatus": 404,
 *   "path": "/api/empleados/12345",
 *   "timestamp": "2024-01-15T10:30:00Z",
 *   "details": { ... }
 * }
 * </pre>
 *
 * @author Integra Development Team
 * @since 1.0.0
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

    /**
     * Código de error estandarizado (ej: EMP-001, AUT-002)
     */
    private final String errorCode;

    /**
     * Título del error para presentación al usuario
     */
    private final String title;

    /**
     * Mensaje descriptivo del error
     */
    private final String message;

    /**
     * Estado HTTP asociado
     */
    private final int httpStatus;

    /**
     * URI del recurso que generó el error
     */
    private final String path;

    /**
     * Timestamp del error en formato ISO-8601
     */
    private final Instant timestamp;

    /**
     * Detalles adicionales específicos del error
     */
    private final Map<String, Object> details;

    /**
     * URI de tipo del problema (RFC 7807)
     */
    private final URI type;

    /**
     * Campo relacionado con el error (para errores de validación)
     */
    private final String field;

    /**
     * Valor rechazado (para errores de validación)
     */
    private final Object rejectedValue;

    /**
     * Crea un ApiError a partir de un ErrorCode.
     *
     * @param errorCode el código de error
     * @param message el mensaje personalizado
     * @param path el path de la solicitud
     * @return ApiError configurado
     */
    public static ApiError fromErrorCode(ErrorCode errorCode, String message, String path) {
        return ApiError.builder()
                .errorCode(errorCode.getCode())
                .title(errorCode.getTitle())
                .message(message != null ? message : errorCode.getTitle())
                .httpStatus(errorCode.getHttpStatus())
                .path(path)
                .timestamp(Instant.now())
                .type(URI.create("about:blank"))
                .build();
    }

    /**
     * Crea un ApiError a partir de un ErrorCode con detalles adicionales.
     *
     * @param errorCode el código de error
     * @param message el mensaje personalizado
     * @param path el path de la solicitud
     * @param details detalles adicionales
     * @return ApiError configurado
     */
    public static ApiError fromErrorCode(ErrorCode errorCode, String message, String path, Map<String, Object> details) {
        return ApiError.builder()
                .errorCode(errorCode.getCode())
                .title(errorCode.getTitle())
                .message(message != null ? message : errorCode.getTitle())
                .httpStatus(errorCode.getHttpStatus())
                .path(path)
                .timestamp(Instant.now())
                .details(details)
                .type(URI.create("about:blank"))
                .build();
    }

    /**
     * Convierte este ApiError a un ProblemDetail de Spring 6+.
     *
     * @return ProblemDetail para respuesta HTTP
     */
    public ProblemDetail toProblemDetail() {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                org.springframework.http.HttpStatusCode.valueOf(httpStatus),
                message
        );

        problemDetail.setTitle(title);
        problemDetail.setType(type);
        problemDetail.setProperty("errorCode", errorCode);
        problemDetail.setProperty("timestamp", timestamp.toString());
        problemDetail.setProperty("path", path);

        if (field != null) {
            problemDetail.setProperty("field", field);
        }

        if (rejectedValue != null) {
            problemDetail.setProperty("rejectedValue", rejectedValue);
        }

        if (details != null && !details.isEmpty()) {
            problemDetail.setProperty("details", details);
        }

        return problemDetail;
    }

    /**
     * Crea un builder preconfigurado para errores de validación.
     *
     * @param errorCode el código de error
     * @param field el campo con error
     * @param rejectedValue el valor rechazado
     * @param path el path de la solicitud
     * @return ApiError configurado
     */
    public static ApiError validationError(ErrorCode errorCode, String field, Object rejectedValue, String path) {
        return ApiError.builder()
                .errorCode(errorCode.getCode())
                .title(errorCode.getTitle())
                .message("El campo '" + field + "' tiene un valor inválido")
                .httpStatus(errorCode.getHttpStatus())
                .path(path)
                .timestamp(Instant.now())
                .field(field)
                .rejectedValue(rejectedValue)
                .type(URI.create("about:blank"))
                .build();
    }
}
