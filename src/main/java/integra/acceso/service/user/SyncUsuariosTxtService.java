package integra.acceso.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class SyncUsuariosTxtService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    @Value("${integra.sync.usuarios.output-dir:./integra-config/sync-usuarios/}")
    private String outputDir;

    public String generarTxt(List<SyncUsuariosExcelService.FilaUsuario> filas) {
        new File(outputDir).mkdirs();
        String nombre = "sync_usuarios_" + LocalDateTime.now().format(FMT) + ".txt";
        String ruta = outputDir + nombre;

        try (PrintWriter writer = new PrintWriter(new FileWriter(ruta))) {
            for (SyncUsuariosExcelService.FilaUsuario fila : filas) {
                // El txt debe contener: usuario \n contraseña
                writer.println("Usuario: " + fila.usuario());
                writer.println("Contraseña: " + fila.password());
                writer.println("----------------------------------------");
            }
            log.info("[SyncUsuarios] TXT generado: {}", ruta);
        } catch (Exception e) {
            log.error("[SyncUsuarios] Error al generar TXT: {}", e.getMessage());
        }

        return ruta;
    }
}
