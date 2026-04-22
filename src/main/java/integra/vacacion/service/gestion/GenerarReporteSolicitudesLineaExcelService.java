package integra.vacacion.service.gestion;

import integra.vacacion.dto.request.FiltroSolicitud;
import integra.vacacion.dto.response.FechaSolicitud;
import integra.vacacion.dto.response.SolicitudesGestionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Genera el Excel de solicitudes en formato LÍNEA por día vacacional.
 *
 * Una fila por cada día solicitado dentro de un folio, con las columnas:
 *   A = Empleado (código + nombre)
 *   B = Fecha Ingreso de Solicitud (fecha_creacion de la solicitud)
 *   C = Unidad Asociada
 *   D = Folio Solicitud
 *   E = Tipo Solicitud
 *   F = Fecha Solicitada (día individual)
 *   G = Primer Responsable
 *   H = Segundo Responsable
 *   I = Estatus Primer Responsable (granular del día)
 *   J = Estatus Segundo Responsable (granular del día)
 *
 * Usa la plantilla "solicitudes-vacaciones.xlsx" que ya tiene el encabezado configurado.
 * Los datos comienzan en la fila 2 (índice 1).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GenerarReporteSolicitudesLineaExcelService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Columnas (0-indexed)
    private static final int COL_EMPLEADO       = 0; // A
    private static final int COL_FECHA_INGRESO  = 1; // B
    private static final int COL_UNIDAD         = 2; // C
    private static final int COL_FOLIO          = 3; // D
    private static final int COL_TIPO           = 4; // E
    private static final int COL_FECHA_SOL      = 5; // F
    private static final int COL_JEFE1          = 6; // G
    private static final int COL_JEFE2          = 7; // H
    private static final int COL_ESTATUS_J1     = 8; // I
    private static final int COL_ESTATUS_J2     = 9; // J

    // La plantilla tiene encabezados en la fila 1; datos desde fila 2 (índice 1)
    private static final int ROW_DATA_START = 1;

    private final ObtenerSolicitudes obtenerSolicitudes;

    @Transactional(readOnly = true)
    public byte[] generar(FiltroSolicitud filtro) {
        List<SolicitudesGestionDTO> solicitudes = obtenerSolicitudes.getAllSinPaginacion(filtro);

        try (InputStream is = new ClassPathResource("plantillas/solicitudes-vacaciones.xlsx").getInputStream();
             Workbook workbook = new XSSFWorkbook(is);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.getSheetAt(0);

            int rowIdx = ROW_DATA_START;

            for (SolicitudesGestionDTO sol : solicitudes) {
                List<FechaSolicitud> dias = sol.getSolicitudes();
                if (dias == null || dias.isEmpty()) continue;

                for (FechaSolicitud dia : dias) {
                    Row row = getOrCreateRow(sheet, rowIdx);

                    // A – Empleado
                    setCell(row, COL_EMPLEADO, nombreCompleto(sol));

                    // B – Fecha de ingreso de la solicitud
                    setCell(row, COL_FECHA_INGRESO, formatFecha(sol.getFechaCreacion()));

                    // C – Unidad
                    setCell(row, COL_UNIDAD, nvl(sol.getUnidad()));

                    // D – Folio
                    setCell(row, COL_FOLIO, String.valueOf(sol.getFolioSolicitud()));

                    // E – Tipo de solicitud
                    setCell(row, COL_TIPO, formatarTipo(sol.getTipoSolicitud()));

                    // F – Fecha solicitada (día individual)
                    setCell(row, COL_FECHA_SOL, formatFecha(dia.getFecha()));

                    // G – Primer responsable
                    setCell(row, COL_JEFE1, nvl(sol.getPrimerJefe()));

                    // H – Segundo responsable
                    setCell(row, COL_JEFE2, nvl(sol.getSegundoJefe()));

                    // I – Estatus granular primer responsable
                    setCell(row, COL_ESTATUS_J1, nvl(dia.getEstatusPrimerJefe()));

                    // J – Estatus granular segundo responsable
                    setCell(row, COL_ESTATUS_J2, nvl(dia.getEstatusSegundoJefe()));

                    rowIdx++;
                }
            }

            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            log.error("Error al generar reporte solicitudes en línea", e);
            throw new RuntimeException("Error al generar el excel de solicitudes en línea", e);
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private String nombreCompleto(SolicitudesGestionDTO dto) {
        if (dto.getColaborador() == null) return "";
        String codigo = nvl(dto.getColaborador().getCodigo());
        String nombre = nvl(dto.getColaborador().getNombreCompleto());
        return codigo.isBlank() ? nombre : codigo + " " + nombre;
    }

    private String formatarTipo(String tipo) {
        if (tipo == null) return "";
        return switch (tipo) {
            case "VACACION" -> "Vacacion";
            case "DESCANSO" -> "Descanso";
            default         -> tipo;
        };
    }

    private String formatFecha(LocalDate fecha) {
        return fecha != null ? fecha.format(FMT) : "";
    }

    private String nvl(String value) {
        return value != null ? value : "";
    }

    private Row getOrCreateRow(Sheet sheet, int rowIdx) {
        Row row = sheet.getRow(rowIdx);
        return row != null ? row : sheet.createRow(rowIdx);
    }

    private void setCell(Row row, int colIdx, String value) {
        Cell cell = row.getCell(colIdx, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        cell.setCellValue(value != null ? value : "");
    }
}
