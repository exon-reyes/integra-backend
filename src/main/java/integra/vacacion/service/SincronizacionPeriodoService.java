package integra.vacacion.service;

import integra.empleado.repository.EmpleadoRepository;
import integra.vacacion.dto.response.PeriodoCerradoInfo;
import integra.vacacion.dto.response.PeriodoGeneradoInfo;
import integra.vacacion.dto.response.PeriodoVencidoInfo;
import integra.vacacion.dto.response.SincronizacionPeriodoResponse;
import integra.vacacion.query.EmpleadoDescansoInfo;
import integra.vacacion.repository.PeriodoVacacionalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SincronizacionPeriodoService {

    private final EmpleadoRepository empleadoRepository;
    private final PeriodoVacacionalRepository periodoVacacionalRepository;
    private final EmpleadoPeriodoTransactionService empleadoPeriodoTransactionService;

    public SincronizacionPeriodoResponse sincronizarUltimoPeriodo() {
        log.info("Iniciando sincronización de períodos vacacionales");

        List<EmpleadoDescansoInfo> empleadosActivos = empleadoRepository.findByEstatusNot("B", EmpleadoDescansoInfo.class);

        // Periodos vencidos por caducidad (job de VencimientoPeriodoService los marca, aquí los recolectamos para el reporte)
        LocalDate hoy = LocalDate.now();
        List<PeriodoCerradoInfo> cerrados = periodoVacacionalRepository.findPeriodosVencidos(hoy)
                .stream()
                .map(p -> new PeriodoCerradoInfo(
                        p.getEmpleado().getId(),
                        null,
                        p.getFechaInicio(),
                        p.getFechaFin(),
                        p.getFechaCaducidad(),
                        p.getDiasRestantes()
                ))
                .toList();

        int exitosos = 0, fallidos = 0, omitidos = 0;
        List<PeriodoGeneradoInfo> nuevos = new ArrayList<>();
        List<PeriodoVencidoInfo> vencidos = new ArrayList<>();

        for (EmpleadoDescansoInfo empleado : empleadosActivos) {
            try {
                var resultado = empleadoPeriodoTransactionService.procesarEmpleadoTransaccional(empleado);
                if (resultado == null) {
                    omitidos++;
                } else {
                    nuevos.add(resultado.nuevo());
                    if (resultado.vencido() != null) vencidos.add(resultado.vencido());
                    exitosos++;
                }
            } catch (Exception e) {
                log.error("Error al procesar empleado {}: {}", empleado.id(), e.getMessage());
                fallidos++;
            }
        }

        log.info("Sincronización completada: {} exitosos, {} fallidos, {} omitidos", exitosos, fallidos, omitidos);
        return new SincronizacionPeriodoResponse(empleadosActivos.size(), exitosos, fallidos, omitidos, nuevos, vencidos, cerrados);
    }
}
