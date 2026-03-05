package integra.asistencia.service;

import integra.utils.ImageUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class WorkImageService {

    private final String directorioFotosConfig;
    private final String nombreServicio;
    private Path directorioFotos;

    public WorkImageService() {
        this("img", "RELOJ CHECADOR");
    }

    public WorkImageService(String directorioFotosConfig, String nombreServicio) {
        this.directorioFotosConfig = directorioFotosConfig;
        this.nombreServicio = nombreServicio;
    }

    @PostConstruct
    public void init() throws IOException {
        directorioFotos = Paths.get(System.getProperty("user.dir"), directorioFotosConfig);
        Files.createDirectories(directorioFotos);
        log.info(
                "📂 INICIALIZANDO DIRECTORIO DE IMÁGENES {}: Directorio: {} , Directorio existe: {} , Se puede escribir: {}",
                nombreServicio, directorioFotos.toAbsolutePath(), Files.exists(directorioFotos),
                Files.isWritable(directorioFotos));
    }

    public String saveImg(String data, Integer idEmpleado) throws IOException {
        return ImageUtils.saveBase64Image(data, idEmpleado, directorioFotos);
    }

    public Resource getImg(String filename) {
        return ImageUtils.getImage(filename, directorioFotos);
    }

    public Resource getResizedImg(String filename, int width, int height) throws IOException {
        return ImageUtils.getResizedImage(filename, width, height, directorioFotos);
    }

    public void deleteImg(String filename) {
        if (filename == null || filename.isBlank())
            return;
        try {
            Path fileToDeletePath = directorioFotos.resolve(filename).normalize();
            Files.deleteIfExists(fileToDeletePath);
            log.info("Archivo {} eliminado exitosamente del servicio {}", filename, nombreServicio);
        } catch (IOException e) {
            log.error("Error al eliminar la imagen {} del servicio {}", filename, nombreServicio, e);
        }
    }
}