package integra.vacacion.service.command;

import integra.vacacion.core.EstatusPeriodo;
import integra.vacacion.entity.PeriodoVacacionalEntity;
import integra.vacacion.repository.PeriodoVacacionalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VencimientoPeriodoService {

    private final PeriodoVacacionalRepository periodoRepository;

    @Scheduled(cron = "0 40 3 * * *") // 3 AM diario
    @Transactional
    public void vencerPeriodosExpirados() {
        log.info("Iniciando proceso de vencimiento de períodos vacacionales");

        LocalDate hoy = LocalDate.now();
        List<PeriodoVacacionalEntity> periodosVencidos = periodoRepository.findPeriodosVencidos(hoy);

        int vencidos = 0;
        for (PeriodoVacacionalEntity periodo : periodosVencidos) {
            if (periodo.getDiasRestantes() > 0) {
                log.warn("Venciendo período: empleado={}, año={}, días perdidos={}",
                        periodo.getEmpleado().getId(),
                        periodo.getAnioLaboral(),
                        periodo.getDiasRestantes());
            }
            periodo.setEstatus(EstatusPeriodo.VENCIDO);
            periodoRepository.save(periodo);
            vencidos++;
        }
        log.info("Períodos vencidos: {}", vencidos);
    }
}
