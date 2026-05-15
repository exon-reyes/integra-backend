package integra.config.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

@Slf4j
@Service
public class BusinessPropertiesService {

    private static final String PROPERTIES_PATH = "./integra-config/business.properties";
    private static final String KEY_ULTIMA_SINCRONIZACION = "vacaciones.sincronizacion.ultima";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public LocalDateTime leerUltimaSincronizacion() {
        Properties props = cargar();
        String valor = props.getProperty(KEY_ULTIMA_SINCRONIZACION, "").trim();
        if (valor.isEmpty()) return null;
        try {
            return LocalDateTime.parse(valor, FORMATTER);
        } catch (Exception e) {
            log.warn("[BusinessProperties] No se pudo parsear ultima sincronizacion: {}", valor);
            return null;
        }
    }

    public void actualizarUltimaSincronizacion(LocalDateTime fechaHora) {
        Properties props = cargar();
        props.setProperty(KEY_ULTIMA_SINCRONIZACION, fechaHora.format(FORMATTER));
        guardar(props);
        log.info("[BusinessProperties] Ultima sincronizacion actualizada: {}", fechaHora.format(FORMATTER));
    }

    public boolean yaSincronizadoHoy() {
        LocalDateTime ultima = leerUltimaSincronizacion();
        return ultima != null && ultima.toLocalDate().equals(LocalDate.now());
    }

    private Properties cargar() {
        Properties props = new Properties();
        try (InputStream is = new FileInputStream(PROPERTIES_PATH)) {
            props.load(is);
        } catch (IOException e) {
            log.warn("[BusinessProperties] No se pudo leer el archivo: {}", e.getMessage());
        }
        return props;
    }

    private void guardar(Properties props) {
        try (OutputStream os = new FileOutputStream(PROPERTIES_PATH)) {
            props.store(os, "Configuracion Integra");
        } catch (IOException e) {
            log.error("[BusinessProperties] No se pudo guardar el archivo: {}", e.getMessage());
        }
    }
}
