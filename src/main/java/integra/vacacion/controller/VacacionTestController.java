package integra.vacacion.controller;

import integra.vacacion.dto.response.DashboardVacacionDTO;
import integra.vacacion.dto.response.SolicitudVacacionDTO;
import integra.vacacion.service.query.VacacionQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("vacaciones/test")
@RequiredArgsConstructor
public class VacacionTestController {

    private final VacacionQueryService queryService;

    @GetMapping("/dashboard/{empleadoId}")
    public ResponseEntity<DashboardVacacionDTO> testDashboard(@PathVariable Integer empleadoId) {
        return ResponseEntity.ok(queryService.obtenerDashboard(empleadoId));
    }

    @GetMapping("/historial/{empleadoId}")
    public ResponseEntity<List<SolicitudVacacionDTO>> testHistorial(@PathVariable Integer empleadoId) {
        return ResponseEntity.ok(queryService.obtenerHistorial(empleadoId));
    }

    @GetMapping("/ping")
    public ResponseEntity<Map<String, String>> ping() {
        return ResponseEntity.ok(Map.of("status", "OK", "module", "vacaciones"));
    }
}
