package integra.core.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Builder
public class ParamsDTO {
    private Integer idPuestoNocturno;
    private LocalTime horaInicioNocturno;
    private String defaultRolUsuarioNuevo;
    private Integer idPuestoSupervisor;
    private Long idUsuarioAdmin;
    private Long idRolDefault;
    private Integer tiempoCapturaKiosco;
}
