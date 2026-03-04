package integra.empleado.service;

import integra.empleado.repository.EmpleadoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Servicio de consulta para operaciones de lectura de empleados.
 * Implementa caché para mejorar el rendimiento de consultas frecuentes.
 *
 * @author Pablo Reyes
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ConsultarEmpleadoPinService {

    private final EmpleadoRepository empleadoRepository;

    /**
     * Busca un empleado por su PIN de asistencia.
     * Los resultados se cachean para mejorar el rendimiento en consultas repetidas.
     * <p>
     * La clave de caché incluye tanto el PIN como el tipo de proyección solicitado,
     * permitiendo cachear diferentes vistas del mismo empleado.
     * </p>
     *
     * @param pin  PIN del empleado (código de asistencia)
     * @param type Clase de proyección deseada (ej: EmpleadoModelInfo.class)
     * @param <T>  Tipo de la proyección
     * @return Optional con el empleado encontrado, o vacío si no existe
     */
    @Cacheable(value = "empleadoPorNip", key = "#pin + '_' + #type.simpleName")
    public <T> Optional<T> execute(String pin, Class<T> type) {
        return empleadoRepository.findByPin(pin, type);
    }

    public <T> Optional<T> obtenerPorId(Integer id, Class<T> type) {
        return empleadoRepository.findById(id, type);
    }
}
