package integra.vacacion.service.validation;

import integra.vacacion.domain.model.Solicitud;
import integra.vacacion.exception.VacacionException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servicio centralizado de validaciones para operaciones de descanso.
 * <p>
 * Agrupa todas las reglas de negocio que deben verificarse antes de
 * persistir o modificar un {@code DescansoEmpleadoEntity}, manteniendo
 * los servicios de comando limpios y reutilizando las mismas reglas
 * desde cualquier punto de la aplicación.
 */
@Service
public class DescansoValidationService {

    /**
     * Verifica que una fecha de descanso no sea anterior al día de hoy.
     * <p>
     * Un descanso solo puede solicitarse para hoy en adelante; solicitar
     * un descanso con fecha pasada no tiene sentido operativo y debe
     * rechazarse explícitamente.
     *
     * @param fecha la fecha propuesta para el descanso
     * @throws VacacionException con código {@code VAC_FECHA_INVALIDA}
     *                           si {@code fecha} es anterior a {@link LocalDate#now()}
     */
    public void validarFechaNoPasada(LocalDate fecha) {
        if (fecha.isBefore(LocalDate.now())) {
            throw VacacionException.fechaInvalida(String.format("La fecha de descanso '%s' no puede ser anterior al día de hoy (%s).", fecha, LocalDate.now()));
        }
    }

    /**
     * Verifica que <strong>todas</strong> las fechas de una colección sean
     * iguales o posteriores al día de hoy.
     * <p>
     * Itera sobre cada elemento y delega en {@link #validarFechaNoPasada(LocalDate)},
     * por lo que lanza la excepción en cuanto encuentra la primera fecha inválida.
     *
     * @param fechas colección de fechas a validar (no debe ser {@code null})
     * @throws VacacionException con código {@code VAC_FECHA_INVALIDA}
     *                           si alguna fecha es anterior a hoy
     */
    public void validarFechasNoPasadas(Collection<LocalDate> fechas) {
        fechas.forEach(this::validarFechaNoPasada);
    }

    /**
     * Verifica que la colección de fechas no esté vacía.
     *
     * @param fechas colección a validar
     * @throws VacacionException con código {@code VAC_FECHA_INVALIDA}
     *                           si la colección es nula o no contiene elementos
     */
    public void validarFechasNoVacias(Collection<LocalDate> fechas) {
        if (fechas == null || fechas.isEmpty()) {
            throw VacacionException.fechaInvalida("Debe proporcionar al menos una fecha de descanso.");
        }
    }

    /**
     * Ejecuta todas las validaciones de fecha de manera encadenada:
     * <ol>
     *   <li>La colección no debe estar vacía.</li>
     *   <li>Ninguna fecha debe ser anterior a hoy.</li>
     * </ol>
     *
     * @param fechas colección de fechas propuestas para los descansos
     * @throws VacacionException si alguna validación falla
     */
    public void validarFechasDescanso(Collection<LocalDate> fechas) {
        validarFechasNoVacias(fechas);
        validarFechasNoPasadas(fechas);
    }

    /**
     * Filtra una lista de fechas de descanso eliminando aquellas que interfieren
     * con alguna solicitud de vacaciones aprobada o pendiente.
     * <p>
     * Un día de descanso "interfiere" con una solicitud cuando cae dentro del
     * rango {@code [solicitud.fechaInicio, solicitud.fechaFin]} (ambos inclusive).
     * Los días que no tienen conflicto con ninguna solicitud son devueltos sin cambios.
     *
     * @param solicitudes    lista de solicitudes de vacaciones del empleado
     *                       (puede estar vacía, en cuyo caso se devuelven todas las fechas)
     * @param fechasDescanso lista de fechas candidatas para el descanso
     * @return lista de fechas de descanso que <strong>no</strong> se solapan
     * con ninguna solicitud de vacaciones
     */
    public Set<LocalDate> filtrarDescansosSinConflictoVacaciones(List<Solicitud> solicitudes, Set<LocalDate> fechasDescanso) {

        if (solicitudes == null || solicitudes.isEmpty()) {
            return fechasDescanso;
        }

        return fechasDescanso.stream()
                .filter(fecha -> solicitudes.stream()
                        .noneMatch(s -> !fecha.isBefore(s.getFechaInicio()) && !fecha.isAfter(s.getFechaFin())))
                .collect(Collectors.toSet());
    }
}
