package integra.operatividad.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record GuardarHorariosRequest(
        @NotNull Integer idUnidad,
        @NotEmpty @Valid List<HorarioOperativoDto> horarios) {
}
