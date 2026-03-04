package integra.config.cache;

import integra.acceso.service.account.TokenVersionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado de realizar el "precalentamiento" (warmup) de las cachés
 * del sistema
 * una vez que la aplicación ha iniciado completamente.
 *
 * <p>
 * El objetivo principal de este servicio es cargar en memoria ciertos datos
 * críticos o de uso
 * frecuente, de manera que las primeras peticiones al backend no experimenten
 * latencia
 * causada por una caché vacía.
 * </p>
 *
 * <p>
 * Este proceso se ejecuta automáticamente al recibir el evento
 * {@link ApplicationReadyEvent}, lo que garantiza que todos los componentes de
 * Spring
 * ya estén inicializados antes de comenzar el precalentamiento.
 * </p>
 *
 * <p>
 * En caso de ocurrir un error durante la carga de datos, el proceso será
 * registrado
 * en el log pero no impedirá que la aplicación continúe con su arranque normal.
 * </p>
 *
 * @author Pablo Reyes
 * @version 1.0.1-Beta
 * @since 2025
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CacheWarmupService {
    private final TokenVersionService tokenVersionService;

    /**
     * Método que se ejecuta automáticamente al completar el arranque de la
     * aplicación.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void warmupCaches() {
        log.info("=== INICIANDO PRECARGA DE CACHÉ ===");
        try {
//            log.info("Cargando catálogo de unidades...");
//            catalogoUnidades.obtenerUnidades();

            log.info("Cargando versiones de tokens...");
            tokenVersionService.obtenerTokensCache(); // <--- Agrega esto

            log.info("=== CACHÉ PRECALENTADA EXITOSAMENTE ===");
        } catch (Exception e) {
            log.error("Error de incio de cachés. " + "La aplicación continuará su ejecución normal.", e);
            // No se lanza la excepción para no interrumpir el arranque del sistema
        }
    }
}
