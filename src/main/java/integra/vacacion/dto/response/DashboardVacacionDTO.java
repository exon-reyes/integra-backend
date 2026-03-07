package integra.vacacion.dto.response;

import java.time.LocalDate;
import java.util.List;

public record DashboardVacacionDTO(
        Integer empleadoId,
        String nombreCompleto,
        Integer antiguedadAnios,
        Integer diasTotalesAnio,
        Integer diasDisponibles,
        Integer diasDisfrutados,
        Integer diasProgramados,
        Integer diasProximosVencer,
        LocalDate fechaProximoVencer,
        List<ProximaVacacionDTO> proximasVacaciones,
        Integer diasAnualesActual,
        LocalDate proximoAniversario,
        String departamento,
        String puesto,
        String unidad,
        Integer anioGestion,
        Integer diasAprobados,
        Integer diasRechazados,
        Integer diasCancelados,
        List<DescansoDTO> descansosRegistrados,
        List<ProximaVacacionDTO> aprobadosPorTomar,
        List<ProximaVacacionDTO> disfrutados,
        List<ProximaVacacionDTO> pendientes,
        List<ProximaVacacionDTO> rechazadas,
        List<ProximaVacacionDTO> canceladas
) {
    public record ProximaVacacionDTO(
            Long solicitudId,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            List<LocalDate> fechas,
            Integer diasLaborables,
            String estado
    ) {
    }

    public record DescansoDTO(
            LocalDate fecha,
            String motivo
    ) {
    }
}
