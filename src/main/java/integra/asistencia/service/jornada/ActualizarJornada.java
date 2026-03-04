package integra.asistencia.service.jornada;

import integra.asistencia.actions.ActualizarJornadaDTO;
import integra.asistencia.exception.AsistenciaDomainException;
import integra.asistencia.repository.AsistenciaRepository;
import integra.global.exception.code.ErrorCode;
import integra.asistencia.repository.PausaModelRepository;
import integra.asistencia.util.HandlerExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActualizarJornada implements HandlerExecutor<Void, ActualizarJornadaDTO> {
    private final AsistenciaRepository asistenciaRepository;
    private final PausaModelRepository pausaRepository;

    @Override
    @Transactional
    public Void execute(ActualizarJornadaDTO dto) {
        var jornada = asistenciaRepository.findById(dto.getJornadaId())
                .orElseThrow(() -> AsistenciaDomainException.notFound(dto.getJornadaId().longValue()));

        var ahora = LocalDateTime.now();

        // Actualizar inicio de jornada solo si se proporciona
        if (dto.getInicioJornada() != null) {
            if (dto.getInicioJornada().isAfter(ahora)) {
                throw new AsistenciaDomainException(ErrorCode.ASI_INVALID_TIME, "La hora de inicio no puede ser futura");
            }
            jornada.setInicioJornada(dto.getInicioJornada());
        }

        // Actualizar fin de jornada solo si se proporciona
        if (dto.getFinJornada() != null) {
            if (dto.getFinJornada().isAfter(ahora)) {
                throw new AsistenciaDomainException(ErrorCode.ASI_INVALID_TIME, "La hora de fin no puede ser futura");
            }
            jornada.setFinJornada(dto.getFinJornada());
            if (jornada.getCerradoAutomatico()) {
                jornada.setCerradoAutomatico(false);
            }
            jornada.setJornadaCerrada(true); // Marcar como cerrada si tiene fin

            // Cerrar todas las pausas abiertas de esta jornada como medida de protección
            cerrarPausasAbiertas(dto.getJornadaId(), dto.getFinJornada());
        }

        // Actualizar comentario solo si se proporciona
        if (dto.getComentario() != null) {
            jornada.setComentario(dto.getComentario());
        }

        asistenciaRepository.save(jornada);
        log.info("Jornada {} actualizada", dto.getJornadaId());
        return null;
    }

    /**
     * Cierra todas las pausas abiertas (fin = null) de una jornada específica.
     * Esto previene pausas "colgadas" cuando se cierra una jornada manualmente.
     *
     * @param jornadaId  ID de la jornada
     * @param finJornada Hora de cierre de la jornada (se usa para cerrar las
     *                   pausas)
     */
    private void cerrarPausasAbiertas(Integer jornadaId, java.time.LocalDateTime finJornada) {
        var pausasAbiertas = pausaRepository.findByAsistenciaId(jornadaId).stream()
                .filter(pausa -> pausa.getFin() == null)
                .toList();

        if (!pausasAbiertas.isEmpty()) {
            log.info("Cerrando {} pausa(s) abierta(s) de la jornada {} a las {}",
                    pausasAbiertas.size(), jornadaId, finJornada);

            pausasAbiertas.forEach(pausa -> {
                pausa.setFin(finJornada);
                pausaRepository.save(pausa);
            });
        }
    }
}
