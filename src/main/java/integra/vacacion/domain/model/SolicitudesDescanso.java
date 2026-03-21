package integra.vacacion.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SolicitudesDescanso {
    private List<SolicitudEmpleado> pendientes;
    private List<SolicitudEmpleado> aprobadas;
    private List<SolicitudEmpleado> canceladas;
    private int sumaPendientes;
    private int sumaAprobadas;
    private int sumaCanceladas;

    public SolicitudesDescanso(List<SolicitudEmpleado> pendientes, List<SolicitudEmpleado> aprobadas, List<SolicitudEmpleado> canceladas) {
        this.pendientes = pendientes;
        this.canceladas = canceladas;
        this.aprobadas = aprobadas;

    }

    public void setIndicadores(int sumaPendientes, int sumaAprobadas, int sumaCanceladas) {
        this.sumaPendientes = sumaPendientes;
        this.sumaAprobadas = sumaAprobadas;
        this.sumaCanceladas = sumaCanceladas;
    }
}
