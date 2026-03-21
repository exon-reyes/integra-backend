package integra.vacacion.service.command;

import integra.empleado.entity.EmpleadoEntity;
import integra.empleado.exception.EmpleadoException;
import integra.empleado.query.InfoBasicaEmpleadoQuery;
import integra.empleado.repository.EmpleadoRepository;
import integra.vacacion.entity.PeriodoVacacionalEntity;
import integra.vacacion.entity.PoliticaVacacionEntity;
import integra.vacacion.entity.PoliticaVacacionEscalaEntity;
import integra.vacacion.repository.PeriodoVacacionalRepository;
import integra.vacacion.repository.PoliticaVacacionEscalaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PeriodoVacacionalSyncService {

    private final EmpleadoRepository empleadoRepository;
    private final PeriodoVacacionalRepository periodoRepository;
    private final PoliticaVacacionEscalaRepository politicaEscalaRepository;

    @Scheduled(cron = "0 58 12 * * *") // 2 AM diario
    @Transactional
    public void generarPeriodosAutomaticos() {
        log.info("Iniciando generación automática de períodos vacacionales");

        List<InfoBasicaEmpleadoQuery> empleados = empleadoRepository.findByEstatus("A");

        int generados = 0;
        for (InfoBasicaEmpleadoQuery empleadoInfo : empleados) {
            try {
                // Instanciar entidad mínima o buscarla si es necesario
                EmpleadoEntity empleado = empleadoRepository.findById(empleadoInfo.id())
                        .orElseThrow(() -> EmpleadoException.notFound(Long.valueOf(empleadoInfo.id())));

                // NOTA: Para no sobrecargar memoria ni tener transacciones muy largas, procesamos de a uno.
                // Lo idóneo será mejorar la forma de recuperar entidad para periodos, pero evitamos N+1 masivo del findAll
                generarPeriodoActualConPoliticaCorrecta(empleado);
                generados++;
            } catch (Exception e) {
                log.error("Error generando período para empleado {}: {}", empleadoInfo.id(), e.getMessage());
            }
        }

        log.info("Períodos procesados: {}", generados);
    }


    @Transactional
    public void generarPeriodo(EmpleadoEntity empleado, PoliticaVacacionEntity politica) {
        LocalDate fechaBase = empleado.getFechaReingreso() != null
                ? empleado.getFechaReingreso()
                : empleado.getFechaAlta();

        long aniosCompletos = ChronoUnit.YEARS.between(fechaBase, LocalDate.now());
        int diasCorresponden = calcularDiasSegunAntiguedad((int) aniosCompletos, politica);
        LocalDate inicioAnio = fechaBase.plusYears(aniosCompletos - 1);

        PeriodoVacacionalEntity periodo = new PeriodoVacacionalEntity();
        periodo.setEmpleadoId(empleado.getId());
        periodo.setAnioLaboral((int) aniosCompletos);
        periodo.setFechaInicio(inicioAnio);
        periodo.setFechaFin(inicioAnio.plusYears(1).minusDays(1));
        periodo.setDiasHabilitados(diasCorresponden);
        periodo.setDiasTomados(0);
        periodo.setDiasRestantes(diasCorresponden);
        periodo.setFechaCaducidad(inicioAnio.plusYears(1).minusDays(1)); // Fin del año laboral
        periodo.setEstatus(PeriodoVacacionalEntity.EstatusPeriodo.VIGENTE);
        periodo.setCreatedAt(java.time.LocalDateTime.now());
        periodo.setPeriodoNumero((int) aniosCompletos);
        periodo.setAnioGestion(inicioAnio.getYear());

        periodoRepository.save(periodo);

        log.info("Período generado para empleado {} - Año {} - {} días",
                empleado.getId(), aniosCompletos, diasCorresponden);
    }

    private int calcularDiasSegunAntiguedad(int anios, PoliticaVacacionEntity politica) {
        if (anios <= 0) return 0;
        if (anios == 1) return politica.getDiasPrimerAnio();
        if (anios <= politica.getAniosIncrementoHasta()) {
            return politica.getDiasPrimerAnio() + (politica.getIncrementoAnual() * (anios - 1));
        }

        int diasBase = politica.getDiasPrimerAnio() +
                (politica.getIncrementoAnual() * (politica.getAniosIncrementoHasta() - 1));

        int aniosExtra = anios - politica.getAniosIncrementoHasta();
        int bloquesExtra = aniosExtra / politica.getAniosBloquePostLimite();

        return diasBase + (bloquesExtra * politica.getIncrementoPostLimite());
    }

    @Transactional
    public void sincronizarEmpleado(Integer empleadoId) {
        EmpleadoEntity empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> EmpleadoException.notFound(Long.valueOf(empleadoId)));

        generarPeriodoActualConPoliticaCorrecta(empleado);
    }

    @Transactional
    public void regenerarPeriodosEmpleado(Integer empleadoId) {
        EmpleadoEntity empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> EmpleadoException.notFound(Long.valueOf(empleadoId)));

        List<PeriodoVacacionalEntity> periodosExistentes = periodoRepository.findByEmpleadoId(empleadoId);
        periodosExistentes.stream()
                .filter(p -> p.getDiasTomados() == 0)
                .forEach(periodoRepository::delete);

        log.info("Períodos eliminados para empleado {}: {}", empleadoId,
                periodosExistentes.stream().filter(p -> p.getDiasTomados() == 0).count());

        generarPeriodoActualConPoliticaCorrecta(empleado);
    }

    private void generarPeriodoActualConPoliticaCorrecta(EmpleadoEntity empleado) {
        LocalDate fechaBase = empleado.getFechaReingreso() != null
                ? empleado.getFechaReingreso()
                : empleado.getFechaAlta();

        LocalDate hoy = LocalDate.now();
        long aniosCompletos = ChronoUnit.YEARS.between(fechaBase, hoy);

        if (aniosCompletos < 1) {
            generarPeriodoSinAntiguedad(empleado, fechaBase);
            return;
        }

        // Los días se habilitan EN el aniversario, no antes
        int anioPeriodoDisponible = (int) aniosCompletos;

        if (!periodoRepository.existsByEmpleadoIdAndAnioLaboral(empleado.getId(), anioPeriodoDisponible)) {
            LocalDate fechaAniversario = fechaBase.plusYears(anioPeriodoDisponible - 1);

            PoliticaVacacionEscalaEntity politicaEscala = politicaEscalaRepository
                    .findPoliticaVigenteParaFecha(fechaAniversario)
                    .orElseThrow(() -> new IllegalStateException(
                            "No existe política para aniversario: " + fechaAniversario));

            generarPeriodoParaAnioConEscala(empleado, politicaEscala, anioPeriodoDisponible, fechaBase);
            log.info("Período generado con política '{}': empleado={}, año={}, aniversario={}",
                    politicaEscala.getNombre(), empleado.getId(), anioPeriodoDisponible, fechaAniversario);
        }
    }

    private void generarPeriodoSinAntiguedad(EmpleadoEntity empleado, LocalDate fechaBase) {
        int anioLaboral = 0;
        if (periodoRepository.existsByEmpleadoIdAndAnioLaboral(empleado.getId(), anioLaboral)) {
            return;
        }

        LocalDate finAnio = fechaBase.plusYears(1).minusDays(1);

        PeriodoVacacionalEntity periodo = new PeriodoVacacionalEntity();
        periodo.setEmpleadoId(empleado.getId());
        periodo.setAnioLaboral(anioLaboral);
        periodo.setFechaInicio(fechaBase);
        periodo.setFechaFin(finAnio);
        periodo.setDiasHabilitados(0);
        periodo.setDiasTomados(0);
        periodo.setDiasRestantes(0);
        periodo.setFechaCaducidad(finAnio);
        periodo.setEstatus(PeriodoVacacionalEntity.EstatusPeriodo.VIGENTE);
        periodo.setCreatedAt(java.time.LocalDateTime.now());
        periodo.setPeriodoNumero(0);
        periodo.setAnioGestion(fechaBase.getYear());

        periodoRepository.save(periodo);
        log.info("Período sin antigüedad creado: empleado={}, ingreso={}, primerAniversario={}",
                empleado.getId(), fechaBase, finAnio.plusDays(1));
    }


    private void generarPeriodoParaAnioConEscala(EmpleadoEntity empleado,
                                                 PoliticaVacacionEscalaEntity politica,
                                                 int anio, LocalDate fechaBase) {
        int diasCorresponden = politica.getDiasVacacionesPorAnio(anio);
        LocalDate inicioAnio = fechaBase.plusYears(anio - 1);
        LocalDate finAnio = inicioAnio.plusYears(1).minusDays(1);

        // Caducidad interna de la empresa: 12 meses desde el ANIVERSARIO que genera los días
        // El aniversario que genera los días es al FINAL del período (finAnio)
        // Ejemplo: Ingreso 1 ago 2022, año 3:
        // - inicioAnio: 1 ago 2024
        // - finAnio: 31 jul 2025
        // - Aniversario que genera días: 1 ago 2025
        // - Caducidad: 1 ago 2025 + 12 meses = 1 ago 2026
        LocalDate aniversarioQueGeneraDias = finAnio.plusDays(1); // Día después del fin = aniversario
        LocalDate caducidad = aniversarioQueGeneraDias.plusMonths(12);

        LocalDate hoy = LocalDate.now();
        PeriodoVacacionalEntity.EstatusPeriodo estatus = hoy.isAfter(caducidad)
                ? PeriodoVacacionalEntity.EstatusPeriodo.VENCIDO
                : PeriodoVacacionalEntity.EstatusPeriodo.VIGENTE;

        PeriodoVacacionalEntity periodo = new PeriodoVacacionalEntity();
        periodo.setEmpleadoId(empleado.getId());
        periodo.setAnioLaboral(anio);
        periodo.setFechaInicio(inicioAnio);
        periodo.setFechaFin(finAnio);
        periodo.setDiasHabilitados(diasCorresponden);
        periodo.setDiasTomados(0);
        periodo.setDiasRestantes(diasCorresponden);
        periodo.setFechaCaducidad(caducidad);
        periodo.setEstatus(estatus);
        periodo.setCreatedAt(java.time.LocalDateTime.now());
        periodo.setPeriodoNumero(anio);
        periodo.setAnioGestion(inicioAnio.getYear());

        periodoRepository.save(periodo);

        log.info("Período creado: empleado={}, año={}, días={}, aniversario={}, caducidad={}, política='{}'",
                empleado.getId(), anio, diasCorresponden, aniversarioQueGeneraDias, caducidad, politica.getNombre());
    }

    @Transactional
    public void generarPeriodoActual(EmpleadoEntity empleado, PoliticaVacacionEntity politica) {
        LocalDate fechaBase = empleado.getFechaReingreso() != null
                ? empleado.getFechaReingreso()
                : empleado.getFechaAlta();

        LocalDate hoy = LocalDate.now();
        long aniosCompletos = ChronoUnit.YEARS.between(fechaBase, hoy);

        if (aniosCompletos < 1) {
            generarPeriodoSinAntiguedad(empleado, fechaBase);
            return;
        }

        // Calcular el año del período en curso
        LocalDate ultimoAniversario = fechaBase.plusYears(aniosCompletos);
        int anioPeriodoEnCurso;

        if (hoy.isBefore(ultimoAniversario)) {
            // Aún no cumple el aniversario, usar año anterior
            anioPeriodoEnCurso = (int) aniosCompletos;
        } else {
            // Ya cumplió aniversario, usar año actual
            anioPeriodoEnCurso = (int) aniosCompletos + 1;
        }

        // Solo generar período en curso si no existe
        if (!periodoRepository.existsByEmpleadoIdAndAnioLaboral(empleado.getId(), anioPeriodoEnCurso)) {
            generarPeriodoParaAnio(empleado, politica, anioPeriodoEnCurso, fechaBase);
            log.info("Período en curso generado: empleado={}, año={}", empleado.getId(), anioPeriodoEnCurso);
        } else {
            log.info("Empleado {} ya tiene período en curso (año {})", empleado.getId(), anioPeriodoEnCurso);
        }
    }

    private void generarPeriodoParaAnio(EmpleadoEntity empleado, PoliticaVacacionEntity politica,
                                        int anio, LocalDate fechaBase) {
        int diasCorresponden = calcularDiasSegunAntiguedad(anio, politica);
        LocalDate inicioAnio = fechaBase.plusYears(anio - 1);
        LocalDate finAnio = inicioAnio.plusYears(1).minusDays(1);
        LocalDate caducidad = finAnio; // Caducan al final del año laboral, no a los 6 meses

        // Determinar estatus: VIGENTE si estamos dentro del año laboral
        LocalDate hoy = LocalDate.now();
        PeriodoVacacionalEntity.EstatusPeriodo estatus;

        if (hoy.isAfter(finAnio)) {
            estatus = PeriodoVacacionalEntity.EstatusPeriodo.VENCIDO;
        } else if (hoy.isBefore(inicioAnio)) {
            estatus = PeriodoVacacionalEntity.EstatusPeriodo.VIGENTE; // Futuro
        } else {
            estatus = PeriodoVacacionalEntity.EstatusPeriodo.VIGENTE; // En curso
        }

        PeriodoVacacionalEntity periodo = new PeriodoVacacionalEntity();
        periodo.setEmpleadoId(empleado.getId());
        periodo.setAnioLaboral(anio);
        periodo.setFechaInicio(inicioAnio);
        periodo.setFechaFin(finAnio);
        periodo.setDiasHabilitados(diasCorresponden);
        periodo.setDiasTomados(0);
        periodo.setDiasRestantes(diasCorresponden);
        periodo.setFechaCaducidad(caducidad);
        periodo.setEstatus(estatus);
        periodo.setCreatedAt(java.time.LocalDateTime.now());
        periodo.setPeriodoNumero(anio);
        periodo.setAnioGestion(inicioAnio.getYear());

        periodoRepository.save(periodo);

        log.info("Período generado: empleado={}, año={}, días={}, inicio={}, fin={}, estatus={}",
                empleado.getId(), anio, diasCorresponden, inicioAnio, finAnio, estatus);
    }
}
