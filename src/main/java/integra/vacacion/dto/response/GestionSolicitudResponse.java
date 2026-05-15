package integra.vacacion.dto.response;

import java.util.List;

public record GestionSolicitudResponse(
        Integer empleadoId,
        String nombreCompleto,
        String unidad,
        String primerJefe,
        String segundoJefe,
        List<SolicitudAgrupada> solicitudes,
        int totalSolicitudes,
        int diasHabilitados,
        int diasDisponibles,
        int diasTomados
) {

    public record SolicitudAgrupada(
            Long folio,
            String tipo,
            String estatus,
            List<DiaSolicitado> dias,
            Indicadores indicadores
    ) {}

    public record Indicadores(
            int totalDias,
            int aprobados,
            int pendientes,
            int cancelados,
            int disfrutados
    ) {}

    public record DiaSolicitado(
            Long id,
            String fecha,
            String estatus,
            Responsable primerResponsable,
            Responsable segundoResponsable
    ) {}

    public record Responsable(
            String nombre,
            String estatus
    ) {}
}
