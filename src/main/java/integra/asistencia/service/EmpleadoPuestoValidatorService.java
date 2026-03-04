package integra.asistencia.service;

import integra.asistencia.repository.AsistenciaRepository;
import integra.asistencia.repository.EmpleadoPuestoService;
import integra.core.service.ParamsDataProxy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Servicio para validar puestos de empleados y sus turnos asociados.
 * Esta clase proporciona métodos para verificar si un empleado tiene un puesto de turno nocturno
 * o un turno nocturno activo.
 */
@Service
@RequiredArgsConstructor
public class EmpleadoPuestoValidatorService implements EmpleadoPuestoService {
    private final AsistenciaRepository asistenciaRepository;
    private final ParamsDataProxy globalConfig;

    /**
     * Verifica si el ID del puesto del empleado corresponde a un puesto de turno nocturno.
     *
     * @param idPuestoEmpleado El ID del puesto del empleado a verificar.
     * @return true si el puesto es un turno nocturno, false en caso contrario.
     */
    @Override
    public boolean tienePuestoNocturno(Integer idPuestoEmpleado) {
        if (idPuestoEmpleado == null) return false;
        return idPuestoEmpleado.equals(globalConfig.getIdPuestoNocturno());
    }

    /**
     * Verifica si el empleado tiene un turno nocturno activo.
     * Un turno nocturno se considera activo si comenzó después de la hora de inicio del turno nocturno
     * del día anterior y antes de la hora actual.
     *
     * @param empleadoId El ID del empleado a verificar.
     * @return true si el empleado tiene un turno nocturno activo, false en caso contrario.
     */
    @Override
    public boolean tieneJornadaActivaNocturna(Integer empleadoId) {
        LocalTime horaCorte = globalConfig.getHoraInicioNocturno();
        LocalDateTime desde = LocalDate.now().minusDays(1).atTime(horaCorte);
        LocalDateTime hasta = LocalDateTime.now();

        return asistenciaRepository.findByEmpleado_IdAndJornadaCerradaFalse(empleadoId)
                .stream()
                .anyMatch(a -> a.getInicioJornada() != null && !a.getJornadaCerrada() && a.getInicioJornada()
                        .isAfter(desde) && a.getInicioJornada().isBefore(hasta));
    }
}
