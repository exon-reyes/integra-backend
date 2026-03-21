package integra.vacacion.service.validation;

import integra.model.Empleado;
import integra.vacacion.domain.model.DashboardSolicitudTiempo;
import integra.vacacion.domain.model.SolicitudEmpleado;
import integra.vacacion.domain.model.SolicitudesDescanso;
import integra.vacacion.domain.model.SolicitudesVacaciones;
import integra.vacacion.dto.response.Festivo;
import integra.vacacion.exception.VacacionException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SolicitudValidatorService {

    public void validarAntiguedadMinima(Empleado empleado) {
        LocalDate fechaIngreso = empleado.getFechaReingreso() != null
                ? empleado.getFechaReingreso()
                : empleado.getFechaAlta();


        if (fechaIngreso == null || ChronoUnit.YEARS.between(fechaIngreso, LocalDate.now()) < 1) {
            throw VacacionException.antiguedadInsuficiente();
        }
    }

    public Set<LocalDate> filtrarCruceSolicitudVacaciones(Set<LocalDate> fechasSolicitadas,
                                                          DashboardSolicitudTiempo dashboard,
                                                          List<Festivo> festivos) {

        if (fechasSolicitadas == null || fechasSolicitadas.isEmpty()) {
            return fechasSolicitadas;
        }

        Set<LocalDate> excluidas = obtenerFechasExcluidas(dashboard, festivos);

        // Fast-path
        if (excluidas.isEmpty()) {
            return fechasSolicitadas;
        }

        Set<LocalDate> resultado = new HashSet<>(fechasSolicitadas.size());

        for (LocalDate fecha : fechasSolicitadas) {
            if (!excluidas.contains(fecha)) {
                resultado.add(fecha);
            }
        }

        return resultado;
    }

    private Set<LocalDate> obtenerFechasExcluidas(DashboardSolicitudTiempo dashboard,
                                                  List<Festivo> festivos) {

        int capacity = 32;

        if (festivos != null) {
            capacity += festivos.size();
        }

        Set<LocalDate> excluidas = new HashSet<>(capacity);

        // 1. Solicitudes existentes
        if (dashboard != null) {

            SolicitudesDescanso descansos = dashboard.getDescansos();
            if (descansos != null) {
                extraerFechas(descansos.getPendientes(), excluidas);
                extraerFechas(descansos.getAprobadas(), excluidas);
            }

            SolicitudesVacaciones vacaciones = dashboard.getVacaciones();
            if (vacaciones != null) {
                extraerFechas(vacaciones.getPendientes(), excluidas);
                extraerFechas(vacaciones.getAprobadas(), excluidas);
            }
        }

        // 2. Festivos activos
        if (festivos != null && !festivos.isEmpty()) {
            for (int i = 0, size = festivos.size(); i < size; i++) {
                Festivo festivo = festivos.get(i);

                if (festivo.activo() == Boolean.TRUE) {
                    LocalDate fecha = festivo.fecha();
                    if (fecha != null) {
                        excluidas.add(fecha);
                    }
                }
            }
        }

        return excluidas;
    }

    private void extraerFechas(List<SolicitudEmpleado> solicitudes, Set<LocalDate> destino) {
        if (solicitudes == null || solicitudes.isEmpty()) {
            return;
        }


        for (int i = 0, size = solicitudes.size(); i < size; i++) {
            LocalDate fecha = solicitudes.get(i).getFecha();
            if (fecha != null) {
                destino.add(fecha);
            }
        }
    }
}