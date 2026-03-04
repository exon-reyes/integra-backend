package integra.empresa.controller;

import integra.acceso.util.Autoridades;
import integra.empresa.request.ActualizarUnidad;
import integra.empresa.request.NuevaUnidad;
import integra.empresa.service.unidad.UnidadCommandService;
import integra.model.Unidad;
import integra.utils.ResponseData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("unidades")
@RequiredArgsConstructor
class UnidadCommandController {
    private final UnidadCommandService commandService;

    @PostMapping("registrar")
    @PreAuthorize(Autoridades.UNIDADES_CREAR)
    public ResponseEntity<ResponseData<Unidad>> registrarUnidad(@RequestBody @Valid NuevaUnidad command) {
        return ResponseEntity.ok(ResponseData.of(commandService.registrarUnidad(command), "Unidad registrada"));
    }

    @PutMapping("actualizar")
    @PreAuthorize(Autoridades.UNIDADES_EDITAR)
    public ResponseEntity<ResponseData<Void>> actualizarUnidad(@RequestBody @Valid ActualizarUnidad command) {
        commandService.actualizarUnidad(command);
        return ResponseEntity.ok(ResponseData.of(null, "Unidad actualizada"));
    }

    @PutMapping("estatus/{id}/{estatus}")
    public ResponseEntity<ResponseData<Void>> deshabilitarUnidad(@PathVariable Integer id, @PathVariable Boolean estatus) {
        commandService.actualizarEstatusUnidad(id, estatus);
        return ResponseEntity.ok(ResponseData.of(null, estatus ? "Unidad habilitada" : "Unidad deshabilitada"));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ResponseData<Void>> eliminarUnidad(@PathVariable Integer id) {
        commandService.eliminarUnidad(id);
        return ResponseEntity.ok(ResponseData.of(null, "Unidad eliminada"));
    }
}