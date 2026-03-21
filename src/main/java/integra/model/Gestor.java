package integra.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Gestor extends Empleado {
    private Integer nivel;
    private String proceso;

    public Gestor() {
        super(null, null);
    }

    public Gestor(Integer id, String nombreCompleto, Integer nivel, String tipoProceso) {
        super(id, nombreCompleto);
        this.nivel = nivel;
        this.proceso = tipoProceso;
    }
}
