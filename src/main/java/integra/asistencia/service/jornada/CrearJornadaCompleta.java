package integra.asistencia.service.jornada;

import integra.asistencia.actions.CrearJornadaCompletaDTO;
import integra.asistencia.entity.AsistenciaModel;
import integra.asistencia.exception.AsistenciaDomainException;
import integra.asistencia.repository.AsistenciaRepository;
import integra.asistencia.util.HandlerExecutor;
import integra.empleado.entity.EmpleadoEntity;
import integra.global.exception.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrearJornadaCompleta implements HandlerExecutor<Void, CrearJornadaCompletaDTO> {

    private final AsistenciaRepository jornadaRepository;

    @Override
    @Transactional
    public Void execute(CrearJornadaCompletaDTO dto) {
        log.info("Creando jornada completa para empleado {} desde {} hasta {}",
                dto.getEmpleadoId(), dto.getInicioJornada(), dto.getFinJornada());

        // Validar que finJornada sea posterior a inicioJornada
        if (dto.getFinJornada().isBefore(dto.getInicioJornada()) ||
                dto.getFinJornada().isEqual(dto.getInicioJornada())) {
            throw new AsistenciaDomainException(ErrorCode.ASI_INVALID_TIME, "La hora de fin debe ser posterior a la hora de inicio");
        }

        // Validar duración máxima (ej: 24 horas)
        Duration duracion = Duration.between(dto.getInicioJornada(), dto.getFinJornada());
        if (duracion.toHours() > 24) {
            throw new AsistenciaDomainException(ErrorCode.BUS_LIMIT_EXCEEDED, "La jornada no puede exceder 24 horas");
        }

        // Crear nueva jornada
        var jornada = new AsistenciaModel();
        jornada.setFecha(dto.getInicioJornada().toLocalDate());
        jornada.setEmpleado(new EmpleadoEntity(dto.getEmpleadoId()));
        jornada.setInicioJornada(dto.getInicioJornada());
        jornada.setFinJornada(dto.getFinJornada());
        jornada.setComentario(dto.getComentario());
        jornada.setJornadaCerrada(true); // Jornada completa = cerrada

        jornadaRepository.save(jornada);

        log.info("Jornada completa creada exitosamente con ID {}", jornada.getId());

        return null;
    }
}
