package integra.vacacion.exception;

import integra.global.exception.BusinessException;
import integra.global.exception.code.ErrorCode;

import java.time.LocalDate;

public class VacacionException extends BusinessException {

    public VacacionException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public VacacionException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    public VacacionException(ErrorCode errorCode, String message, String field, Object rejectedValue) {
        super(errorCode, message, field, rejectedValue);
    }

    public static VacacionException antiguedadInsuficiente() {
        return new VacacionException(
                ErrorCode.VAC_ANTIGUEDAD_INSUFICIENTE,
                "El empleado no cumple con el año mínimo de antigüedad requerido"
        );
    }

    public static VacacionException saldoInsuficiente(int disponible, int solicitado) {
        return new VacacionException(
                ErrorCode.VAC_SALDO_INSUFICIENTE,
                String.format("Saldo insuficiente: tiene %d días disponibles pero solicita %d", disponible, solicitado),
                "diasSolicitados", solicitado
        );
    }

    public static VacacionException periodoVedaActivo() {
        return new VacacionException(
                ErrorCode.VAC_PERIODO_VEDA,
                "Las solicitudes están bloqueadas en este período (veda)"
        );
    }

    public static VacacionException plazoLegalVencido(LocalDate ultimoAniversario) {
        return new VacacionException(
                ErrorCode.VAC_PLAZO_VENCIDO,
                String.format("El plazo legal de 6 meses posterior al aniversario (%s) ha vencido", ultimoAniversario)
        );
    }

    public static VacacionException solicitudNoEncontrada(Long solicitudId) {
        return new VacacionException(
                ErrorCode.VAC_SOLICITUD_NO_ENCONTRADA,
                String.format("Solicitud de vacaciones no encontrada con ID: %d", solicitudId)
        );
    }

    public static VacacionException estadoInvalido(String operacion, String estadoActual) {
        return new VacacionException(
                ErrorCode.VAC_ESTADO_INVALIDO,
                String.format("No se puede %s. La solicitud tiene estado: %s", operacion, estadoActual)
        );
    }

    public static VacacionException sinPeriodoActivo() {
        return new VacacionException(
                ErrorCode.VAC_SIN_PERIODO,
                "No existe período vacacional activo para el empleado"
        );
    }

    public static VacacionException traslapeDetectado() {
        return new VacacionException(
                ErrorCode.VAC_TRASLAPE,
                "Las fechas solicitadas coinciden con otras vacaciones del empleado"
        );
    }

    public static VacacionException festivoDuplicado(LocalDate fecha) {
        return new VacacionException(
                ErrorCode.VAC_FESTIVO_DUPLICADO,
                String.format("Ya existe un día festivo registrado en la fecha: %s", fecha),
                "fecha", fecha
        );
    }

    /**
     * Empleado no encontrado en el sistema.
     *
     * @param empleadoId el ID del empleado buscado
     * @return VacacionException configurada
     */
    public static VacacionException empleadoNoEncontrado(Integer empleadoId) {
        return new VacacionException(
                ErrorCode.VAC_EMPLEADO_NO_ENCONTRADO,
                String.format("Empleado no encontrado con ID: %d", empleadoId)
        );
    }

    /**
     * Usuario sin permisos para realizar la operación.
     *
     * @param operacion la operación que se intentó realizar
     * @return VacacionException configurada
     */
    public static VacacionException sinPermisos(String operacion) {
        return new VacacionException(
                ErrorCode.VAC_SIN_PERMISOS,
                String.format("No tiene permisos para %s", operacion)
        );
    }

    /**
     * Rango de fechas inválido para la solicitud.
     *
     * @param mensaje detalle adicional del error
     * @return VacacionException configurada
     */
    public static VacacionException fechaInvalida(String mensaje) {
        return new VacacionException(
                ErrorCode.VAC_FECHA_INVALIDA,
                mensaje
        );
    }

    /**
     * Gestor/Aprobador no encontrado.
     *
     * @param gestorId el ID del gestor buscado
     * @return VacacionException configurada
     */
    public static VacacionException gestorNoEncontrado(Integer gestorId) {
        return new VacacionException(
                ErrorCode.VAC_GESTOR_NO_ENCONTRADO,
                String.format("Gestor/Aprobador no encontrado con ID: %d", gestorId)
        );
    }

    /**
     * Período vacacional no encontrado.
     *
     * @param periodoId el ID del período buscado
     * @return VacacionException configurada
     */
    public static VacacionException periodoNoEncontrado(Long periodoId) {
        return new VacacionException(
                ErrorCode.VAC_PERIODO_NO_ENCONTRADO,
                String.format("Período vacacional no encontrado con ID: %d", periodoId)
        );
    }

    /**
     * Error técnico al procesar la solicitud.
     *
     * @param mensaje mensaje de error
     * @param cause   causa original del error
     * @return VacacionException configurada
     */
    public static VacacionException errorTecnico(String mensaje, Throwable cause) {
        return new VacacionException(
                ErrorCode.VAC_ERROR_TECNICO,
                mensaje,
                cause
        );
    }

    public static VacacionException descansosNoConfigurados() {
        return new VacacionException(
                ErrorCode.VAC_DESCANSOS_NO_CONFIGURADOS,
                "Debe configurar sus días de descanso antes de solicitar vacaciones"
        );
    }
}
