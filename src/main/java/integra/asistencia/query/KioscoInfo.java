package integra.asistencia.query;

import integra.empresa.entity.UnidadEntity;

import java.io.Serializable;
import java.time.LocalTime;

/**
 * DTO for {@link UnidadEntity}
 */
public record KioscoInfo(Integer id, String nombreCompleto, Boolean requiereCamara,
                         String codigoAutorizacionKiosco, Boolean requiereCodigo, Integer versionKiosco,
                         LocalTime tiempoCompensacion, Integer tiempoEsperaKiosco) implements Serializable {
}