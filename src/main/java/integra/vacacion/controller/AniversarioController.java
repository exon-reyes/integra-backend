package integra.vacacion.controller;

import integra.utils.ResponseData;
import integra.vacacion.dto.request.FiltroAniversario;
import integra.vacacion.dto.response.EmpleadoAniversarioDTO;
import integra.vacacion.service.AniversarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("vacaciones/aniversarios")
public class AniversarioController {

    private final AniversarioService aniversarioService;

    @GetMapping
    public ResponseEntity<ResponseData<List<EmpleadoAniversarioDTO>>> obtenerPorMes(@Valid FiltroAniversario filtro) {
        if (filtro.getMes() < 1 || filtro.getMes() > 12) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(ResponseData.of(aniversarioService.obtenerAniversariosDelMes(filtro), "Proximos aniversarios"));
    }
}
