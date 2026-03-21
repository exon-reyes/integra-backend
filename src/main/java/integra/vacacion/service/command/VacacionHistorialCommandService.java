package integra.vacacion.service.command;

import integra.vacacion.entity.EmpleadoTiempoHistorialEntity;
import integra.vacacion.repository.EmpleadoTiempoHistorialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
@Transactional
public class VacacionHistorialCommandService {

    private final EmpleadoTiempoHistorialRepository historialRepository;

    /**
     * Registra un nuevo evento en la trazabilidad de una solicitud.
     */
    public void registrarEvento(Long empleadoTiempoId, String tipoEvento, Integer usuarioId, String comentario) {
        EmpleadoTiempoHistorialEntity evento = new EmpleadoTiempoHistorialEntity();
        evento.setEmpleadoTiempoId(empleadoTiempoId);
        evento.setTipoEvento(tipoEvento);
        evento.setUsuarioId(usuarioId);
        evento.setComentario(comentario);
        evento.setFechaEvento(LocalDateTime.now());

        historialRepository.save(evento);
    }
}
