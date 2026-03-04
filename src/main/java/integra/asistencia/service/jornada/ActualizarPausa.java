package integra.asistencia.service.jornada;

import integra.asistencia.actions.ActualizarPausaDTO;
import integra.asistencia.repository.PausaModelRepository;
import integra.asistencia.util.HandlerExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActualizarPausa implements HandlerExecutor<Void, ActualizarPausaDTO> {
    private final PausaModelRepository pausaRepository;

    @Override
    @Transactional
    public Void execute(ActualizarPausaDTO dto) {
        var pausa = pausaRepository.findById(dto.getPausaId())
                .orElseThrow(() -> new IllegalArgumentException("Pausa no encontrada"));

        // Actualizar inicio solo si se proporciona
        if (dto.getInicio() != null) {
            pausa.setInicio(dto.getInicio());
        }

        // Actualizar fin solo si se proporciona (puede ser null para pausas activas)
        if (dto.getFin() != null) {
            pausa.setFin(dto.getFin());
        }

        pausaRepository.save(pausa);
        return null;
    }
}
