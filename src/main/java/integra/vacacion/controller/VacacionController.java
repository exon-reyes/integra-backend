package integra.vacacion.controller;

import integra.utils.ResponseData;
import integra.vacacion.dto.request.SolicitudVacacionRequest;
import integra.vacacion.dto.response.*;
import integra.vacacion.service.command.VacacionCommandService;
import integra.vacacion.service.query.VacacionQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("vacaciones")
@RequiredArgsConstructor
public class VacacionController {

    private final VacacionQueryService queryService;
    private final VacacionCommandService commandService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardVacacionDTO> getDashboard(@RequestParam Integer empleadoId) {
        return ResponseEntity.ok(queryService.obtenerDashboard(empleadoId));
    }

    @GetMapping("/disponibles")
    public ResponseEntity<Integer> getDiasDisponibles(@RequestParam Integer empleadoId) {
        return ResponseEntity.ok(queryService.obtenerDashboard(empleadoId).diasDisponibles());
    }

    @PostMapping("/solicitudes")
    public ResponseEntity<SolicitudVacacionDTO> crearSolicitud(@RequestParam Integer empleadoId, @Valid @RequestBody SolicitudVacacionRequest request) {
        return ResponseEntity.ok(commandService.crearSolicitud(empleadoId, request));
    }

    @GetMapping("/solicitudes")
    public ResponseEntity<List<SolicitudVacacionDTO>> getHistorial(@RequestParam Integer empleadoId) {
        return ResponseEntity.ok(queryService.obtenerHistorial(empleadoId));
    }

    @GetMapping("/solicitudes/{id}")
    public ResponseEntity<SolicitudVacacionDTO> getSolicitud(@PathVariable Long id) {
        return ResponseEntity.ok(queryService.obtenerSolicitud(id));
    }

    @DeleteMapping("/solicitudes/{id}")
    public ResponseEntity<Void> cancelarSolicitud(@PathVariable Long id, @RequestParam Integer empleadoId) {
        commandService.cancelarSolicitud(id, empleadoId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/calendario-equipo")
    public ResponseEntity<List<CalendarioEquipoDTO>> getCalendarioEquipo(@RequestParam Integer empleadoId) {
        return ResponseEntity.ok(queryService.obtenerCalendarioEquipo(empleadoId));
    }

    @PostMapping("/calcular-dias")
    public ResponseEntity<CalculoDiasDTO> calcularDias(@RequestParam Integer empleadoId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ResponseEntity.ok(commandService.preCalcular(empleadoId, inicio, fin));
    }

    @GetMapping("/calendario-festivo")
    public ResponseEntity<ResponseData<List<FestivoDTO>>> getCalendarioFestivo(@RequestParam Integer anio) {
        return ResponseEntity.ok(ResponseData.of(queryService.obtenerCalendarioFestivo(anio), "Festivos oficiales"));
    }
}
