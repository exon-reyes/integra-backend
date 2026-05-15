package integra.vacacion.dto.response;

import java.util.List;

public record SincronizacionPeriodoResponse(
        int totalProcesados,
        int exitosos,
        int fallidos,
        int omitidos,
        List<PeriodoGeneradoInfo> periodosNuevos,
        List<PeriodoVencidoInfo> periodosVencidos,
        List<PeriodoCerradoInfo> periodosCerrados
) {
}
