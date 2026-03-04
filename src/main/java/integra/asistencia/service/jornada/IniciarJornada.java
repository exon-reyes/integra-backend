package integra.asistencia.service.jornada;

import integra.asistencia.actions.IniciarJornadaCommand;
import integra.asistencia.entity.AsistenciaModel;
import integra.asistencia.entity.TipoIncidencia;
import integra.asistencia.exception.AsistenciaDomainException;
import integra.asistencia.repository.AsistenciaRepository;
import integra.asistencia.service.UnidadVerificadorService;
import integra.asistencia.service.WorkImageService;
import integra.asistencia.util.HandlerExecutor;
import integra.empleado.entity.EmpleadoEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class IniciarJornada extends BaseAsistenciaService implements HandlerExecutor<Void, IniciarJornadaCommand> {
    private final AsistenciaRepository asistenciaRepository;
    private final UnidadVerificadorService unidadVerificadorService;

    public IniciarJornada(WorkImageService workImageService, AsistenciaRepository asistenciaRepository, UnidadVerificadorService unidadVerificadorService) {
        super(workImageService);
        this.asistenciaRepository = asistenciaRepository;
        this.unidadVerificadorService = unidadVerificadorService;
    }

    @Override
    public Void execute(IniciarJornadaCommand command) {
        validarNoExisteJornadaActiva(command.empleadoId());
        String pathFoto = guardarFotoSiExiste(command.foto(), command.empleadoId());

        boolean hayIncidencia = !Objects.equals(command.unidadId(), command.unidadAsignadaId());

        AsistenciaModel asistencia = construirAsistencia(command, pathFoto, hayIncidencia);
        AsistenciaModel asistenciaGuardada = asistenciaRepository.save(asistencia);

        if (hayIncidencia) {
            unidadVerificadorService.registrarIncidenciaKioscoAsync(asistenciaGuardada.getId(), command.empleadoId(), command.unidadAsignadaId(), command.unidadId(), pathFoto, TipoIncidencia.UNIDAD_INCORRECTA, "Inicio de jornada");
        }
        return null;
    }

    private void validarNoExisteJornadaActiva(Integer empleadoId) {
        Optional<AsistenciaModel> jornadaActiva = asistenciaRepository.findFirstByEmpleado_IdAndJornadaCerradaFalseOrderByInicioJornadaDesc(empleadoId);
        if (jornadaActiva.isPresent()) {
            throw AsistenciaDomainException.duplicateEntry(empleadoId.longValue(), LocalDate.now().toString());
        }
    }


    private AsistenciaModel construirAsistencia(IniciarJornadaCommand command, String pathFoto, boolean inconsistencia) {
        AsistenciaModel asistencia = new AsistenciaModel();
        asistencia.setEmpleado(new EmpleadoEntity(command.empleadoId()));
        asistencia.setFecha(LocalDate.now());
        LocalDateTime ahora = LocalDateTime.now();

        //Para escenarios donde se requiere registrar la asistencia manual, solo se contempla la hora, ya que el día debe estar vigente

        if (command.hora() != null) {
            ahora = LocalDateTime.of(ahora.getYear(), ahora.getMonth(), ahora.getDayOfMonth(), command.hora()
                    .getHour(), command.hora().getMinute(), command.hora().getSecond());
            asistencia.setComentario("El registro fue agregado manualmente");
        }

        asistencia.setInicioJornada(ahora);
        asistencia.setPathFotoInicio(pathFoto);
        asistencia.setJornadaCerrada(false);
        asistencia.setInconsistencia(inconsistencia);
        return asistencia;
    }
}