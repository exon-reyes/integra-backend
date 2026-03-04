package integra.asistencia.controller;

import integra.asistencia.actions.EmpleadoReporteCommand;
import integra.asistencia.actions.EmpleadoReporteRequest;
import integra.asistencia.actions.EmpleadosSinAsistenciaCommand;
import integra.asistencia.actions.EmpleadosSinAsistenciaRequest;
import integra.asistencia.actions.FiltroIncidencia;
import integra.asistencia.actions.InasistenciaPorFechaResponse;
import integra.asistencia.actions.ResumenMesAsistencia;
import integra.asistencia.entity.Incidencia;
import integra.asistencia.facade.ReporteFacade;
import integra.asistencia.factory.EmpleadosSinAsistenciaFactory;
import integra.asistencia.model.EmpleadoReporte;
import integra.asistencia.service.ObtenerEmpleadosSinAsistenciaService;
import integra.asistencia.service.WorkImageService;
import integra.utils.ResponseData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static integra.asistencia.factory.EmpleadoFactory.mapRequestToCommand;

/**
 * Controlador REST para la gestión de reportes de asistencia de empleados.
 * Proporciona endpoints para obtener reportes consolidados en formato JSON y
 * reportes detallados en formato Excel.
 */
@RestController
@RequestMapping("asistencia/reporte")
@RequiredArgsConstructor
@Slf4j
public class ReporteQueryController {

    private final ReporteFacade reporteFacade;
    private final WorkImageService imageService;
    private final ObtenerEmpleadosSinAsistenciaService obtenerEmpleadosSinAsistenciaService;

    /**
     * Obtiene un reporte consolidado de asistencias de empleados en formato JSON.
     *
     * @param request Objeto con los parámetros de filtrado para generar el reporte
     * @return ResponseEntity con los datos del reporte de asistencias de empleados
     */
    @GetMapping("/asistencias")
    public ResponseEntity<ResponseData<List<EmpleadoReporte>>> obtenerReporteJson(
            @Valid EmpleadoReporteRequest request) {
        EmpleadoReporteCommand command = mapRequestToCommand(request);
        return ResponseEntity.ok(ResponseData.of(reporteFacade.obtenerAsistencia(command), "Asistencia por empleados"));
    }

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) throws IOException {
        log.info("Consultando la imagen {}", filename);
        Resource image = imageService.getResizedImg(filename, 300, 300);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + image.getFilename() + "\"")
                .body(image);
    }

    /**
     * Genera y descarga un reporte detallado de asistencias con pausas anidadas en
     * formato Excel.
     *
     * @param request Objeto con los parámetros de filtrado para generar el reporte
     * @return ResponseEntity con el archivo Excel del reporte detallado de
     *         asistencias
     */
    @GetMapping("/asistencias/detallado/excel")
    public ResponseEntity<byte[]> obtenerReporteDetalladoExcel(@Valid EmpleadoReporteRequest request) {
        EmpleadoReporteCommand command = mapRequestToCommand(request);
        byte[] excelBytes = reporteFacade.obtenerReporteAsistenciaExcel(command);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(
                MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "reporte-asistencias-detallado.xlsx");

        return ResponseEntity.ok().headers(headers).body(excelBytes);
    }

    @GetMapping("/inconsistencias")
    public ResponseEntity<ResponseData<List<Incidencia>>> obtenerInconsistenciasAsistencia(FiltroIncidencia filtro) {
        return ResponseEntity.ok(ResponseData.of(reporteFacade.obtenerInconsistenciasAsistencia(filtro.getFechaInicio(),
                filtro.getFechaFin(), filtro.getEmpleadoId()), "Inconsistencias de asistencia"));
    }

    /**
     * Obtiene el resumen de días laborados y no laborados del empleado en el mes
     * indicado (por defecto mes en curso).
     *
     * @param empleadoId id del empleado (requerido)
     * @param anio       año (opcional, por defecto actual)
     * @param mes        mes 1-12 (opcional, por defecto actual)
     */
    @GetMapping("/resumen-mes")
    public ResponseEntity<ResponseData<ResumenMesAsistencia>> obtenerResumenMes(
            @RequestParam Integer empleadoId,
            @RequestParam(required = false) Integer anio,
            @RequestParam(required = false) Integer mes) {
        LocalDate hoy = LocalDate.now();
        int year = anio != null ? anio : hoy.getYear();
        int month = mes != null ? mes : hoy.getMonthValue();
        ResumenMesAsistencia resumen = reporteFacade.obtenerResumenMes(empleadoId, year, month);
        return ResponseEntity.ok(ResponseData.of(resumen, "Resumen de asistencia del mes"));
    }

    /**
     * Obtiene la lista de fechas (días) en que el empleado tiene registro de
     * asistencia en el mes indicado.
     *
     * @param empleadoId id del empleado (requerido)
     * @param anio       año (requerido, ej. 2026)
     * @param mes        mes 1-12 (requerido, ej. 1 = enero)
     * @return lista de fechas en formato "yyyy-MM-dd" (días laborados)
     */
    @GetMapping("/dias-laborados")
    public ResponseEntity<ResponseData<List<LocalDate>>> obtenerDiasLaboradosEnMes(
            @RequestParam Integer empleadoId,
            @RequestParam Integer anio,
            @RequestParam Integer mes) {
        List<LocalDate> dias = reporteFacade.obtenerDiasLaboradosEnMes(empleadoId, anio, mes);
        return ResponseEntity.ok(ResponseData.of(dias, "Días laborados en el mes"));
    }

    /**
     * Obtiene la lista de empleados que NO tienen registros de asistencia en el
     * rango de fechas especificado, agrupados por fecha.
     * Aplica filtros opcionales por unidad, puesto, zona y supervisor.
     *
     * @param request Objeto con los parámetros de filtrado (fechas requeridas,
     *                filtros opcionales)
     * @return ResponseEntity con la lista de inasistencias agrupadas por fecha
     */
    @GetMapping("/empleados-sin-asistencia")
    public ResponseEntity<ResponseData<List<InasistenciaPorFechaResponse>>> obtenerEmpleadosSinAsistencia(
            @Valid EmpleadosSinAsistenciaRequest request) {
        EmpleadosSinAsistenciaCommand command = EmpleadosSinAsistenciaFactory.mapRequestToCommand(request);
        List<InasistenciaPorFechaResponse> inasistencias = obtenerEmpleadosSinAsistenciaService.execute(command);
        return ResponseEntity.ok(ResponseData.of(inasistencias, "Inasistencias agrupadas por fecha"));
    }
}