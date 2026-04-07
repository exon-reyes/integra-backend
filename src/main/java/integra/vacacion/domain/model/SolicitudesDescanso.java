package integra.vacacion.domain.model;

import integra.vacacion.dto.response.FechaSolicitud;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SolicitudesDescanso {
    private List<FechaSolicitud> pendientes;
    private List<FechaSolicitud> aprobadas;
    private List<FechaSolicitud> canceladas;
    private List<FechaSolicitud> disfrutadas;

    private int sumaPendientes;
    private int sumaAprobadas;
    private int sumaCanceladas;
    private int sumaDisfrutadas;

    public void setIndicadores(int sumaPendientes, int sumaAprobadas, int sumaCanceladas, int sumaDisfrutadas) {
        this.sumaPendientes = sumaPendientes;
        this.sumaAprobadas = sumaAprobadas;
        this.sumaCanceladas = sumaCanceladas;
        this.sumaDisfrutadas = sumaDisfrutadas;
    }
}
