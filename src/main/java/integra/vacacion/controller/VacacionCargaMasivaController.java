package integra.vacacion.controller;

import integra.vacacion.service.gestion.CargaMasivaVacacionesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@RestController
@RequestMapping("vacaciones")
@RequiredArgsConstructor
public class VacacionCargaMasivaController {

    private final CargaMasivaVacacionesService cargaMasivaService;

    @PostMapping(value = "/carga-masiva", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> cargarMasiva(@RequestParam("archivo") MultipartFile archivo) {
        byte[] reporte = cargaMasivaService.procesar(archivo);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment",
                "inconsistencias_vacaciones_" + LocalDate.now() + ".xlsx");

        return new ResponseEntity<>(reporte, headers, HttpStatus.OK);
    }
}
