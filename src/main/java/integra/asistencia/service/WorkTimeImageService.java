package integra.asistencia.service;

import integra.utils.ImageUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
public class WorkTimeImageService {

    private Path directorioFotos;

    @PostConstruct
    public void init() throws IOException {
        directorioFotos = Paths.get(System.getProperty("user.dir"), "img");
        Files.createDirectories(directorioFotos);
        log.info("📂 INICIALIZANDO DIRECTORIO DE IMÁGENES RELOJ CHECADOR: Directorio: {} , Directorio existe: {} , Se puede escribir: {}", directorioFotos.toAbsolutePath(), Files.exists(directorioFotos), Files.isWritable(directorioFotos));
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
}