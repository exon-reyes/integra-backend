package integra.vacacion.controller;

import integra.utils.ResponseData;
import integra.vacacion.service.PeriodoEstatusChangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("periodo-estatus-changed")
public class PeriodoEstatusChanged {
    private final PeriodoEstatusChangeService periodoEstatusChangeService;
    @PatchMapping("/update/{id}")
    public ResponseEntity<ResponseData<Void>> update(@PathVariable Long id) {
        periodoEstatusChangeService.update(id);
        return ResponseEntity.ok(ResponseData.of(null, "Periodo estatus changed"));
    }
}
