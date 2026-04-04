package integra.utils;

import java.time.LocalDate;

public final class FolioGenerator {

    private FolioGenerator() {
    }

    /**
     * Genera un folio con el formato: YY + DDD (día del año) + empleadoId + tipo
     * Ejemplo: 26 (año) + 085 (26 de marzo) + 724 (ID) + 1 (Tipo) -> 260857241
     * * @param empleadoId ID del empleado
     * @param tipo 0 para Vacaciones, 1 para Descansos
     * @return long con el folio generado
     */
    public static long generar(Integer empleadoId, Integer tipo) {
        LocalDate hoy = LocalDate.now();

        long yearShort = hoy.getYear() % 100;
        long diaDelAnio = hoy.getDayOfYear(); // Devuelve de 1 a 366

        // 1. Combinamos Año y Día del Año (YYDDD)
        // Multiplicamos el año por 1000 para dejar 3 espacios para el día (001-366)
        long fechaRef = (yearShort * 1000) + diaDelAnio;

        // 2. Concatenamos el empleadoId y el tipo
        // Multiplicamos la fecha por 10,000 para dar espacio a un empleadoId
        // de hasta 3 dígitos (999) más el dígito del tipo (0/1).

        return (fechaRef * 10000L) + (empleadoId * 10L) + tipo;
    }
}