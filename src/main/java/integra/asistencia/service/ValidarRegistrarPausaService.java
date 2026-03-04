package integra.asistencia.service;

import integra.asistencia.exception.AsistenciaDomainException;
import integra.asistencia.repository.PausaModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ValidarRegistrarPausaService {
    private final PausaModelRepository repository;

    public void execute(Integer empleadoId) {
        repository.findFirstByAsistencia_Empleado_IdAndFinNullOrderByInicioDesc(empleadoId)
                .ifPresent(p -> {
                    throw AsistenciaDomainException.pausaActivaExistente(empleadoId);
                });
    }
}
