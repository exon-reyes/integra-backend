package integra.acceso.controller;

import integra.acceso.command.ActualizarPermisosRolRequest;
import integra.acceso.command.ActualizarRolRequest;
import integra.acceso.request.NuevoRolRequest;
import integra.acceso.service.rol.RoleService;
import integra.model.Rol;
import integra.utils.ResponseData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping()
    public ResponseEntity<ResponseData<Rol>> agregarRol(@RequestBody NuevoRolRequest command) {
        return ResponseEntity.ok(ResponseData.of(roleService.agregarRol(command), "Rol agregado"));
    }

    @GetMapping
    public ResponseEntity<ResponseData<List<Rol>>> getAll() {
        return ResponseEntity.ok(ResponseData.success("Roles recuperados", roleService.obtenerCatalagoRoles()));
    }

    @GetMapping("{id}/permisos")
    public ResponseEntity<ResponseData<Rol>> obtenerRolConPermisos(@PathVariable Long id) {
        return ResponseEntity.ok(ResponseData.success("Rol con permisos", roleService.obtenerRolPorId(id)));
    }

    @PutMapping("permisos")
    public ResponseEntity<ResponseData<Rol>> actualizarPermisos(@Valid @RequestBody ActualizarPermisosRolRequest command) {
        roleService.actualizarPermisos(command);
        return ResponseEntity.ok(ResponseData.success("Permisos actualizados", null));
    }

    @PatchMapping("actualizar")
    public ResponseEntity<ResponseData<Rol>> actualizarRol(@Valid @RequestBody ActualizarRolRequest rol) {
        roleService.actualizar(rol);
        return ResponseEntity.ok(ResponseData.success("Rol actualizado", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseData<Void>> eliminarRol(@PathVariable Long id) {
        roleService.eliminarRol(id);
        return ResponseEntity.ok(ResponseData.of(true, "Rol eliminado"));
    }
}