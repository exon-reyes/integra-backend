package integra.vacacion.util;

import integra.empleado.query.EmpleadoVacacionInfo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class VacacionUtil {
    public static LocalDate proximoAniversario(LocalDate fechaIngreso) {

        return fechaIngreso.plusYears(calcularAntiguedad(fechaIngreso) + 1);
    }

    public static LocalDate obtenerFechaIngreso(EmpleadoVacacionInfo user) {
        return user.fechaReingreso() != null ? user.fechaReingreso() : user.fechaAlta();
    }

    public static int calcularAntiguedad(LocalDate fechaIngreso) {
        return (int) ChronoUnit.YEARS.between(fechaIngreso, LocalDate.now());
    }
}
