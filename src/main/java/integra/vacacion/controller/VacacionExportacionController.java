package integra.vacacion.controller;

import integra.vacacion.dto.request.FiltroSolicitud;
import integra.vacacion.service.gestion.GenerarPapeletaExcelService;
import integra.vacacion.service.gestion.GenerarReporteSolicitudesConsolidadoExcelService;
import integra.vacacion.service.gestion.GenerarReporteSolicitudesExcelService;
import integra.vacacion.service.gestion.GenerarReporteSolicitudesLineaExcelService;
import integra.vacacion.service.gestion.GenerarReporteVacacionesExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("vacaciones/exportar")
@RequiredArgsConstructor
public class VacacionExportacionController {

    private final GenerarReporteVacacionesExcelService generarReporteVacacionesExcelService;
    private final GenerarPapeletaExcelService generarPapeletaExcelService;
    private final GenerarReporteSolicitudesExcelService generarReporteSolicitudesExcelService;
    private final GenerarReporteSolicitudesLineaExcelService generarReporteSolicitudesLineaExcelService;
    private final GenerarReporteSolicitudesConsolidadoExcelService generarReporteSolicitudesConsolidadoExcelService;

    @GetMapping
    public ResponseEntity<byte[]> exportarValoresActuales() {
        byte[] excelContent = generarReporteVacacionesExcelService.generar();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "Reporte_Vacaciones_" + LocalDate.now() + ".xlsx");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(excelContent, headers, HttpStatus.OK);
    }

    @GetMapping("/solicitudes/{folio}/papeleta")
    public ResponseEntity<byte[]> generarPapeleta(@PathVariable Long folio, @RequestParam Double salarioDiario, @RequestParam Integer diasAdicionales) {

        byte[] excelContent = generarPapeletaExcelService.generar(folio, salarioDiario, diasAdicionales);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "Papeleta_" + folio + ".xlsx");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(excelContent, headers, HttpStatus.OK);
    }

    @GetMapping("/solicitudes")
    public ResponseEntity<byte[]> exportarSolicitudes(FiltroSolicitud filtro) {
        byte[] excelContent = generarReporteSolicitudesConsolidadoExcelService.generar(filtro);

        String nombre = "Solicitudes_" + LocalDate.now() + ".xlsx";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", nombre);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(excelContent, headers, HttpStatus.OK);
    }

//    @GetMapping("/solicitudes/linea")
//    public ResponseEntity<byte[]> exportarSolicitudesLinea(FiltroSolicitud filtro) {
//        byte[] excelContent = generarReporteSolicitudesLineaExcelService.generar(filtro);
//
//        String nombre = "Solicitudes_Linea_" + LocalDate.now() + ".xlsx";
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
//        headers.setContentDispositionFormData("attachment", nombre);
//        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
//
//        return new ResponseEntity<>(excelContent, headers, HttpStatus.OK);
//    }
}

