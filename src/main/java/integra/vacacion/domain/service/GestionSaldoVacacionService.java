package integra.vacacion.domain.service;

import integra.vacacion.entity.PeriodoVacacionalEntity;
import integra.vacacion.repository.PeriodoVacacionalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GestionSaldoVacacionService {

    private final PeriodoVacacionalRepository periodoRepository;

    public void descontarDiasDePeriodos(Integer empleadoId, int diasADescontar) {
        List<PeriodoVacacionalEntity> periodos = periodoRepository.findPeriodosDisponiblesOrdenados(empleadoId);

        int restante = diasADescontar;
        for (PeriodoVacacionalEntity periodo : periodos) {
            if (restante <= 0) break;

            int aDescontar = Math.min(restante, periodo.getDiasRestantes());
            periodo.descontarDias(aDescontar);
            periodoRepository.save(periodo);
            restante -= aDescontar;
        }
    }

    public int calcularSaldoTotal(Integer empleadoId) {
        return periodoRepository.sumDiasRestantesByEmpleado(empleadoId);
    }
}
