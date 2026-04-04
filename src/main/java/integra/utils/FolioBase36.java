package integra.utils;

import java.time.LocalDate;
import java.util.Random;

public final class FolioBase36 {
    private static final Random random = new Random();
    // Generar una letra aleatoria entre A y Z
    private static char generarLetraAleatoria() {
        return (char) (random.nextInt(26) + 'A');
    }

    // Convertir un número entero a base 36
    private static String convertirBase36(int numero) {
        if (numero == 0) {
            return "0";
        }
        StringBuilder sb = new StringBuilder();
        while (numero > 0) {
            int rem = numero % 36;
            char ch = (char) (rem < 10 ? (rem + '0') : (rem - 10 + 'A'));
            sb.append(ch);
            numero /= 36;
        }
        return sb.reverse()
                .toString();
    }

    public static String generar() {
        LocalDate ahora = LocalDate.now();
        int year = ahora.getYear() % 100; // Obtener los últimos dos dígitos del año
        int dayOfYear = ahora.getDayOfYear();
        // Generar dos letras aleatorias (A-Z)
        char letra1 = generarLetraAleatoria();
        char letra2 = generarLetraAleatoria();
        // Generar un número aleatorio de 10000 a 99999
        int numeroAleatorio = 10000 + random.nextInt(90000);
        return (convertirBase36(year) + convertirBase36(dayOfYear) + Integer.toString(numeroAleatorio,
                36) + letra1 + letra2).toUpperCase();
    }
}
