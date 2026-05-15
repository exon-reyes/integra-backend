package integra.vacacion.dto.response;

import java.time.LocalDate;

public interface ReportePeriodoVacacionalProjection {
    String getClave();
    String getColaborador();
    String getEstatus();
    String getUnidadAsociada();
    LocalDate getFechaIngreso();
    String getPuesto();
    String getResponsable();
    String getResponsableNivel2();
    Integer getAnioLaboral();
    LocalDate getFechaInicio();
    LocalDate getFechaFin();
    LocalDate getFechaCaducidad();
    Integer getHabilitadas();
    Integer getTomadas();
    Integer getRestantes();
    String getEstatusPeriodo();
    Integer getAnioGestion();
}
