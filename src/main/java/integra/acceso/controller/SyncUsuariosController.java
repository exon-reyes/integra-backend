package integra.acceso.controller;

import integra.acceso.dto.SyncUsuariosResponse;
import integra.acceso.service.user.SyncNotificacionService;
import integra.acceso.service.user.SyncUsuariosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class SyncUsuariosController {

    private final SyncUsuariosService syncUsuariosService;
    private final SyncNotificacionService syncNotificacionService;

    @PostMapping("/sync")
    public ResponseEntity<SyncUsuariosResponse> sincronizar() {
        return ResponseEntity.ok(syncUsuariosService.sincronizarUsuarios());
    }

    @PostMapping(value = "/sync/notificar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SyncNotificacionService.ResultadoNotificacion> notificar(@RequestParam("archivo") MultipartFile archivo) {
        return ResponseEntity.ok(syncNotificacionService.enviarCredencialesDesdeExcel(archivo));
    }
}
