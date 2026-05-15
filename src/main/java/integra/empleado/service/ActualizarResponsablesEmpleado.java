package integra.empleado.service;

import integra.empleado.dto.ActualizarResponsableRequest;
import integra.empleado.entity.EmpleadoEntity;
import integra.empleado.exception.EmpleadoException;
import integra.empleado.repository.EmpleadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActualizarResponsablesEmpleado {

    private final EmpleadoRepository empleadoRepository;

    @Transactional
    public void actualizarResponsables(Integer empleadoId, ActualizarResponsableRequest request) {
        EmpleadoEntity empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> EmpleadoException.notFound(empleadoId.longValue()));

        if (request.primerResponsableId() != null) {
            EmpleadoEntity primerJefe = empleadoRepository.findById(request.primerResponsableId())
                    .orElseThrow(() -> EmpleadoException.notFound(request.primerResponsableId().longValue()));
            empleado.setJefe(primerJefe);
        } else {
            empleado.setJefe(null);
        }

        if (request.segundoResponsableId() != null) {
            EmpleadoEntity segundoJefe = empleadoRepository.findById(request.segundoResponsableId())
                    .orElseThrow(() -> EmpleadoException.notFound(request.segundoResponsableId().longValue()));
            empleado.setSegundoJefe(segundoJefe);
        } else {
            empleado.setSegundoJefe(null);
        }

        empleadoRepository.save(empleado);
    }
}
