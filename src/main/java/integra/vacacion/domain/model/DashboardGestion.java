package integra.vacacion.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Proyección de datos para el dashboard de gestión de vacaciones.
 * Representa un empleado con periodo vigente, sus gestores y sus solicitudes.
 */
public record DashboardGestion(
        @JsonProperty("empleado_id")    Integer empleadoId,
        @JsonProperty("nombre_completo") String nombreCompleto,
        @JsonProperty("dias_restantes")  Integer diasRestantes,
        @JsonProperty("gestor_1_nombre") String gestor1Nombre,
        @JsonProperty("gestor_2_nombre") String gestor2Nombre,
        @JsonProperty("solicitudes")     List<SolicitudItem> solicitudes
) {

    /**
     * Representa una solicitud de tiempo (VACACION o DESCANSO) del empleado.
     */
    public record SolicitudItem(
            @JsonProperty("id")           Long   id,
            @JsonProperty("fecha")        String fecha,
            @JsonProperty("tipo")         String tipo,
            @JsonProperty("estatus")      String estatus,
            @JsonProperty("estatus_jefe") String estatusJefe,
            @JsonProperty("estatus_rrhh") String estatusRrhh
    ) {}
}
