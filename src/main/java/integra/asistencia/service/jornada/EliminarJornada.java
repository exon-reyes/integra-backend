package integra.asistencia.service.jornada;

import integra.asistencia.exception.AsistenciaDomainException;
import integra.asistencia.repository.AsistenciaRepository;
import integra.asistencia.util.HandlerExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EliminarJornada implements HandlerExecutor<Void, Integer> {
    private final AsistenciaRepository asistenciaRepository;

    @Override
    @Transactional
    public Void execute(Integer jornadaId) {
        if (!asistenciaRepository.existsById(jornadaId)) {
            throw AsistenciaDomainException.jornadaNotFound(jornadaId);
        }
        asistenciaRepository.deleteById(jornadaId);
        return null;
    }
}
