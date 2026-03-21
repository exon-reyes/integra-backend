package integra.vacacion.dto.response;

import integra.model.Empleado;

import java.time.LocalDate;
import java.util.List;

public record DashboardVacacionDTO(
        Empleado empleado,
        Integer diasTotalesAnio,
        Integer diasDisponibles,
        Integer diasDisfrutados,
        Integer diasProgramados,
        Integer diasProximosVencer,
        LocalDate fechaProximoVencer,
        List<ProximaVacacionDTO> proximasVacaciones,
        Integer diasAnualesActual,
        LocalDate proximoAniversario,
        Integer anioGestion,
        Integer diasAprobados,
        Integer diasRechazados,
        Integer diasCancelados,
        List<DescansoDTO> descansosRegistrados,
        List<DescansoDTO> descansosPendientes,
        List<ProximaVacacionDTO> aprobadosPorTomar,
        List<ProximaVacacionDTO> disfrutados,
        List<ProximaVacacionDTO> vacacionesPendientes,
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
