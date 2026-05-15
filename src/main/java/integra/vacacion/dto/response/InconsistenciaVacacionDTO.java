package integra.vacacion.dto.response;

import java.time.LocalDate;

public record InconsistenciaVacacionDTO(
        int numeroFila,
        String codigoEmpleado,
        String estatusEmpleado,
        String unidadEmpleado,
        int diasHabilitadosExcel,
        int diasHabilitadosBD,
        int diasDisponiblesExcel,
        int diasDisponiblesBD,
        LocalDate fechaRegistroExcel,
        LocalDate fechaRegistroBD,
        String mensajeError,
        String detalleCuadre
) {}
