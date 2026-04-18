package integra.vacacion.service;

import integra.empleado.repository.EmpleadoRepository;
import integra.empleado.repository.EmpleadoRepository.VinculacionEmpleadoProjection;
import integra.empleado.util.FiltroEmpleado;
import integra.utils.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class EmpleadoVinculaciones {

    private final EmpleadoRepository repository;

    public PageResponse<VinculacionEmpleadoProjection> getEmpleadoVinculaciones(FiltroEmpleado filtro) {
        return new PageResponse<>(repository.findVinculaciones(filtro, filtro.toPageable()));
    }
}
