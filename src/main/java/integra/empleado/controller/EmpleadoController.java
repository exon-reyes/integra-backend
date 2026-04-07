package integra.empleado.controller;

import integra.empleado.dto.ActualizarAvatarRequest;
import integra.empleado.service.ActualizarAvatarEmpleado;
import integra.empleado.service.ConsultarCatalogoEmpleados;
import integra.empleado.util.FiltroEmpleado;
import integra.model.Empleado;
import integra.utils.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("empleados")
class EmpleadoController {
    private final ConsultarCatalogoEmpleados catalagoEmpleados;
    private final ActualizarAvatarEmpleado actualizarAvatarEmpleado;

    @GetMapping()
    public ResponseEntity<ResponseData<List<Empleado>>> obtenerEmpleados(FiltroEmpleado filtro) {
        return ResponseEntity.ok(ResponseData.of(catalagoEmpleados.consultar(filtro), "Empleado"));
    }

    @GetMapping("supervisores")
    public ResponseEntity<ResponseData<List<Empleado>>> obtenerSupervisores(
            @RequestParam(required = false) Boolean activos) {
        return ResponseEntity
                .ok(ResponseData.of(catalagoEmpleados.obtenerSupervisores(activos), "Supervisores activos"));
    }

    @GetMapping("{id}/detalles")
    public ResponseEntity<ResponseData<Empleado>> obtenerDetallesEmpleado(@PathVariable Integer id) {
        return ResponseEntity.ok(ResponseData.of(catalagoEmpleados.obtenerDetalles(id), "Detalles de empleado"));
    }

    @PutMapping("{id}/avatar")
    public ResponseEntity<ResponseData<Void>> actualizarAvatar(@PathVariable Integer id,
                                                               @RequestBody ActualizarAvatarRequest request) {
        actualizarAvatarEmpleado.actualizarAvatar(id, request.avatarName(), request.base64Image());
        return ResponseEntity.ok(ResponseData.of(null, "Avatar actualizado correctamente"));
    }

    @DeleteMapping("{id}/avatar")
    public ResponseEntity<ResponseData<Void>> eliminarAvatar(@PathVariable Integer id) {
        actualizarAvatarEmpleado.eliminarAvatar(id);
        return ResponseEntity.ok(ResponseData.of(null, "Avatar eliminado correctamente"));
    }

    @GetMapping(value = "{id}/avatar/imagen")
    public ResponseEntity<Resource> obtenerImagenAvatar(@PathVariable Integer id) {
        Resource resource = actualizarAvatarEmpleado.obtenerAvatarEnBytes(id);

        if (resource == null || !resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        String mimeType = "image/jpeg";
        if (resource.getFilename() != null && resource.getFilename().toLowerCase().endsWith(".png")) {
            mimeType = "image/png";
        } else if (resource.getFilename() != null && resource.getFilename().toLowerCase().endsWith(".webp")) {
            mimeType = "image/webp";
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .contentType(MediaType.parseMediaType(mimeType))
                .body(resource);
    }
}
