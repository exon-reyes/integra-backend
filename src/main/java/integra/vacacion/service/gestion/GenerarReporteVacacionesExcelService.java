package integra.vacacion.service.gestion;

import integra.vacacion.dto.response.ReportePeriodoVacacionalProjection;
import integra.vacacion.repository.PeriodoVacacionalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenerarReporteVacacionesExcelService {

    private final PeriodoVacacionalRepository periodoVacacionalRepository;

    public byte[] generar() {
        List<ReportePeriodoVacacionalProjection> datos = periodoVacacionalRepository.obtenerReporteMejoresPeriodos();

        try (InputStream is = new ClassPathResource("plantillas/vacaciones.xlsx").getInputStream();
             Workbook workbook = new XSSFWorkbook(is);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.getSheetAt(0);
            int rowIdx = 4; // Fila 5 en excel es el index 4

            CreationHelper createHelper = workbook.getCreationHelper();
            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));

            for (ReportePeriodoVacacionalProjection fila : datos) {
                Row row = sheet.getRow(rowIdx);
                if (row == null) {
                    row = sheet.createRow(rowIdx);
                }
                rowIdx++;

                Cell cellClave = row.createCell(0);
                cellClave.setCellValue(fila.getClave() != null ? fila.getClave() : "");

                Cell cellColaborador = row.createCell(1);
                cellColaborador.setCellValue(fila.getColaborador() != null ? fila.getColaborador() : "");

                Cell cellEstatus = row.createCell(2);
                cellEstatus.setCellValue(fila.getEstatus() != null ? fila.getEstatus() : "");

                Cell cellUnidad = row.createCell(3);
                cellUnidad.setCellValue(fila.getUnidadAsociada() != null ? fila.getUnidadAsociada() : "");

                Cell cellFechaIngreso = row.createCell(4);
                if(fila.getFechaIngreso() != null) {
                    cellFechaIngreso.setCellValue(fila.getFechaIngreso());
                    cellFechaIngreso.setCellStyle(dateStyle);
                }

                Cell cellPuesto = row.createCell(5);
                cellPuesto.setCellValue(fila.getPuesto() != null ? fila.getPuesto() : "");

                Cell cellResponsable = row.createCell(6);
                cellResponsable.setCellValue(fila.getResponsable() != null ? fila.getResponsable() : "");

                Cell cellResponsableNivel2 = row.createCell(7);
                cellResponsableNivel2.setCellValue(fila.getResponsableNivel2() != null ? fila.getResponsableNivel2() : "");

                Cell cellAnioLaboral = row.createCell(8);
                if (fila.getAnioLaboral() != null) {
                    cellAnioLaboral.setCellValue(fila.getAnioLaboral());
                }

                Cell cellInicio = row.createCell(9);
                if(fila.getFechaInicio() != null) {
                    cellInicio.setCellValue(fila.getFechaInicio());
                    cellInicio.setCellStyle(dateStyle);
                }

                Cell cellFin = row.createCell(10);
                if(fila.getFechaFin() != null) {
                    cellFin.setCellValue(fila.getFechaFin());
                    cellFin.setCellStyle(dateStyle);
                }

                Cell cellCaducidad = row.createCell(11);
                if(fila.getFechaCaducidad() != null) {
                    cellCaducidad.setCellValue(fila.getFechaCaducidad());
                    cellCaducidad.setCellStyle(dateStyle);
                }

                Cell cellHabilitadas = row.createCell(12);
                if (fila.getHabilitadas() != null) {
                    cellHabilitadas.setCellValue(fila.getHabilitadas());
                }

                Cell cellTomadas = row.createCell(13);
                if (fila.getTomadas() != null) {
                    cellTomadas.setCellValue(fila.getTomadas());
                }

                Cell cellRestantes = row.createCell(14);
                if (fila.getRestantes() != null) {
                    cellRestantes.setCellValue(fila.getRestantes());
                }

                Cell cellEstatusPeriodo = row.createCell(15);
                cellEstatusPeriodo.setCellValue(fila.getEstatusPeriodo() != null ? fila.getEstatusPeriodo() : "");

                Cell cellAnioGestion = row.createCell(16);
                if (fila.getAnioGestion() != null) {
                    cellAnioGestion.setCellValue(fila.getAnioGestion());
                }
            }

            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            log.error("Error al generar el archivo excel de vacaciones", e);
            throw new RuntimeException("Error al generar el archivo excel", e);
        }
    }
}
