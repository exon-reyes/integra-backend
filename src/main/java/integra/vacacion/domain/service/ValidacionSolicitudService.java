//package integra.vacacion.domain.service;
//
//import integra.empleado.entity.EmpleadoEntity;
//import integra.empleado.repository.EmpleadoRepository;
//import integra.vacacion.domain.model.EstatusSolicitud;
//import integra.vacacion.entity.SolicitudVacacionEntity;
//import integra.vacacion.exception.VacacionException;
//import integra.vacacion.repository.DescansoEmpleadoRepository;
//import integra.vacacion.repository.PeriodoVacacionalRepository;
//import integra.vacacion.repository.PeriodoVedaRepository;
//import integra.vacacion.repository.SolicitudVacacionRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.time.temporal.ChronoUnit;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class ValidacionSolicitudService {
//
//    private final PeriodoVacacionalRepository periodoVacacionalRepository;
//    private final PeriodoVedaRepository periodoVedaRepository;
//    private final SolicitudVacacionRepository solicitudVacacionRepository;
//    private final DescansoEmpleadoRepository descansoEmpleadoRepository;
//    private final EmpleadoRepository empleadoRepository;
//    private final CalculoDiasLaboralesService calculoService;
//
//    public void validarSolicitud(Integer empleadoId, LocalDate inicio, LocalDate fin) {
//        // Verificar que existe el empleado
//        if (!empleadoRepository.existsById(empleadoId)) {
//            throw VacacionException.empleadoNoEncontrado(empleadoId);
//        }
//
//        EmpleadoEntity empleado = empleadoRepository.findById(empleadoId).get();
//
//        // Los descansos son OPCIONALES. Si el empleado no los configura,
//        // el cálculo toma días corridos. No se bloquea la solicitud.
//        validarAntiguedadMinima(empleado);
//        validarSaldo(empleadoId, inicio, fin);
//        validarPeriodoVeda(inicio, fin);
//        validarTraslape(empleadoId, inicio, fin);
//    }
//
//
//    private void validarAntiguedadMinima(EmpleadoEntity empleado) {
//        LocalDate fechaIngreso = empleado.getFechaAlta();
//        if (empleado.getFechaReingreso() != null) {
//            fechaIngreso = empleado.getFechaReingreso();
//        }
//
//        long anios = ChronoUnit.YEARS.between(fechaIngreso, LocalDate.now());
//        if (anios < 1) {
//            throw VacacionException.antiguedadInsuficiente();
//        }
//    }
//
//    private void validarSaldo(Integer empleadoId, LocalDate inicio, LocalDate fin) {
//        int saldo = periodoVacacionalRepository.sumDiasRestantesByEmpleado(empleadoId);
//
//        if (saldo <= 0) {
//            throw VacacionException.sinPeriodoActivo();
//        }
//
//        // Calcular días laborables reales (excluye festivos y descansos aprobados del empleado)
//        int diasSolicitados = calculoService.calcularDiasLaborables(inicio, fin, empleadoId);
//
//        if (diasSolicitados > saldo) {
//            throw VacacionException.saldoInsuficiente(saldo, diasSolicitados);
//        }
//    }
//
//    private void validarPeriodoVeda(LocalDate inicio, LocalDate fin) {
//        boolean existeVeda = periodoVedaRepository.existsActivoBetween(inicio, fin);
//        if (existeVeda) {
//            throw VacacionException.periodoVedaActivo();
//        }
//    }
//
//    private void validarTraslape(Integer empleadoId, LocalDate inicio, LocalDate fin) {
//        List<SolicitudVacacionEntity> solicitudesActivas = solicitudVacacionRepository
//                .findSolicitudesActivasEnRango(empleadoId, inicio, fin,
//                        List.of(EstatusSolicitud.PENDIENTE, EstatusSolicitud.APROBADA));
//
//        if (!solicitudesActivas.isEmpty()) {
//            throw VacacionException.traslapeDetectado();
//        }
//    }
//
//    /**
//     * Compara una lista de fechas (prospecto de vacaciones) contra
//     * todos los descansos del empleado (APROBADOS y PENDIENTES).
//     *
//     * @param empleadoId el identificador del empleado
//     * @param diasVacaciones las fechas propuestas para tomar como vacaciones
//     * @return una nueva lista que solo contiene aquellos días que
//     *         <strong>NO</strong> coinciden con ningún descanso.
//     */
//    public List<LocalDate> filtrarVacacionesSinConflictoDescansos(
//            Integer empleadoId,
//            List<LocalDate> diasVacaciones) {
//
//        if (diasVacaciones == null || diasVacaciones.isEmpty()) {
//            return diasVacaciones;
//        }
//
//        // Recuperar tanto los aprobados como los pendientes
//        java.util.Set<LocalDate> descansos = descansoEmpleadoRepository
//                .findTodasFechasDescansoByEmpleado(empleadoId);
//
//        if (descansos == null || descansos.isEmpty()) {
//            return diasVacaciones;
//        }
//
//        return diasVacaciones.stream()
//                .filter(fecha -> !descansos.contains(fecha))
//                .toList();
//    }
//}
