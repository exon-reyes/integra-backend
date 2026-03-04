package integra.asistencia.controller;

import integra.asistencia.dto.CruceKioscoFiltroDTO;
import integra.asistencia.service.kiosco.CruceKioscoService;
import integra.utils.ResponseData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("cruce-kiosco")
@RequiredArgsConstructor
@RestController
public class CruceKioscoController {
    private final CruceKioscoService service;

    @GetMapping()
    public ResponseEntity<ResponseData<?>> obtenerCruceUnidades(@Valid CruceKioscoFiltroDTO filtro) {
        var cruces = service.buscarPorFiltros(filtro);
        return ResponseEntity.ok(ResponseData.of(cruces, "Cruce de empleados en unidades de registro"));
    }
}
