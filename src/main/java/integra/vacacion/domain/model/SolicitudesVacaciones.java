package integra.vacacion.domain.model;

import integra.vacacion.dto.response.FechaSolicitud;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SolicitudesVacaciones {
    private List<FechaSolicitud> pendientes;
    private List<FechaSolicitud> aprobadas;
    private List<FechaSolicitud> canceladas;
    private List<FechaSolicitud> disfrutadas;

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
