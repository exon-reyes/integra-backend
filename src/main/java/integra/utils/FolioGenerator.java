package integra.utils;

import java.time.LocalDate;
import java.time.LocalTime;

public final class FolioGenerator {

    private FolioGenerator() {
    }

    /**
     * MÉTODO DE AGRUPACIÓN (Determinista)
     * Si el mismo empleado solicita el mismo tipo hoy, siempre obtendrá el mismo folio.
     * Formato: YY + DDD + empleadoId + tipo
     * Ejemplo: 26 (Año) + 096 (Día) + 724 (ID) + 0 (Vacación) -> 260967240
     * * @param empleadoId ID del empleado
     * @param tipo 0 para Vacaciones, 1 para Descansos
     * @return long con el folio de grupo diario
     */
    public static long generarFolioDia(Integer empleadoId, Integer tipo) {
        LocalDate hoy = LocalDate.now();

        long yearShort = hoy.getYear() % 100;
        long diaDelAnio = hoy.getDayOfYear(); // 1 a 366

        // 1. Combinamos Año y Día del Año (YYDDD)
        long fechaRef = (yearShort * 1000L) + diaDelAnio;

        // 2. Concatenamos el empleadoId y el tipo
        // Espacio para empleadoId de hasta 3 dígitos (999) + 1 dígito de tipo
        return (fechaRef * 10000L) + (empleadoId * 10L) + tipo;
    }

    /**
     * MÉTODO ÚNICO (Instante de tiempo)
     * Genera un folio que cambia cada milisegundo. No se repite aunque sea
     * el mismo empleado, ya que usa la estampa de tiempo real.
     * * @return long con un folio único basado en segundos y milisegundos del día
     */
    public static long generarFolioNumericoUnico() {
        LocalTime tiempo = LocalTime.now();

        // Segundos del día (0-86400) * 1000 + milisegundos actuales (0-999)
        // Esto garantiza un número único cada milésima de segundo en el día.
        return (tiempo.toSecondOfDay() * 1000L) + (System.currentTimeMillis() % 1000);
    }
}