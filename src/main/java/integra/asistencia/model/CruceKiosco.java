package integra.asistencia.model;

import integra.model.Unidad;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CruceKiosco {
    private Integer id;
    private LocalDateTime fecha;
    private Integer empleadoId;
    private Integer asistenciaId;
    private String pathImg;
    private Unidad unidadRegistro;
    private Unidad unidadEsperada;
    private String accion;
}
