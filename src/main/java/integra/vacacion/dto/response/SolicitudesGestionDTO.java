package integra.vacacion.dto.response;

import integra.model.Empleado;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SolicitudesGestionDTO {
    private Long id;
    private Empleado colaborador;
    private String unidad;
    private String estatusJefe;
    private String estatusRrhh;
    private int diasAprobados;
    private int diasTotalSolicitud;
    private long folioSolicitud;
    private List<FechaSolicitud> solicitudes;
    private String estatusGeneral;
    private String tipoSolicitud;
}
