package integra.vacacion.domain.service;

import integra.empleado.entity.EmpleadoEntity;
import integra.empleado.repository.EmpleadoRepository;
import integra.vacacion.entity.SolicitudVacacionEntity;
import integra.vacacion.entity.SolicitudVacacionEntity.EstatusSolicitud;
import integra.vacacion.exception.VacacionException;
import integra.vacacion.repository.DescansoEmpleadoRepository;
import integra.vacacion.repository.PeriodoVacacionalRepository;
import integra.vacacion.repository.PeriodoVedaRepository;
import integra.vacacion.repository.SolicitudVacacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ValidacionSolicitudService {

    private final PeriodoVacacionalRepository periodoVacacionalRepository;
    private final PeriodoVedaRepository periodoVedaRepository;
    private final SolicitudVacacionRepository solicitudVacacionRepository;
    private final EmpleadoRepository empleadoRepository;
    private final DescansoEmpleadoRepository descansoRepository;

    public void validarSolicitud(Integer empleadoId, LocalDate inicio, LocalDate fin) {
        // Primero verificar que existe el empleado
        if (!empleadoRepository.existsById(empleadoId)) {
            throw VacacionException.empleadoNoEncontrado(empleadoId);
        }

        EmpleadoEntity empleado = empleadoRepository.findById(empleadoId).get();

        validarDescansoConfigurado(empleadoId);
        validarAntiguedadMinima(empleado);
        validarSaldo(empleadoId, inicio, fin);
        validarPeriodoVeda(inicio, fin);
        validarTraslape(empleadoId, inicio, fin);
    }

    private void validarDescansoConfigurado(Integer empleadoId) {
        if (!descansoRepository.existsByEmpleadoIdAndActivoTrue(empleadoId)) {
            throw VacacionException.descansosNoConfigurados();
        }
    }

    private void validarAntiguedadMinima(EmpleadoEntity empleado) {
        LocalDate fechaIngreso = empleado.getFechaAlta();
        if (empleado.getFechaReingreso() != null) {
            fechaIngreso = empleado.getFechaReingreso();
        }

        long anios = ChronoUnit.YEARS.between(fechaIngreso, LocalDate.now());
        if (anios < 1) {
            throw VacacionException.antiguedadInsuficiente();
        }
    }

    private void validarSaldo(Integer empleadoId, LocalDate inicio, LocalDate fin) {
        int saldo = periodoVacacionalRepository.sumDiasRestantesByEmpleado(empleadoId);

        // Calcular días laborales solicitados
        int diasSolicitados = (int) ChronoUnit.DAYS.between(inicio, fin) + 1;
        // Nota: La validación más precisa de días laborales debería usar CalculoDiasLaboralesService
        // Por ahora usamos una estimación básica

        if (saldo < diasSolicitados) {
            throw VacacionException.saldoInsuficiente(saldo, diasSolicitados);
        }

        if (saldo <= 0) {
            throw VacacionException.sinPeriodoActivo();
        }
    }

    private void validarPeriodoVeda(LocalDate inicio, LocalDate fin) {
        boolean existeVeda = periodoVedaRepository.existsActivoBetween(inicio, fin);
        if (existeVeda) {
            throw VacacionException.periodoVedaActivo();
        }
    }

    private void validarTraslape(Integer empleadoId, LocalDate inicio, LocalDate fin) {
        List<SolicitudVacacionEntity> solicitudesActivas = solicitudVacacionRepository
                .findSolicitudesActivasEnRango(empleadoId, inicio, fin,
                        List.of(EstatusSolicitud.PENDIENTE, EstatusSolicitud.APROBADA));

        if (!solicitudesActivas.isEmpty()) {
            throw VacacionException.traslapeDetectado();
        }
    }
}
