package integra.global.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Códigos de error estandarizados para toda la aplicación Integra.
 *
 * <p>Formato de códigos:</p>
 * <ul>
 *   <li><b>GEN</b>: Errores generales del sistema</li>
 *   <li><b>VAL</b>: Errores de validación</li>
 *   <li><b>AUT</b>: Errores de autenticación y autorización</li>
 *   <li><b>BUS</b>: Errores de reglas de negocio</li>
 *   <li><b>DAT</b>: Errores de datos (no encontrado, duplicado)</li>
 *   <li><b>TEC</b>: Errores técnicos/infraestructura</li>
 *   <li><b>EMP</b>: Errores del módulo de empleados</li>
 *   <li><b>EMR</b>: Errores del módulo de empresa</li>
 *   <li><b>UND</b>: Errores del módulo de unidades</li>
 *   <li><b>ACC</b>: Errores del módulo de acceso</li>
 *   <li><b>REP</b>: Errores del módulo de reportes</li>
 *   <li><b>ASI</b>: Errores del módulo de asistencia</li>
 *   <li><b>OBS</b>: Errores del módulo de observaciones</li>
 * </ul>
 *
 * @author Integra Development Team
 * @since 1.0.0
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // ============================================================================
    // ERRORES GENERALES (GEN)
    // ============================================================================
    GEN_INTERNAL_ERROR("GEN-001", "Error interno del sistema", 500),
    GEN_SERVICE_UNAVAILABLE("GEN-002", "Servicio no disponible", 503),
    GEN_TIMEOUT("GEN-003", "Tiempo de espera agotado", 504),
    GEN_NOT_IMPLEMENTED("GEN-004", "Funcionalidad no implementada", 501),

    // ============================================================================
    // ERRORES DE VALIDACIÓN (VAL)
    // ============================================================================
    VAL_INVALID_REQUEST("VAL-001", "Solicitud inválida", 400),
    VAL_MISSING_FIELD("VAL-002", "Campo requerido no proporcionado", 400),
    VAL_INVALID_FORMAT("VAL-003", "Formato de datos inválido", 400),
    VAL_INVALID_VALUE("VAL-004", "Valor no válido", 400),
    VAL_CONSTRAINT_VIOLATION("VAL-005", "Violación de restricción", 400),

    // ============================================================================
    // ERRORES DE AUTENTICACIÓN Y AUTORIZACIÓN (AUT)
    // ============================================================================
    AUTH_INVALID_CREDENTIALS("AUT-001", "Credenciales inválidas", 401),
    AUTH_TOKEN_EXPIRED("AUT-002", "Token de autenticación expirado", 401),
    AUTH_TOKEN_INVALID("AUT-003", "Token de autenticación inválido", 401),
    AUTH_ACCESS_DENIED("AUT-004", "Acceso denegado", 403),
    AUTH_INSUFFICIENT_PERMISSIONS("AUT-005", "Permisos insuficientes", 403),
    AUTH_ACCOUNT_LOCKED("AUT-006", "Cuenta bloqueada", 403),
    AUTH_ACCOUNT_DISABLED("AUT-007", "Cuenta deshabilitada", 403),
    AUTH_PASSWORD_EXPIRED("AUT-008", "Contraseña expirada", 403),
    AUTH_TOO_MANY_REQUESTS("AUT-009", "Demasiadas solicitudes", 429),
    AUTH_PASSWORD_RESET_BLOCKED("AUT-010", "Restablecimiento de contraseña bloqueado", 429),
    AUTH_INVALID_RESET_TOKEN("AUT-011", "Token de restablecimiento inválido", 400),

    // ============================================================================
    // ERRORES DE REGLAS DE NEGOCIO (BUS)
    // ============================================================================
    BUS_OPERATION_NOT_ALLOWED("BUS-001", "Operación no permitida", 409),
    BUS_INSUFFICIENT_BALANCE("BUS-002", "Saldo insuficiente", 409),
    BUS_LIMIT_EXCEEDED("BUS-003", "Límite excedido", 409),
    BUS_INVALID_STATE("BUS-004", "Estado inválido para la operación", 409),
    BUS_DEPENDENCY_VIOLATION("BUS-005", "Violación de dependencias", 409),

    // ============================================================================
    // ERRORES DE DATOS (DAT)
    // ============================================================================
    DAT_NOT_FOUND("DAT-001", "Recurso no encontrado", 404),
    DAT_DUPLICATE("DAT-002", "Recurso duplicado", 409),
    DAT_FK_VIOLATION("DAT-003", "Violación de clave foránea", 409),
    DAT_INVALID_REFERENCE("DAT-004", "Referencia inválida", 400),
    DAT_STALE_DATA("DAT-005", "Datos desactualizados", 409),

    // ============================================================================
    // ERRORES TÉCNICOS (TEC)
    // ============================================================================
    TEC_DB_CONNECTION("TEC-001", "Error de conexión a base de datos", 500),
    TEC_DB_QUERY("TEC-002", "Error en consulta a base de datos", 500),
    TEC_DB_INTEGRITY("TEC-003", "Error de integridad de datos", 500),
    TEC_CACHE_ERROR("TEC-004", "Error de caché", 500),
    TEC_EXTERNAL_SERVICE("TEC-005", "Error en servicio externo", 502),
    TEC_FILE_PROCESSING("TEC-006", "Error al procesar archivo", 500),
    TEC_EMAIL_SENDING("TEC-007", "Error al enviar correo", 500),

    // ============================================================================
    // ERRORES DE EMPLEADOS (EMP)
    // ============================================================================
    EMP_NOT_FOUND("EMP-001", "Empleado no encontrado", 404),
    EMP_DUPLICATE_NIP("EMP-002", "Ya existe un empleado con ese NIP", 409),
    EMP_DUPLICATE_EMAIL("EMP-003", "Ya existe un empleado con ese correo", 409),
    EMP_INVALID_NIP("EMP-004", "NIP de empleado inválido", 400),
    EMP_NOT_ACTIVE("EMP-005", "El empleado no está activo", 409),
    EMP_ALREADY_ASSIGNED("EMP-006", "El empleado ya tiene una asignación activa", 409),

    // ============================================================================
    // ERRORES DE EMPRESA (EMR)
    // ============================================================================
    EMR_NOT_FOUND("EMR-001", "Empresa no encontrada", 404),
    EMR_DUPLICATE_RFC("EMR-002", "Ya existe una empresa con ese RFC", 409),
    EMR_INVALID_RFC("EMR-003", "RFC inválido", 400),
    EMR_HAS_DEPARTMENTS("EMR-004", "La empresa tiene departamentos asociados", 409),

    // ============================================================================
    // ERRORES DE UNIDADES (UND)
    // ============================================================================
    UND_NOT_FOUND("UND-001", "Unidad no encontrada", 404),
    UND_DUPLICATE_CODE("UND-002", "Ya existe una unidad con ese código", 409),
    UND_INVALID_CODE("UND-003", "Código de unidad inválido", 400),
    UND_HAS_EMPLOYEES("UND-004", "La unidad tiene empleados asignados", 409),
    UND_PARENT_NOT_FOUND("UND-005", "Unidad padre no encontrada", 404),
    UND_CIRCULAR_REFERENCE("UND-006", "Referencia circular detectada", 400),

    // ============================================================================
    // ERRORES DE ACCESO (ACC)
    // ============================================================================
    ACC_USER_NOT_FOUND("ACC-001", "Usuario no encontrado", 404),
    ACC_USER_DUPLICATE("ACC-002", "Ya existe un usuario con ese identificador", 409),
    ACC_ROLE_NOT_FOUND("ACC-003", "Rol no encontrado", 404),
    ACC_ROLE_DUPLICATE("ACC-004", "Ya existe un rol con ese nombre", 409),
    ACC_ROLE_ASSIGNED("ACC-005", "El rol tiene usuarios asignados", 409),
    ACC_PASSWORD_MISMATCH("ACC-006", "Las contraseñas no coinciden", 400),
    ACC_PASSWORD_WEAK("ACC-007", "La contraseña no cumple con los requisitos de seguridad", 400),
    ACC_SESSION_EXPIRED("ACC-008", "Sesión expirada", 401),

    // ============================================================================
    // ERRORES DE REPORTES (REP)
    // ============================================================================
    REP_NOT_FOUND("REP-001", "Reporte no encontrado", 404),
    REP_GENERATION_FAILED("REP-002", "Error al generar el reporte", 500),
    REP_INVALID_DATE_RANGE("REP-003", "Rango de fechas inválido", 400),
    REP_EMPTY_RESULT("REP-004", "El reporte no contiene datos", 404),
    REP_EXPORT_FAILED("REP-005", "Error al exportar el reporte", 500),

    // ============================================================================
    // ERRORES DE ASISTENCIA (ASI)
    // ============================================================================
    ASI_NOT_FOUND("ASI-001", "Registro de asistencia no encontrado", 404),
    ASI_DUPLICATE_ENTRY("ASI-002", "Registro de asistencia duplicado", 409),
    ASI_INVALID_TIME("ASI-003", "Hora de registro inválida", 400),
    ASI_OUTSIDE_SCHEDULE("ASI-004", "Registro fuera del horario permitido", 409),
    ASI_KIOSCO_NOT_CONFIGURED("ASI-005", "Kiosco no configurado", 400),
    ASI_KIOSCO_INVALID_PIN("ASI-006", "PIN de kiosco inválido", 401),
    ASI_KIOSCO_BLOCKED("ASI-007", "Kiosco bloqueado", 403);

    private final String code;
    private final String title;
    private final int httpStatus;

    /**
     * Busca un ErrorCode por su código string.
     *
     * @param code el código a buscar
     * @return el ErrorCode encontrado o GEN_INTERNAL_ERROR si no existe
     */
    public static ErrorCode fromCode(String code) {
        for (ErrorCode errorCode : values()) {
            if (errorCode.getCode().equals(code)) {
                return errorCode;
            }
        }
        return GEN_INTERNAL_ERROR;
    }
}
