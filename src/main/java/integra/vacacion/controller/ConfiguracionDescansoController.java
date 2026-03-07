package integra.vacacion.controller;

import integra.vacacion.dto.request.ConfiguracionDescansoRequest;
import integra.vacacion.dto.response.ConfiguracionDescansoDTO;
import integra.vacacion.service.command.ConfiguracionDescansoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("vacaciones/descansos")
@RequiredArgsConstructor
public class ConfiguracionDescansoController {

    private final ConfiguracionDescansoService descansoService;

    @GetMapping
    public ResponseEntity<ConfiguracionDescansoDTO> obtener(@RequestParam Integer empleadoId) {
        return ResponseEntity.ok(descansoService.obtener(empleadoId));
    }

    @PostMapping
    public ResponseEntity<ConfiguracionDescansoDTO> configurar(@RequestParam Integer empleadoId, @Valid @RequestBody ConfiguracionDescansoRequest request) {
        return ResponseEntity.ok(descansoService.configurar(empleadoId, request));
    }
}
