package integra.vacacion.service.gestion;

import integra.vacacion.dto.response.PeriodoCerradoInfo;
import integra.vacacion.dto.response.PeriodoGeneradoInfo;
import integra.vacacion.dto.response.PeriodoVencidoInfo;
import integra.vacacion.dto.response.SincronizacionPeriodoResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class SincronizacionReporteExcelService {

    private static final String DIR = "./integra-config/reportes-sincronizacion/";
    private static final DateTimeFormatter FMT_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FMT_CELDA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void generarReportes(SincronizacionPeriodoResponse resultado, LocalDateTime fechaSincronizacion, String identificador) {
        new File(DIR).mkdirs();

        String nombreArchivo = fechaSincronizacion.format(FMT_FECHA) + "_" + identificador + "_reporte-sincronizacion.xlsx";

        try (Workbook wb = new XSSFWorkbook()) {
            generarNuevosPeriodos(wb, resultado);
            generarPeriodosVencidos(wb, resultado);
            generarPeriodosCerrados(wb, resultado);

            guardar(wb, nombreArchivo);
        } catch (Exception e) {
            log.error("[SincronizacionReporte] Error al generar reporte maestro de Excel: {}", e.getMessage());
        }
    }

    private void generarNuevosPeriodos(Workbook wb, SincronizacionPeriodoResponse r) {
        try {
            Sheet sheet = wb.createSheet("Nuevos Periodos");

            Row header = sheet.createRow(0);
            String[] cols = {"Empleado ID", "Nombre", "Fecha Ingreso", "Años Antigüedad",
                    "Días Habilitados", "Fecha Inicio", "Fecha Fin", "Fecha Caducidad"};
            writeHeader(header, cols);

            int idx = 1;
            for (PeriodoGeneradoInfo p : r.periodosNuevos()) {
                Row row = sheet.createRow(idx++);
                row.createCell(0).setCellValue(p.empleadoId());
                row.createCell(1).setCellValue(p.nombreEmpleado());
                setDateCell(row, 2, p.fechaIngreso() != null ? p.fechaIngreso().format(FMT_CELDA) : "");
                row.createCell(3).setCellValue(p.aniosAntiguedad());
                row.createCell(4).setCellValue(p.diasHabilitados());
                setDateCell(row, 5, p.fechaInicio() != null ? p.fechaInicio().format(FMT_CELDA) : "");
                setDateCell(row, 6, p.fechaFin() != null ? p.fechaFin().format(FMT_CELDA) : "");
                setDateCell(row, 7, p.fechaCaducidad() != null ? p.fechaCaducidad().format(FMT_CELDA) : "");
            }
        } catch (Exception e) {
            log.error("[SincronizacionReporte] Error al poblar hoja nuevos periodos: {}", e.getMessage());
        }
    }

    private void generarPeriodosVencidos(Workbook wb, SincronizacionPeriodoResponse r) {
        try {
            Sheet sheet = wb.createSheet("Periodos Vencidos");

            Row header = sheet.createRow(0);
            writeHeader(header, new String[]{"Empleado ID", "Nombre", "Fecha Inicio", "Fecha Fin", "Días Restantes Perdidos"});

            int idx = 1;
            for (PeriodoVencidoInfo p : r.periodosVencidos()) {
                Row row = sheet.createRow(idx++);
                row.createCell(0).setCellValue(p.empleadoId());
                row.createCell(1).setCellValue(p.nombreEmpleado());
                setDateCell(row, 2, p.fechaInicio() != null ? p.fechaInicio().format(FMT_CELDA) : "");
                setDateCell(row, 3, p.fechaFin() != null ? p.fechaFin().format(FMT_CELDA) : "");
                row.createCell(4).setCellValue(p.diasRestantes());
            }
        } catch (Exception e) {
            log.error("[SincronizacionReporte] Error al poblar hoja periodos vencidos: {}", e.getMessage());
        }
    }

    private void generarPeriodosCerrados(Workbook wb, SincronizacionPeriodoResponse r) {
        try {
            Sheet sheet = wb.createSheet("Periodos Cerrados");

            Row header = sheet.createRow(0);
            writeHeader(header, new String[]{"Empleado ID", "Nombre", "Fecha Inicio", "Fecha Fin", "Fecha Caducidad", "Días Restantes Perdidos"});

            int idx = 1;
            for (PeriodoCerradoInfo p : r.periodosCerrados()) {
                Row row = sheet.createRow(idx++);
                row.createCell(0).setCellValue(p.empleadoId());
                row.createCell(1).setCellValue(p.nombreEmpleado() != null ? p.nombreEmpleado() : "");
                setDateCell(row, 2, p.fechaInicio() != null ? p.fechaInicio().format(FMT_CELDA) : "");
                setDateCell(row, 3, p.fechaFin() != null ? p.fechaFin().format(FMT_CELDA) : "");
                setDateCell(row, 4, p.fechaCaducidad() != null ? p.fechaCaducidad().format(FMT_CELDA) : "");
                row.createCell(5).setCellValue(p.diasRestantes());
            }
        } catch (Exception e) {
            log.error("[SincronizacionReporte] Error al poblar hoja periodos cerrados: {}", e.getMessage());
        }
    }

    private void guardar(Workbook wb, String nombreArchivo) throws Exception {
        File archivo = new File(DIR + nombreArchivo);
        try (FileOutputStream fos = new FileOutputStream(archivo)) {
            wb.write(fos);
        }
        log.info("[SincronizacionReporte] Reporte generado: {}", archivo.getAbsolutePath());
    }

    private void writeHeader(Row row, String[] cols) {
        for (int i = 0; i < cols.length; i++) {
            row.createCell(i).setCellValue(cols[i]);
        }
    }

    private void setDateCell(Row row, int col, String valor) {
        row.createCell(col).setCellValue(valor);
    }

    private CellStyle dateStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setDataFormat(wb.createDataFormat().getFormat("dd/MM/yyyy"));
        return style;
    }
}
