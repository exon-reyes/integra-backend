package integra.asistencia.service.kiosco;

import integra.asistencia.exception.AsistenciaDomainException;
import integra.core.service.ParamKey;
import integra.core.service.ParamsService;
import integra.empresa.repository.UnidadRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalTime;

/**
 * Servicio que proporciona operaciones de comando para la gestión de kioscos.
 *
 * <p>Esta clase ofrece métodos para administrar diferentes aspectos de los kioscos,
 * incluyendo la configuración de uso de cámara, generación y uso de códigos de configuración,
 * y gestión de solicitudes de configuración.</p>
 *
 * <p>Ejemplo de uso:
 * <pre>
 * {@code
 * @Autowired
 * private KioscoService kioscoService;
 *
 * // Actualizar uso de cámara
 * kioscoService.actualizarUsoCamara(123, true);
 *
 * // Generar código de configuración
 * String codigo = kioscoService.generarCodigoConfig(123);
 *
 * // Solicitar código de configuración
 * kioscoService.solicitarCodigo(123);
 *
 * // Usar código de configuración
 * kioscoService.usarCodigoConfig(123, "12345");
 * }
 * </pre>
 * </p>
 *
 * @author Pablo Reyes
 * @version 1.0
 * @since 1.0
 *
 */
@Service
@RequiredArgsConstructor
@Transactional
public class KioscoService {
    private final UnidadRepository repository;
    private final ParamsService paramsService;

    /**
     * Actualiza el estatus de uso de cámara para el kiosco especificado.
     *
     * @param idKiosco     Identificador único del kiosco a actualizar
     * @param nuevoEstatus Nuevo estatus de uso de cámara (true para habilitado, false para deshabilitado)
     * @throws AsistenciaDomainException si no se puede actualizar el kiosco
     */
    public void actualizarUsoCamara(Integer idKiosco, Boolean nuevoEstatus) {
        SecureRandom random = new SecureRandom();
        Integer version = 10000 + random.nextInt(90000);
        int registrosActualizados = repository.actualizarUsoCamara(nuevoEstatus, version, idKiosco);
        if (registrosActualizados == 0) {
            throw AsistenciaDomainException.kioscoNotConfigured(idKiosco.longValue());
        }
    }


    /**
     * Genera un código de configuración aleatorio para el kiosco especificado.
     *
     * @param id Identificador único del kiosco al que se asignará el código de configuración
     * @return El código de configuración generado como una cadena de 5 dígitos
     */
    public String generarCodigoConfig(Integer id) {
        String codigo = String.valueOf(10000 + new SecureRandom().nextInt(90000));
        repository.actualizarSolicitudCodigoConfig(false, id);
        repository.asignarCodigoConfig(codigo, id);
        return codigo;
    }


    /**
     * Solicita un código de configuración para el kiosco especificado.
     *
     * @param id Identificador único del kiosco que solicita el código
     * @throws AsistenciaDomainException si no se puede registrar la solicitud de código
     */
    public void solicitarCodigo(Integer id) {
        if (repository.actualizarSolicitudCodigoConfig(true, id) == 0) {
            throw AsistenciaDomainException.kioscoNotConfigured(id.longValue());
        }
    }


    /**
     * Usa un código de configuración para el kiosco especificado.
     *
     * <p>Este método verifica si el código proporcionado existe en el sistema.
     * Si el código es válido, se procede a eliminar el código de configuración
     * del kiosco especificado.</p>
     *
     * @param id     Identificador único del kiosco que usará el código
     * @param codigo Código de configuración a utilizar
     * @throws AsistenciaDomainException si el código no existe o no es válido
     * @see UnidadRepository#existeCodigo(String, Integer) para verificar la existencia del código
     * @see UnidadRepository#asignarCodigoConfig(String, Integer) para eliminar el código del kiosco
     */
    public void usarCodigoConfig(Integer id, String codigo) {
        if (repository.existeCodigo(codigo, id)) {
            repository.asignarCodigoConfig(null, id);
        } else {
            throw AsistenciaDomainException.notFound(id.longValue());
        }

    }

    public void cancelarRequiereCodigo(Integer id) {
        if (repository.actualizarSolicitudCodigoConfig(false, id) == 0) {
            throw AsistenciaDomainException.kioscoNotConfigured(id.longValue());
        }
    }

    public void actualizarCompensacion(Integer id, LocalTime compensacion) {
        if (repository.actualizarCompensacionKiosco(compensacion, id) == 0) {
            throw AsistenciaDomainException.kioscoNotConfigured(id.longValue());
        }
    }

    public void actualizarTiempoEspera(Integer tiempoEspera) {
        paramsService.updateValorById(ParamKey.TIEMPO_CAPTURA_KIOSCO.getId(), tiempoEspera.toString());
    }
}
