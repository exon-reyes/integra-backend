package integra.vacacion.controller;

import integra.utils.PageResponse;
import integra.utils.ResponseData;
import integra.vacacion.domain.model.TipoSolicitud;
import integra.vacacion.dto.request.FiltroPeriodo;
import integra.vacacion.dto.request.FiltroSolicitud;
import integra.vacacion.dto.request.NuevoEstatusSolicitud;
import integra.vacacion.dto.response.DetalleSolicitudDTO;
import integra.vacacion.dto.response.PeriodoVacacionalResumen;
import integra.vacacion.dto.response.SolicitudesGestionDTO;
import integra.vacacion.service.EliminarDiaSolicitado;
import integra.vacacion.service.EliminarSolicitud;
import integra.vacacion.service.gestion.ActualizarEstatusSolicitud;
import integra.vacacion.service.gestion.DetallesSolicitud;
import integra.vacacion.service.gestion.ObtenerPeriodos;
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
    private final ObtenerPeriodos obtenerPeriodosService;
    private final EliminarDiaSolicitado eliminarDiaSolicitado;
    private final EliminarSolicitud eliminarSolicitud;

    @GetMapping("/periodos")
    public ResponseEntity<PageResponse<PeriodoVacacionalResumen>> obtenerPeriodos(@Valid FiltroPeriodo filtro) {
        return ResponseEntity.ok(new PageResponse<>(obtenerPeriodosService.consultar(filtro)));
    }

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
    @PatchMapping("{id}/cancelar")
    public ResponseEntity<ResponseData<Void>> cancelarSolicitud(@PathVariable Long id, @RequestParam Integer usuarioId, @RequestParam TipoSolicitud tipo) {
        eliminarDiaSolicitado.eliminar(id, usuarioId,tipo);
        return ResponseEntity.ok(ResponseData.success("Solicitud cancelada exitosamente", null));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseData<Void>> eliminarSolicitud(@PathVariable Long id) {
        eliminarSolicitud.eliminarSolicitud(id);
        return ResponseEntity.ok(ResponseData.success("Solicitud eliminada exitosamente", null));
    }
}
