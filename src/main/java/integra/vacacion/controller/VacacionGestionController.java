package integra.vacacion.controller;

import integra.utils.PageResponse;
import integra.utils.ResponseData;
import integra.vacacion.dto.request.FiltroSolicitud;
import integra.vacacion.dto.request.NuevoEstatusSolicitud;
import integra.vacacion.dto.response.DetalleSolicitudDTO;
import integra.vacacion.dto.response.SolicitudesGestionDTO;
import integra.vacacion.service.gestion.ActualizarEstatusSolicitud;
import integra.vacacion.service.gestion.DetallesSolicitud;
import integra.vacacion.service.gestion.ObtenerSolicitudes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("vacaciones/gestion")
@RequiredArgsConstructor
@RestController
public class VacacionGestionController {
    private final ActualizarEstatusSolicitud estatusSolicitudService;
    private final DetallesSolicitud detallesSolicitud;
    private final ObtenerSolicitudes obtenerSolicitudesService;

    @GetMapping("/solicitudes")
    public ResponseEntity<PageResponse<SolicitudesGestionDTO>> obtenerSolicitud(@Valid FiltroSolicitud filtro) {
        Page<SolicitudesGestionDTO> pageResult = obtenerSolicitudesService.getAll(filtro);
        return ResponseEntity.ok(new PageResponse<>(pageResult));
    }

    @GetMapping("/solicitudes/{folio}")
    public ResponseEntity<ResponseData<DetalleSolicitudDTO>> obtenerDetallesSolicitud(@PathVariable Long folio) {
        return ResponseEntity.ok(ResponseData.success("Solicitud encontrada exitosamente", detallesSolicitud.getByFolio(folio)));
    }

    @PatchMapping("/solicitudes")
    public ResponseEntity<ResponseData<Void>> actualizarEstatusSolicitud(@RequestBody NuevoEstatusSolicitud dictamen) {
        estatusSolicitudService.actualizarGlobal(dictamen);
        return ResponseEntity.ok(ResponseData.success("Estatus actualizado correctamente", null));
    }

    @PatchMapping("/solicitudes/dias")
    public ResponseEntity<ResponseData<Void>> actualizarEstatusDiasGranular(@RequestBody NuevoEstatusSolicitud dictamen) {
        estatusSolicitudService.actualizarDiasGranular(dictamen);
        return ResponseEntity.ok(ResponseData.success("Estatus de los días actualizados correctamente", null));
    }
}
