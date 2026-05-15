//package integra.vacacion.controller;
//
//import integra.vacacion.service.command.PeriodoVacacionalSyncService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/v1/vacaciones/admin")
//@RequiredArgsConstructor
//public class VacacionAdminController {
//
//    private final PeriodoVacacionalSyncService syncService;
//
//    @PostMapping("/sincronizar-periodos")
//    public ResponseEntity<Map<String, String>> sincronizarPeriodos() {
//        syncService.generarPeriodosAutomaticos();
//        return ResponseEntity.ok(Map.of("mensaje", "Sincronización completada"));
//    }
//
//    @PostMapping("/sincronizar-empleado/{empleadoId}")
//    public ResponseEntity<Map<String, String>> sincronizarEmpleado(@PathVariable Integer empleadoId) {
//        syncService.sincronizarEmpleado(empleadoId);
//        return ResponseEntity.ok(Map.of("mensaje", "Empleado sincronizado"));
//    }
//
//    @PostMapping("/regenerar-periodos/{empleadoId}")
//    public ResponseEntity<Map<String, String>> regenerarPeriodos(@PathVariable Integer empleadoId) {
//        syncService.regenerarPeriodosEmpleado(empleadoId);
//        return ResponseEntity.ok(Map.of(
//                "mensaje", "Períodos regenerados con política actualizada",
//                "empleadoId", empleadoId.toString()
//        ));
//    }
//}
