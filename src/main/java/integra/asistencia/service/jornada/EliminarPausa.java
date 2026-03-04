package integra.asistencia.service.jornada;

import integra.asistencia.exception.AsistenciaDomainException;
import integra.asistencia.repository.PausaModelRepository;
import integra.asistencia.util.HandlerExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EliminarPausa implements HandlerExecutor<Void, Integer> {
    private final PausaModelRepository pausaRepository;

    @Override
    @Transactional
    public Void execute(Integer pausaId) {
        if (!pausaRepository.existsById(pausaId)) {
            throw AsistenciaDomainException.notFound(pausaId.longValue());
        }
        pausaRepository.deleteById(pausaId);
        return null;
    }
}
