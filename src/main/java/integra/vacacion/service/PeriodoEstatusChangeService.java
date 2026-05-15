package integra.vacacion.service;

import integra.vacacion.core.EstatusPeriodo;
import integra.vacacion.repository.PeriodoVacacionalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PeriodoEstatusChangeService {
    private final PeriodoVacacionalRepository periodoRepository;

    public void update(Long periodoId) {

        var periodo = periodoRepository.findById(periodoId)
                .orElseThrow(() -> new RuntimeException("Periodo no encontrado"));
        var periodoVigente=periodoRepository.obtenerPeriodo(periodo.getEmpleado().getId(),EstatusPeriodo.VIGENTE);
        if (periodoVigente.isPresent()){
            throw new RuntimeException("El empleado ya tiene un periodo vigente");
        }
        if (periodo.getEstatus() == EstatusPeriodo.VIGENTE||periodo.getEstatus()==EstatusPeriodo.CONSUMIDO){
            return;
        }
        periodo.setEstatus(EstatusPeriodo.VIGENTE);
    }
}
