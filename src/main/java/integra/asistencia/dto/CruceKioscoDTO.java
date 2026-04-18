package integra.asistencia.dto;

import java.time.LocalDateTime;

public record CruceKioscoDTO(
        Integer id,
        LocalDateTime fecha,
        Integer empleadoId,
        Integer asistenciaId,
        String pathImg,
        Integer unidadRegistroId,
        Integer unidadEsperadaId,
        String accion
) {}
