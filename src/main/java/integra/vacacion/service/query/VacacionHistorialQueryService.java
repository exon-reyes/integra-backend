package integra.vacacion.service.query;

import integra.vacacion.dto.response.EmpleadoTiempoHistorialDTO;
import integra.vacacion.repository.EmpleadoTiempoHistorialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class VacacionHistorialQueryService {

    private final EmpleadoTiempoHistorialRepository historialRepository;

    public List<EmpleadoTiempoHistorialDTO> obtenerLineaTiempo(Long empleadoTiempoId) {
        return historialRepository.findByEmpleadoTiempoIdOrderByFechaEventoDesc(empleadoTiempoId)
                .stream()
                .map(entity -> new EmpleadoTiempoHistorialDTO(
                        entity.getId(),
                        entity.getEmpleadoTiempoId(),
                        entity.getTipoEvento(),
                        entity.getFechaEvento(),
                        entity.getUsuarioId(),
                        entity.getComentario()
                )).toList();
    }
}
