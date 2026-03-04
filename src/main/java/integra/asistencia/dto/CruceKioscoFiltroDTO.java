package integra.asistencia.dto;

import java.time.Instant;

public record CruceKioscoFiltroDTO(
        Integer empleadoId,
        Integer asistenciaId,
        Instant fechaInicio,
        Instant fechaFin,
        String accion,
        Integer unidadRegistroId,
        Integer unidadEsperadaId
) {}
