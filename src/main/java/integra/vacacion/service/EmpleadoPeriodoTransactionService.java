package integra.vacacion.service;

import integra.empleado.entity.EmpleadoEntity;
import integra.vacacion.core.EstatusPeriodo;
import integra.vacacion.dto.response.PeriodoGeneradoInfo;
import integra.vacacion.dto.response.PeriodoVencidoInfo;
import integra.vacacion.entity.PeriodoVacacionalEntity;
import integra.vacacion.entity.PoliticaVacacionEscalaEntity;
import integra.vacacion.exception.VacacionException;
import integra.vacacion.query.EmpleadoDescansoInfo;
import integra.vacacion.repository.PeriodoVacacionalRepository;
import integra.vacacion.repository.PoliticaVacacionEscalaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmpleadoPeriodoTransactionService {

    private final PeriodoVacacionalRepository periodoVacacionalRepository;
    private final PoliticaVacacionEscalaRepository politicaRepository;

    public record ResultadoEmpleado(PeriodoGeneradoInfo nuevo, PeriodoVencidoInfo vencido) {}

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ResultadoEmpleado procesarEmpleadoTransaccional(EmpleadoDescansoInfo empleado) {
        LocalDate fechaIngreso = obtenerFechaIngreso(empleado);

        if (fechaIngreso == null) {
            log.warn("Empleado {} no tiene fecha de ingreso", empleado.id());
            return null;
        }

        LocalDate hoy = LocalDate.now();
        long aniosCumplidos = ChronoUnit.YEARS.between(fechaIngreso, hoy);

        if (aniosCumplidos < 1) {
            return null;
        }

        int periodoNumero = (int) aniosCumplidos;

        // Idempotencia por numero de periodo (años cumplidos) para asegurar un único periodo por año
        if (periodoVacacionalRepository.existsByEmpleadoIdAndPeriodoNumero(empleado.id(), periodoNumero)) {
            return null;
        }

        LocalDate fechaInicio = fechaIngreso.plusYears(periodoNumero);
        LocalDate fechaFin = fechaInicio.plusYears(1).minusDays(1);
        LocalDate fechaCaducidad = fechaFin;
        int anioGestion = fechaInicio.getYear();

        // Verificar si existe un periodo anterior y vencerlo si es de un ciclo previo
        PeriodoVencidoInfo vencidoInfo = null;
        var periodoAnteriorOpt = periodoVacacionalRepository.obtenerPeriodo(empleado.id(), EstatusPeriodo.VIGENTE);
        if (periodoAnteriorOpt.isPresent()) {
            PeriodoVacacionalEntity p = periodoAnteriorOpt.get();
            if (p.getPeriodoNumero() < periodoNumero) {
                vencidoInfo = new PeriodoVencidoInfo(
                        empleado.id(), empleado.nombreCompleto(),
                        p.getFechaInicio(), p.getFechaFin(), p.getDiasRestantes()
                );
                p.setEstatus(EstatusPeriodo.VENCIDO);
                periodoVacacionalRepository.save(p);
                log.info("Periodo {} vencido para empleado {} por inicio del periodo {}", p.getId(), empleado.id(), periodoNumero);
            }
        }

        PoliticaVacacionEscalaEntity politica = politicaRepository.findByActivaTrue()
                .orElseThrow(VacacionException::politicaNoEncontrada);

        int diasHabilitados = politica.getDiasVacacionesPorAnio(periodoNumero);

        PeriodoVacacionalEntity periodo = new PeriodoVacacionalEntity();
        periodo.setEmpleado(new EmpleadoEntity(empleado.id()));
        periodo.setAnioLaboral(periodoNumero);
        periodo.setFechaInicio(fechaInicio);
        periodo.setFechaFin(fechaFin);
        periodo.setDiasHabilitados(diasHabilitados);
        periodo.setDiasTomados(0);
        periodo.setDiasRestantes(diasHabilitados);
        periodo.setFechaCaducidad(fechaCaducidad);
        periodo.setAnioGestion(anioGestion);
        periodo.setPeriodoNumero(periodoNumero);
        periodo.setEstatus(EstatusPeriodo.VIGENTE);
        periodo.setCreatedAt(LocalDateTime.now());

        periodoVacacionalRepository.save(periodo);

        log.info("Período {} generado para empleado {}: {} días", periodoNumero, empleado.id(), diasHabilitados);

        var nuevo = new PeriodoGeneradoInfo(empleado.id(), empleado.nombreCompleto(), fechaIngreso,
                periodoNumero, diasHabilitados, fechaInicio, fechaFin, fechaCaducidad);

        return new ResultadoEmpleado(nuevo, vencidoInfo);
    }

    private LocalDate obtenerFechaIngreso(EmpleadoDescansoInfo empleado) {
        return empleado.fechaReingreso() != null ? empleado.fechaReingreso() : empleado.fechaAlta();
    }
}
