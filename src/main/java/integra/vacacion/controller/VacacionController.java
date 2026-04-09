//package integra.vacacion.controller;
//
//import integra.utils.ResponseData;
//import integra.vacacion.domain.model.DashboardSolicitudes;
//import integra.vacacion.domain.model.TipoSolicitud;
//import integra.vacacion.dto.request.SolicitudDescansoRequest;
//import integra.vacacion.dto.response.DashboardGestionSolicitudResponse;
//import integra.vacacion.dto.response.HistorialSolicitud;
//import integra.vacacion.dto.response.GestionSolicitudResponse;
//import integra.vacacion.service.command.SolicitudCommandService;
//import integra.vacacion.service.query.DashboardService;
//import integra.vacacion.service.query.GestionSolicitudQueryService;
//import integra.vacacion.service.query.VacacionHistorialQueryService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("vacaciones")
//@RequiredArgsConstructor
//public class VacacionController {
//
//    private final DashboardService dashboardService;
//    private final SolicitudCommandService vacacionCommandService;
//    private final VacacionHistorialQueryService historialQueryService;
//    private final GestionSolicitudQueryService gestionSolicitudQueryService;
//
//    @GetMapping("dashboard")
//    public ResponseEntity<ResponseData<DashboardSolicitudes>> getDashboard(@RequestParam Integer empleadoId) {
//        return ResponseEntity.ok(ResponseData.of(dashboardService.obtenerDashboard(empleadoId), "Generales"));
//    }
//
//    @PostMapping("solicitud")
//    public ResponseEntity<ResponseData<Void>> createSolicitud(@RequestParam Integer empleadoId, @Valid @RequestBody SolicitudDescansoRequest request) {
//        if (request.tipoSolicitud().equals(TipoSolicitud.VACACION)) {
//            vacacionCommandService.solicitarVacaciones(empleadoId, request);
//        } else if (request.tipoSolicitud().equals(TipoSolicitud.DESCANSO)) {
//            vacacionCommandService.solicitarDescansos(empleadoId, request.diasSeleccionados());
//        }
//        return ResponseEntity.ok(ResponseData.of(null, "Solicitud creada exitosamente"));
//    }
//
//    @PatchMapping("{id}/cancelar")
//    public ResponseEntity<ResponseData<Void>> cancelarSolicitud(@PathVariable Long id, @RequestParam Integer usuarioId) {
//        vacacionCommandService.cancelarSolicitudVacaciones(id, usuarioId);
//        return ResponseEntity.ok(ResponseData.success("Solicitud cancelada exitosamente", null));
//    }
//
//    @DeleteMapping("descansos/{id}")
//    public ResponseEntity<ResponseData<Void>> cancelarDescanso(@PathVariable Long id, @RequestParam Integer usuarioId) {
//        vacacionCommandService.cancelarSolicitudDescansos(id, usuarioId);
//        return ResponseEntity.ok(ResponseData.success("Descansos cancelados exitosamente", null));
//    }
//
//    @GetMapping("solicitudes/{id}/timeline")
//    public ResponseEntity<ResponseData<List<HistorialSolicitud>>> obtenerLineaTiempo(@PathVariable Long id) {
//        return ResponseEntity.ok(ResponseData.of(historialQueryService.obtenerLineaTiempo(id), "Línea del tiempo de la solicitud"));
//    }
//
//    @PatchMapping("{id}/reactivar")
//    public ResponseEntity<ResponseData<Void>> reactivar(@PathVariable Long id, @RequestParam Integer usuarioId) {
//        vacacionCommandService.reactivar(id,usuarioId);
//        return ResponseEntity.ok(ResponseData.of(true, "Solicitud creada exitosamente"));
//    }
//
//    /**
//     * Dashboard de gestión de vacaciones para gestores.
//     * Devuelve todos los empleados con periodo VIGENTE, sus gestores nivel 1 y 2,
//     * y sus solicitudes de tiempo agregadas.
//     *
//     * @return lista de empleados con su información consolidada.
//     */
//    @GetMapping("gestion/dashboard")
//    public ResponseEntity<ResponseData<List<GestionSolicitudResponse>>> obtenerDashboardGestionVigente() {
//        return ResponseEntity.ok(ResponseData.of(gestionSolicitudQueryService.obtenerSolicitudesVigentes(), "Solicitudes vigentes"));
//    }
//    @GetMapping("gestion/dashboard/indicadores")
//    public ResponseEntity<ResponseData<DashboardGestionSolicitudResponse>> obtenerDashboardGestion() {
//        return ResponseEntity.ok(ResponseData.of(gestionSolicitudQueryService.obtenerDashboardGestion(), "Indicadores"));
//    }
//}
