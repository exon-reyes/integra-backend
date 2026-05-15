package integra.model;

import integra.vacacion.domain.model.EstatusSolicitud;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Gestor extends Empleado {
    private Integer nivel;
    private EstatusSolicitud estatusSolicitud;
    private String comentario;

    public Gestor() {
        super(null, null);
    }

    public Gestor(Integer id, String nombreCompleto, Integer nivel) {
        super(id, nombreCompleto);
        this.nivel = nivel;
    }
}
