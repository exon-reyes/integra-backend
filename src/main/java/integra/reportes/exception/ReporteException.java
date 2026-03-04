package integra.reportes.exception;

import integra.global.exception.BusinessException;
import integra.global.exception.TechnicalException;
import integra.global.exception.code.ErrorCode;

/**
 * Excepción base para errores del módulo de reportes.
 * Puede ser de negocio (BusinessException) o técnica (TechnicalException) según el contexto.
 *
 * <p>Ejemplos de uso:</p>
 * <pre>
 * // Reporte no encontrado (negocio)
 * throw ReporteException.notFound(reporteId);
 *
 * // Error al generar reporte (técnico)
 * throw ReporteException.generationFailed("Error al procesar plantilla", exception);
 *
 * // Rango de fechas inválido (negocio)
 * throw ReporteException.invalidDateRange(fechaInicio, fechaFin);
 * </pre>
 *
 * @author Integra Development Team
 * @since 1.0.0
 */
public class ReporteException {

    private ReporteException() {
        // Utility class
    }

    /**
     * Crea una excepción de negocio para reporte no encontrado.
     *
     * @param reporteId el ID del reporte buscado
     * @return BusinessException configurada
     */
    public static BusinessException notFound(Long reporteId) {
        return new BusinessException(
                ErrorCode.REP_NOT_FOUND,
                "No existe reporte con ID: " + reporteId
        );
    }

    /**
     * Crea una excepción técnica para error al generar reporte.
     *
     * @param message mensaje descriptivo
     * @param cause la causa original
     * @return TechnicalException configurada
     */
    public static TechnicalException generationFailed(String message, Throwable cause) {
        return new TechnicalException(
                ErrorCode.REP_GENERATION_FAILED,
                "Error al generar el reporte: " + message,
                cause
        );
    }

    /**
     * Crea una excepción de negocio para rango de fechas inválido.
     *
     * @param fechaInicio fecha de inicio
     * @param fechaFin fecha de fin
     * @return BusinessException configurada
     */
    public static BusinessException invalidDateRange(String fechaInicio, String fechaFin) {
        return new BusinessException(
                ErrorCode.REP_INVALID_DATE_RANGE,
                "El rango de fechas es inválido: " + fechaInicio + " a " + fechaFin
        );
    }

    /**
     * Crea una excepción de negocio para reporte sin datos.
     *
     * @param reporteId el ID del reporte
     * @return BusinessException configurada
     */
    public static BusinessException emptyResult(Long reporteId) {
        return new BusinessException(
                ErrorCode.REP_EMPTY_RESULT,
                "El reporte con ID " + reporteId + " no contiene datos para los criterios seleccionados"
        );
    }

    /**
     * Crea una excepción técnica para error al exportar reporte.
     *
     * @param format formato de exportación
     * @param cause la causa original
     * @return TechnicalException configurada
     */
    public static TechnicalException exportFailed(String format, Throwable cause) {
        return new TechnicalException(
                ErrorCode.REP_EXPORT_FAILED,
                "Error al exportar el reporte al formato " + format,
                cause
        );
    }
}
