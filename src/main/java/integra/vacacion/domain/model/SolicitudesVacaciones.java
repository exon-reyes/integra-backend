package integra.vacacion.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SolicitudesVacaciones {
    private List<SolicitudEmpleado> pendientes;
    private List<SolicitudEmpleado> aprobadas;
    private List<SolicitudEmpleado> canceladas;
    private List<SolicitudEmpleado> disfrutadas;

    private int sumaPendientesAprobacion;
    private int sumaAprobadas;
    private int sumaDisfrutados;
    private int sumaCancelados;

    public void setIndicadores(int sumaDisfrutados, int sumaAprobadas, int sumaPendientesAprobacion, int sumaCancelados) {
        this.sumaDisfrutados = sumaDisfrutados;
        this.sumaAprobadas = sumaAprobadas;
        this.sumaPendientesAprobacion = sumaPendientesAprobacion;
        this.sumaCancelados = sumaCancelados;
    }
}
