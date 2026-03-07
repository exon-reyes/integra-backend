package integra.vacacion.controller;

import integra.vacacion.dto.request.AprobacionRequest;
import integra.vacacion.dto.response.SolicitudVacacionDTO;
import integra.vacacion.service.command.VacacionCommandService;
import integra.vacacion.service.query.VacacionQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("vacaciones/aprobacion")
@RequiredArgsConstructor
public class AprobacionController {

    private final VacacionQueryService queryService;
    private final VacacionCommandService commandService;

    @GetMapping("/pendientes")
    public ResponseEntity<List<SolicitudVacacionDTO>> getSolicitudesPendientes(
            @RequestParam Integer aprobadorId) {
        return ResponseEntity.ok(queryService.obtenerSolicitudesPendientesAprobador(aprobadorId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitudVacacionDTO> getDetalle(@PathVariable Long id) {
        return ResponseEntity.ok(queryService.obtenerSolicitud(id));
    }

    @PostMapping("/{id}/aprobar")
    public ResponseEntity<SolicitudVacacionDTO> aprobar(
            @PathVariable Long id,
            @RequestParam Integer aprobadorId) {
        return ResponseEntity.ok(commandService.aprobarSolicitud(id, aprobadorId));
    }

    @PostMapping("/{id}/rechazar")
    public ResponseEntity<SolicitudVacacionDTO> rechazar(
            @PathVariable Long id,
            @RequestParam Integer aprobadorId,
            @Valid @RequestBody AprobacionRequest request) {
        return ResponseEntity.ok(commandService.rechazarSolicitud(id, aprobadorId, request.comentarios()));
    }
}
