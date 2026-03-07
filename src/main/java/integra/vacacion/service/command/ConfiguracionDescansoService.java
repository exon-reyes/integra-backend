package integra.vacacion.service.command;

import integra.empleado.repository.EmpleadoRepository;
import integra.vacacion.dto.request.ConfiguracionDescansoRequest;
import integra.vacacion.dto.response.ConfiguracionDescansoDTO;
import integra.vacacion.entity.DescansoEmpleadoEntity;
import integra.vacacion.exception.VacacionException;
import integra.vacacion.repository.DescansoEmpleadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ConfiguracionDescansoService {

    private final DescansoEmpleadoRepository descansoRepository;
    private final EmpleadoRepository empleadoRepository;

    @Transactional
    public ConfiguracionDescansoDTO configurar(Integer empleadoId, ConfiguracionDescansoRequest request) {
        if (!empleadoRepository.existsById(empleadoId)) {
            throw VacacionException.empleadoNoEncontrado(empleadoId);
        }

        // Desactivar configuración anterior
        descansoRepository.findByEmpleadoIdAndActivoTrue(empleadoId)
                .forEach(d -> {
                    d.setActivo(false);
                    descansoRepository.save(d);
                });

        // Crear nueva configuración con fechas específicas
        for (LocalDate fecha : request.diasDescanso()) {
            DescansoEmpleadoEntity descanso = new DescansoEmpleadoEntity();
            descanso.setEmpleadoId(empleadoId);
            descanso.setFechaDescanso(fecha);
            descanso.setMotivo("Descanso personal");
            descanso.setActivo(true);
            descanso.setCreatedAt(LocalDateTime.now());
            descansoRepository.save(descanso);
        }

        return new ConfiguracionDescansoDTO(empleadoId, request.diasDescanso(), true);
    }

    @Transactional(readOnly = true)
    public ConfiguracionDescansoDTO obtener(Integer empleadoId) {
        Set<LocalDate> fechas = descansoRepository.findFechasDescansoByEmpleado(empleadoId);
        return new ConfiguracionDescansoDTO(empleadoId, fechas, !fechas.isEmpty());
    }
}
