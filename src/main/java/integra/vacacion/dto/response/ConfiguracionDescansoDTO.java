package integra.vacacion.dto.response;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public record ConfiguracionDescansoDTO(
        Integer empleadoId,
        Set<LocalDate> diasDescanso,
        Set<LocalDate> diasDescansosPendientes,
        List<DescansoPendienteDTO> diasDescansosPendientesConId,
        boolean configurado
) {
    public ConfiguracionDescansoDTO(Integer empleadoId, Set<LocalDate> diasDescanso, Set<LocalDate> diasDescansosPendientes, boolean configurado) {
        this(empleadoId, diasDescanso, diasDescansosPendientes, null, configurado);
    }

    public record DescansoPendienteDTO(Long id, LocalDate fecha) {
    }
}
