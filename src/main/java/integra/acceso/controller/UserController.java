package integra.acceso.controller;

import integra.acceso.command.ActualizarUsuarioCommand;
import integra.acceso.entity.User;
import integra.acceso.projection.UsuarioBasicoDTO;
import integra.acceso.repository.UserRepository;
import integra.acceso.request.ActualizarPermisosRequest;
import integra.acceso.request.CreateUserRequest;
import integra.acceso.service.user.UserService;
import integra.utils.PageResponse;
import integra.utils.ResponseData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("usuarios")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<PageResponse<UsuarioBasicoDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) Integer empleadoId) {

        Page<UsuarioBasicoDTO> result = userService.obtenerUsuarios(empleadoId, PageRequest.of(page, size));
        return ResponseEntity.ok(new PageResponse<>(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<User>> getById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok(ResponseData.success("Usuario encontrado", user)))
                .orElse(ResponseEntity.status(404).body(ResponseData.error("Usuario no encontrado")));
    }

    @GetMapping("/{id}/permisos")
    public ResponseEntity<ResponseData<?>> obtenerPermisos(@PathVariable Long id) {
        return ResponseEntity.ok(ResponseData.of(userService.obtenerPermisos(id), "Permisos obtenidos"));
    }

    @PostMapping
    public ResponseEntity<ResponseData<?>> crearUsuario(@Valid @RequestBody CreateUserRequest user) {
        userService.crearUsuario(user);
        return ResponseEntity.ok(ResponseData.of(true, "Usuario creado exitosamente"));
    }

    @PatchMapping("/permisos/actualizar")
    public ResponseEntity<ResponseData<?>> actualizarPermisos(@Valid @RequestBody ActualizarPermisosRequest value) {
        userService.ActualizarPermisos(value);
        return ResponseEntity.ok(ResponseData.of("OK", "Permisos actualizados"));
    }

    @PatchMapping("/{id}/estatus")
    public ResponseEntity<ResponseData<String>> actualizarEstatus(@PathVariable Long id, @RequestParam boolean activo) {
        userService.actualizarEstatus(id, activo);
        return ResponseEntity.ok(ResponseData.of("OK", "Estatus actualizado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseData<Void>> eliminarUsuario(@PathVariable Long id) {
        userService.eliminarUsuario(id);
        return ResponseEntity.ok(ResponseData.of(null, "Usuario eliminado"));
    }

    @PatchMapping("usuario")
    public ResponseEntity<ResponseData<?>> actualizarUsuario(@Valid @RequestBody ActualizarUsuarioCommand request) {
        userService.actualizarUsuario(request);
        return ResponseEntity.ok(ResponseData.of(true, "Usuario actualizado exitosamente"));
    }

}