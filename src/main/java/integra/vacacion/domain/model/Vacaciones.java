package integra.vacacion.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class Vacaciones {
    private List<Solicitud> pendientes;
    private List<Solicitud> aprobadas;
    private List<Solicitud> canceladas;
    private List<Solicitud> disfrudadas;
    private List<Solicitud> aprobadasPorTomar;
}
