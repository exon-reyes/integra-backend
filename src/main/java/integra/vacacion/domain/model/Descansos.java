package integra.vacacion.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Descansos {
    private List<Descanso> pendientes;
    private List<Descanso> aprobados;
    private List<Descanso> rechazados;

}
