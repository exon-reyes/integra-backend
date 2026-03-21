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

    /**
     * Descuenta días del saldo disponible (al crear solicitud PENDIENTE o al APROBAR).
     */
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

    /**
     * Restaura días al saldo disponible (al RECHAZAR o CANCELAR una solicitud PENDIENTE).
     */
    public void restaurarDiasDePeriodos(Integer empleadoId, int diasARestaurar) {
        // Recuperar períodos del empleado ordenados por caducidad (más próximo primero)
        List<PeriodoVacacionalEntity> periodos = periodoRepository.findPeriodosDisponiblesOrdenados(empleadoId);

        // Si no hay periodos vigentes con saldo, buscar todos los vigentes para reponer en el último que se tocó
        if (periodos.isEmpty()) {
            periodos = periodoRepository.findByEmpleadoIdAndEstatus(
                    empleadoId, integra.vacacion.entity.PeriodoVacacionalEntity.EstatusPeriodo.VIGENTE);
        }

        int restante = diasARestaurar;
        for (PeriodoVacacionalEntity periodo : periodos) {
            if (restante <= 0) break;
            int aRestaurar = Math.min(restante, periodo.getDiasHabilitados() - periodo.getDiasRestantes());
            if (aRestaurar <= 0) continue;
            periodo.setDiasRestantes(periodo.getDiasRestantes() + aRestaurar);
            periodo.setDiasTomados(Math.max(0, periodo.getDiasTomados() - aRestaurar));
            periodoRepository.save(periodo);
            restante -= aRestaurar;
        }
    }

    public int calcularSaldoTotal(Integer empleadoId) {
        return periodoRepository.sumDiasRestantesByEmpleado(empleadoId);
    }
}
