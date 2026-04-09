package integra.vacacion.controller;

import integra.utils.ResponseData;
import integra.vacacion.domain.model.DashboardSolicitudes;
import integra.vacacion.domain.model.TipoSolicitud;
import integra.vacacion.dto.request.SolicitudDescansoRequest;
import integra.vacacion.service.CrearSolicitudDescanso;
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
    private final CrearSolicitudDescanso solicitudDescanso;

    @GetMapping("dashboard")
    public ResponseEntity<ResponseData<DashboardSolicitudes>> obtenerSolicitudes(@RequestParam Integer empleadoId, @RequestParam int anio) {
        return ResponseEntity.ok(ResponseData.success("Solicitudes obtenidas con exito", obtenerSolicitudes.obtenerSolicitudes(empleadoId, anio)));
    }

    @PostMapping("solicitud")
    public ResponseEntity<ResponseData<Void>> createSolicitud(@Valid @RequestBody SolicitudDescansoRequest request) {
        if (request.tipoSolicitud().equals(TipoSolicitud.VACACION)) {
            vacacionCommandService.crear(request);
        } else if (request.tipoSolicitud().equals(TipoSolicitud.DESCANSO)) {
            solicitudDescanso.crearSolicitud(request);
        }
        return ResponseEntity.ok(ResponseData.of(null, "Solicitud creada exitosamente"));
    }
}
