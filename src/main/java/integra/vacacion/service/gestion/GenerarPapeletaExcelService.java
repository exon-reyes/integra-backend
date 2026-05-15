package integra.vacacion.service.gestion;

import integra.vacacion.dto.response.DetalleSolicitudDTO;
import integra.vacacion.dto.response.FechaSolicitud;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenerarPapeletaExcelService {

    private final DetallesSolicitud detallesSolicitud;

    @Transactional(readOnly = true)
    public byte[] generar(Long folio, Double salarioDiario, Integer diasAdicionales) {
        DetalleSolicitudDTO dto = detallesSolicitud.getByFolio(folio);

        try (InputStream is = new ClassPathResource("plantillas/papeleta.xlsx").getInputStream();
             Workbook workbook = new XSSFWorkbook(is);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.getSheetAt(0);

            // AH3 -> Row 2, Col 33 -> folioSolicitud
            setCell(sheet, 2, 33, String.valueOf(dto.getFolioSolicitud()));

            // C13 -> Row 12, Col 2 -> empleado.nombreCompleto
            if (dto.getEmpleado() != null) {
                setCell(sheet, 12, 2, dto.getEmpleado().getNombreCompleto());

                // Q13 -> Row 12, Col 16 -> empleado.unidad.nombreCompleto
                if (dto.getEmpleado().getUnidad() != null) {
                    setCell(sheet, 12, 16, dto.getEmpleado().getUnidad().getNombreCompleto());
                }

                // C16 -> Row 15, Col 2 -> empleado.puesto.nombre
                if(dto.getEmpleado().getPuesto() != null) {
                    setCell(sheet, 15, 2, dto.getEmpleado().getPuesto().getNombre());
                }

                // Q16 -> Row 15, Col 16 -> empleado.fechaAlta
                if (dto.getEmpleado().getFechaAlta() != null) {
                    setCell(sheet, 15, 16, dto.getEmpleado().getFechaAlta().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

                    // AB13 -> Row 12, Col 27 -> Años cumplidos
                    long anios = ChronoUnit.YEARS.between(dto.getEmpleado().getFechaAlta(), LocalDate.now());
                    setCell(sheet, 12, 27, String.valueOf(anios));
                }

                // AB16 -> Row 15, Col 27 -> codigoEmpleado
                if (dto.getEmpleado().getCodigo() != null) {
                    setCell(sheet, 15, 27, dto.getEmpleado().getCodigo());
                }

                // B45 -> Row 44, Col 1 -> nombre de quien solicito
                setCell(sheet, 44, 1, dto.getEmpleado().getNombreCompleto());

                // B46 -> Row 45, Col 1 -> puesto de quien solicito
                if (dto.getEmpleado().getPuesto() != null) {
                    setCell(sheet, 45, 1, dto.getEmpleado().getPuesto().getNombre());
                }
            }

            // D19 -> Row 18, Col 3 -> salarioDiario
            if (salarioDiario != null) {
                setCell(sheet, 18, 3, salarioDiario);
            }

            // O26 -> Row 25, Col 14 -> anioGestion
            setCell(sheet, 25, 14, dto.getAnioGestion());

            // Q26 -> Row 25, Col 16 -> diasHabilitados
            setCell(sheet, 25, 16, dto.getDiasHabilitados());

            // Q27 -> Row 26, Col 16 -> diasTomados
            setCell(sheet, 26, 16, dto.getDiasTomados());

            // Q28 -> Row 27, Col 16 -> diasSolicitados
            setCell(sheet, 27, 16, dto.getDiasSolicitados());

            // Q29 -> Row 28, Col 16 -> remaining (diasHabilitados - diasTomados - diasSolicitados)
            setCell(sheet, 28, 16, dto.getRestanteSiAprueba());

            // AB26 -> Row 25, Col 27 -> diasAdicionales
            if (diasAdicionales != null) {
                setCell(sheet, 25, 27, diasAdicionales);
            }

            // W26 -> Row 25, Col 22 -> fechaCreacion
            if (dto.getFechaCreacion() != null) {
                setCell(sheet, 25, 22, dto.getFechaCreacion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }

            // W29 -> Row 28, Col 22 -> anioGestion
            setCell(sheet, 28, 22, dto.getAnioGestion());

            // C34 -> Row 33, Col 2 -> fechas autorizadas
            String fechasStr = dto.getFechaSolicituds().stream()
                    .filter(r -> r.getEstatusSegundoJefe().equals("APROBADA"))
                    .map(FechaSolicitud::getFecha)
                    .map(LocalDate::toString)
                    .collect(Collectors.joining(", "));
            setCell(sheet, 33, 2, fechasStr);

            // W45 -> Row 44, Col 22 -> primerJefe.nombreCompleto
            if (dto.getPrimerJefe() != null) {
                setCell(sheet, 44, 22, dto.getPrimerJefe().getNombreCompleto());
                if (dto.getPrimerJefe().getPuesto() != null) {
                    setCell(sheet, 45, 22, dto.getPrimerJefe().getPuesto().getNombre());
                }
            }

            // W54 -> Row 53, Col 22 -> segundoJefe.nombreCompleto
            if (dto.getSegundoJefe() != null) {
                setCell(sheet, 53, 22, dto.getSegundoJefe().getNombreCompleto());
                if (dto.getSegundoJefe().getPuesto() != null) {
                    setCell(sheet, 54, 22, dto.getSegundoJefe().getPuesto().getNombre());
                }
            }

            // B55 -> Row 54, Col 1 -> Generado el
            String generado = "Generado el " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            setCell(sheet, 54, 1, generado);

            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            log.error("Error al generar la papeleta", e);
            throw new RuntimeException("Error al generar la papeleta excel", e);
        }
    }

    private void setCell(Sheet sheet, int rowIndex, int colIndex, String value) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        Cell cell = row.getCell(colIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        cell.setCellValue(value);
    }

    private void setCell(Sheet sheet, int rowIndex, int colIndex, double value) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        Cell cell = row.getCell(colIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        cell.setCellValue(value);
    }
}
