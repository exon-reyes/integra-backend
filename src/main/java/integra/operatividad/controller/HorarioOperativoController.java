package integra.operatividad.controller;

import integra.model.Operatividad;
import integra.operatividad.service.OperatividadService;
import integra.utils.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import integra.operatividad.dto.GuardarHorariosRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("operatividades")
public class HorarioOperativoController {
    private final OperatividadService operatividadService;

    @GetMapping
    public ResponseEntity<ResponseData<List<Operatividad>>> obtenerOperatividades() {
        return ResponseEntity
                .ok(ResponseData.of(operatividadService.obtenerOperatividades(), "Lista de operatividades"));
    }

    @PostMapping("/horarios")
    public ResponseEntity<ResponseData<Void>> guardarHorarios(@Valid @RequestBody GuardarHorariosRequest request) {
        operatividadService.guardarHorarios(request);
        return ResponseEntity.ok(ResponseData.of(null, "Horarios guardados correctamente"));
    }
}
