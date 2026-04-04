package integra.vacacion.controller;

import integra.utils.PageResponse;
import integra.utils.ResponseData;
import integra.vacacion.dto.request.FiltroSolicitud;
import integra.vacacion.dto.request.NuevoEstatusSolicitud;
import integra.vacacion.dto.response.DetalleSolicitudDTO;
import integra.vacacion.dto.response.SolicitudesGestionDTO;
import integra.vacacion.service.command.GestionSolicitudesCommandService;
import integra.vacacion.service.command.GestiónSolicitudGranularService;
import integra.vacacion.service.query.GestionSolicitudesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("vacaciones/gestion")
@RequiredArgsConstructor
@RestController
public class VacacionGestionController {
    private final GestionSolicitudesService solicitudService;
    private final GestionSolicitudesCommandService solicitudesCommandService;
    private final GestiónSolicitudGranularService solicitudGranularService;

    @GetMapping("/solicitudes")
    public ResponseEntity<PageResponse<SolicitudesGestionDTO>> obtenerSolicitud(@Valid FiltroSolicitud filtro) {
        Page<SolicitudesGestionDTO> pageResult = solicitudService.getSolicitudesVigentes(filtro);
        return ResponseEntity.ok(new PageResponse<>(pageResult));
    }

    @GetMapping("/solicitudes/{folio}")
    public ResponseEntity<ResponseData<DetalleSolicitudDTO>> obtenerDetallesSolicitud(@PathVariable Long folio) {
        return ResponseEntity.ok(ResponseData.success("Solicitud encontrada exitosamente", solicitudService.obtenerDetalles(folio)));
    }

    @PatchMapping("/solicitudes")
    public ResponseEntity<ResponseData<Void>> actualizarEstatusSolicitud(@RequestBody NuevoEstatusSolicitud dictamen) {
        solicitudesCommandService.actualizarBloqueSolicitudes(dictamen);
        return ResponseEntity.ok(ResponseData.success("Estatus actualizado correctamente", null));
    }

    @PatchMapping("/solicitudes/{id}")
    public ResponseEntity<ResponseData<Void>> actualizarEstatusSolicitudGranular(
            @PathVariable Long id,
            @RequestBody NuevoEstatusSolicitud dictamen) {
        dictamen.setIdSolicitud(id);
        solicitudGranularService.actualizarEstatusSolicitud(dictamen);
        return ResponseEntity.ok(ResponseData.success("Estatus actualizado correctamente", null));
    }
}
