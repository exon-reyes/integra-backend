package integra.asistencia.service.jornada;

import integra.asistencia.service.WorkImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseAsistenciaService {
    protected final WorkImageService workTimeImageService;

    /**
     * Guarda la foto de forma síncrona ANTES de abrir la transacción.
     * Acepta MultipartFile: imagen ya comprimida y redimensionada desde Angular.
     * El I/O de disco no debe ocurrir mientras la conexión de BD está retenida.
     */
    protected String guardarFotoSiExiste(MultipartFile foto, Integer empleadoId) {
        if (foto == null || foto.isEmpty()) return null;
        try {
            return workTimeImageService.saveMultipartImg(foto, empleadoId);
        } catch (IOException e) {
            log.error("Error al guardar imagen del empleado {}", empleadoId, e);
            throw new RuntimeException("Error al guardar la imagen, contacte al administrador", e);
        }
    }

    /**
     * Guarda la foto de forma asíncrona, útil cuando el path no se requiere
     * para la persistencia (ej. actualización diferida).
     */
    protected CompletableFuture<String> guardarFotoAsync(MultipartFile foto, Integer empleadoId) {
        if (foto == null || foto.isEmpty()) return CompletableFuture.completedFuture(null);
        return CompletableFuture.supplyAsync(() -> {
            try {
                return workTimeImageService.saveMultipartImg(foto, empleadoId);
            } catch (IOException e) {
                log.error("Error async al guardar imagen del empleado {}", empleadoId, e);
                return null;
            }
        });
    }
}
