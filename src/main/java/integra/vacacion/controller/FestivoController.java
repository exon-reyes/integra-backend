package integra.vacacion.controller;

import integra.utils.ResponseData;
import integra.vacacion.dto.response.Festivo;
import integra.vacacion.service.query.CalendarioFestivoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("vacaciones")
public class FestivoController {
    private final CalendarioFestivoService queryService;

    @GetMapping("/calendario-festivo")
    public ResponseEntity<ResponseData<List<Festivo>>> getCalendarioFestivo(@RequestParam Integer anio) {
        return ResponseEntity.ok(ResponseData.of(queryService.obtenerFestivos(anio, anio), "Festivos oficiales"));
    }
}
