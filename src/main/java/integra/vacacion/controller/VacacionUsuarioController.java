package integra.vacacion.controller;

import integra.utils.ResponseData;
import integra.vacacion.domain.model.DashboardSolicitudes;
import integra.vacacion.domain.model.TipoSolicitud;
import integra.vacacion.dto.request.SolicitudVacacionRequest;
import integra.vacacion.service.CrearSolicitudVacacion;
import integra.vacacion.service.solicitud.ObtenerSolicitudesUsuario;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("vacaciones")
@RestController
public class VacacionUsuarioController {
    private final ObtenerSolicitudesUsuario obtenerSolicitudes;
    private final CrearSolicitudVacacion vacacionCommandService;

    @GetMapping("dashboard")
    public ResponseEntity<ResponseData<DashboardSolicitudes>> obtenerSolicitudes(
            @RequestParam Integer empleadoId,
            @RequestParam int anio) {
        return ResponseEntity.ok(ResponseData.success("Solicitudes obtenidas con exito", obtenerSolicitudes.obtenerSolicitudes(empleadoId, anio)));
    }

    @PostMapping("solicitud")
    public ResponseEntity<ResponseData<Void>> createSolicitud(@RequestParam Integer empleadoId, @Valid @RequestBody SolicitudVacacionRequest request) {
        if (request.tipoSolicitud().equals(TipoSolicitud.VACACION)) {
            vacacionCommandService.crear(request);
//                    solicitarVacaciones(empleadoId, request);
        } else if (request.tipoSolicitud().equals(TipoSolicitud.DESCANSO)) {
//            vacacionCommandService.solicitarDescansos(empleadoId, request.diasSeleccionados());
        }
        return ResponseEntity.ok(ResponseData.of(null, "Solicitud creada exitosamente"));
    }
}
