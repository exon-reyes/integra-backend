package integra.vacacion.service.command;

import integra.config.file.BusinessPropertiesService;
import integra.vacacion.dto.response.SincronizacionPeriodoResponse;
import integra.vacacion.service.SincronizacionPeriodoService;
import integra.vacacion.service.gestion.SincronizacionReporteExcelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class AniversarioScheduler {

    private final SincronizacionPeriodoService sincronizacionService;
    private final BusinessPropertiesService propertiesService;
    private final SincronizacionReporteExcelService reporteService;

    @Scheduled(cron = "0 00 01 * * *")
    public void ejecutar() {
        ejecutarSincronizacion("AUTO");
    }

    public void ejecutarManual() {
        String id = "MANUAL-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
        ejecutarSincronizacion(id);
    }

    private void ejecutarSincronizacion(String identificador) {
        if ("AUTO".equals(identificador) && propertiesService.yaSincronizadoHoy()) {
            log.info("[AniversarioScheduler] Ya se sincronizó hoy, omitiendo ejecución automática");
            return;
        }

        LocalDateTime inicio = LocalDateTime.now();
        log.info("[AniversarioScheduler] Iniciando sincronización [{}]", identificador);

        SincronizacionPeriodoResponse resultado = sincronizacionService.sincronizarUltimoPeriodo();

        propertiesService.actualizarUltimaSincronizacion(inicio);
        log.info("[AniversarioScheduler] Sincronización [{}] completada — nuevos: {}, vencidos: {}, cerrados: {}",
                identificador,
                resultado.periodosNuevos().size(),
                resultado.periodosVencidos().size(),
                resultado.periodosCerrados().size());

        reporteService.generarReportes(resultado, inicio, identificador);
    }
}
