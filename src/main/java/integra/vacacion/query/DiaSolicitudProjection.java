package integra.vacacion.query;

import integra.vacacion.domain.model.EstatusSolicitud;

import java.time.LocalDate;

public interface DiaSolicitudProjection {
    Long getId();
    Long getFolioId();
    LocalDate getFecha();
    EstatusSolicitud getEstatusNivel2();
}
