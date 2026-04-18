package integra.asistencia.service.jornada;

import integra.asistencia.service.WorkImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseAsistenciaService {
    protected final WorkImageService workTimeImageService;

    /**
     * Guarda la foto de forma síncrona ANTES de abrir la transacción.
     * El I/O de disco no debe ocurrir mientras la conexión de BD está retenida.
     */
    protected String guardarFotoSiExiste(String foto, Integer empleadoId) {
        if (foto == null) return null;
        try {
            return workTimeImageService.saveImg(foto, empleadoId);
        } catch (IOException e) {
            log.error("Error al guardar imagen del empleado {}", empleadoId, e);
            throw new RuntimeException("Error al guardar la imagen, contacte al administrador", e);
        }
    }

    /**
     * Guarda la foto de forma asíncrona, útil cuando el path no se requiere
     * para la persistencia (ej. actualización diferida).
     */
    protected CompletableFuture<String> guardarFotoAsync(String foto, Integer empleadoId) {
        if (foto == null) return CompletableFuture.completedFuture(null);
        return CompletableFuture.supplyAsync(() -> {
            try {
                return workTimeImageService.saveImg(foto, empleadoId);
            } catch (IOException e) {
                log.error("Error async al guardar imagen del empleado {}", empleadoId, e);
                return null;
            }
        });
    }
}
