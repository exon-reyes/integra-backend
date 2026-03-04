package integra.asistencia.service.jornada;

import integra.asistencia.actions.CrearPausaCompletaDTO;
import integra.asistencia.entity.AsistenciaModel;
import integra.asistencia.entity.PausaModel;
import integra.asistencia.exception.AsistenciaDomainException;
import integra.asistencia.repository.AsistenciaRepository;
import integra.global.exception.code.ErrorCode;
import integra.asistencia.repository.PausaModelRepository;
import integra.asistencia.util.HandlerExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CrearPausaCompleta implements HandlerExecutor<Void, CrearPausaCompletaDTO> {

    private final AsistenciaRepository asistenciaRepository;
    private final PausaModelRepository pausaRepository;

    @Override
    @Transactional
    public Void execute(CrearPausaCompletaDTO dto) {
        validatePausaDuration(dto);
        AsistenciaModel asistencia = findAsistencia(dto.getAsistenciaId());
        validatePausaWithinJornada(dto, asistencia);

        pausaRepository.save(createPausa(dto, asistencia));
        return null;
    }

    private void validatePausaDuration(CrearPausaCompletaDTO dto) {
        if (!dto.getFin().isAfter(dto.getInicio())) {
            throw new AsistenciaDomainException(ErrorCode.ASI_INVALID_TIME, "La hora de fin de la pausa debe ser posterior a la hora de inicio");
        }
    }

    private AsistenciaModel findAsistencia(Integer asistenciaId) {
        return asistenciaRepository.findById(asistenciaId)
                .orElseThrow(() -> AsistenciaDomainException.notFound(asistenciaId.longValue()));
    }

    private void validatePausaWithinJornada(CrearPausaCompletaDTO dto, AsistenciaModel asistencia) {
        if (asistencia.getInicioJornada() != null && dto.getInicio().isBefore(asistencia.getInicioJornada())) {
            throw new AsistenciaDomainException(ErrorCode.ASI_INVALID_TIME, "La pausa no puede iniciar antes del inicio de la jornada");
        }
        if (asistencia.getFinJornada() != null && dto.getFin().isAfter(asistencia.getFinJornada())) {
            throw new AsistenciaDomainException(ErrorCode.ASI_INVALID_TIME, "La pausa no puede terminar después del fin de la jornada");
        }
    }

    private PausaModel createPausa(CrearPausaCompletaDTO dto, AsistenciaModel asistencia) {
        var pausa = new PausaModel();
        pausa.setInicio(dto.getInicio());
        pausa.setFin(dto.getFin());
        pausa.setTipo(dto.getTipoPausa());
        pausa.setAsistencia(asistencia);
        return pausa;
    }
}
