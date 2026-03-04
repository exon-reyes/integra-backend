package integra.asistencia.service;
import integra.asistencia.actions.EmpleadosSinAsistenciaCommand;
import integra.asistencia.actions.InasistenciaPorFechaResponse;
import integra.asistencia.exception.AsistenciaDomainException;
import integra.asistencia.factory.EmpleadosSinAsistenciaFactory;
import integra.empleado.entity.EmpleadoEntity;
import integra.empleado.repository.EmpleadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
/**
 * Servicio para obtener empleados que no tienen registros de asistencia en un
 * rango de fechas.
 * Agrupa los empleados por cada fecha de inasistencia.
 *
 * @author Sistema Integra
 * @version 2.0
 */
@Service
@RequiredArgsConstructor
public class ObtenerEmpleadosSinAsistenciaService {
    private final EmpleadoRepository empleadoRepository;
    /**
     * Ejecuta la consulta de empleados sin asistencia en el rango de fechas
     * especificado.
     * Retorna los empleados agrupados por cada fecha en la que no tuvieron
     * asistencia.
     *
     * @param command Comando con los parámetros de búsqueda
     * @return Lista de inasistencias agrupadas por fecha
     * @throws AsistenciaDomainException si la fecha de inicio es posterior a la
     *                                  fecha de fin
     */
    @Transactional(readOnly = true)
    public List<InasistenciaPorFechaResponse> execute(EmpleadosSinAsistenciaCommand command) {
        validarFechas(command.getFechaInicio(), command.getFechaFin());
        // Obtener empleados sin asistencia en todo el rango
        List<EmpleadoEntity> empleadosSinAsistencia = empleadoRepository.findEmpleadosSinAsistenciaEnRango(
                "B", // Excluir empleados con estatus de baja
                command.getFechaInicio(),
                command.getFechaFin(),
                command.getUnidadId(),
                command.getPuestoId(),
                command.getZonaId(),
                command.getSupervisorId());
        // Generar todas las fechas del rango
        List<LocalDate> todasLasFechas = generarRangoFechas(command.getFechaInicio(), command.getFechaFin());
        // Agrupar empleados por fecha
        return todasLasFechas.stream()
                .map(fecha -> new InasistenciaPorFechaResponse(
                        fecha,
                        empleadosSinAsistencia.stream()
                                .map(EmpleadosSinAsistenciaFactory::mapEntityToResponse)
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }
    private void validarFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio.isAfter(fechaFin)) {
            throw AsistenciaDomainException.invalidDateRange(fechaInicio.toString(), fechaFin.toString());
        }
    }
    private List<LocalDate> generarRangoFechas(LocalDate inicio, LocalDate fin) {
        List<LocalDate> fechas = new ArrayList<>();
        LocalDate fecha = inicio;
        while (!fecha.isAfter(fin)) {
            fechas.add(fecha);
            fecha = fecha.plusDays(1);
        }
        return fechas;
    }
}