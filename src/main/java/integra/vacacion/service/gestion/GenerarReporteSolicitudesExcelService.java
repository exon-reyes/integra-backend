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
import java.util.*;
import java.util.stream.Collectors;

/**
 * Genera el Excel de solicitudes de ausencias usando la plantilla "Formato de solicitud.xlsx".
 *
 * Layout por fila de datos (a partir de la fila 8, índice 7):
 *   A = Núm. registro incremental por colaborador
 *   B = Código + Nombre del colaborador
 *   C = Unidad asociada
 *   D = Tipo de solicitud + folio  (ej. "Vacaciones #36011880")
 *   E = Fechas del grupo separadas por coma  (ej. "13/05/2026[P|A], 14/05/2026[P|A]")
 *   G = Días solicitados del grupo
 *   I = Nombre del primer responsable (primera fila del colaborador)
 *       ó estatus abreviado A/P/C para filas de solicitud adicionales
 *   J = Nombre del segundo responsable / estatus abreviado
 *
 * Una fila por solicitud (tipo+folio). Los nombres de responsables van en I y J
 * de la primera fila del colaborador, NO en ninguna fila previa.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GenerarReporteSolicitudesExcelService {

    private static final DateTimeFormatter FMT_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Coordenadas 0-indexed
    private static final int ROW_DESDE      = 2;  // B3
    private static final int ROW_HASTA      = 3;  // B4
    private static final int COL_B          = 1;
    private static final int COL_NUM        = 0;  // A
    private static final int COL_COLAB      = 1;  // B
    private static final int COL_UNIDAD     = 2;  // C
    private static final int COL_SOLICIT    = 3;  // D
    private static final int COL_FECHAS     = 4;  // E
    private static final int COL_DIAS       = 6;  // G
    private static final int COL_JEFE1      = 8;  // I
    private static final int COL_JEFE2      = 9;  // J
    private static final int ROW_DATA_START = 7;  // Fila 8 → índice 7

    private final ObtenerSolicitudes obtenerSolicitudes;

    @Transactional(readOnly = true)
    public byte[] generar(FiltroSolicitud filtro) {
        List<SolicitudesGestionDTO> solicitudes = obtenerSolicitudes.getAllSinPaginacion(filtro);

        try (InputStream is = new ClassPathResource("plantillas/Formato de solicitud.xlsx").getInputStream();
             Workbook workbook = new XSSFWorkbook(is);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.getSheetAt(0);

            // ── Período (B3 y B4) ─────────────────────────────────────────────
            setCell(sheet, ROW_DESDE, COL_B, formatearFecha(filtro.getFechaDesde()));
            setCell(sheet, ROW_HASTA, COL_B, formatearFecha(filtro.getFechaHasta()));

            // ── Agrupar por colaborador manteniendo el orden de llegada ────────
            Map<Integer, List<SolicitudesGestionDTO>> porColaborador = solicitudes.stream()
                    .collect(Collectors.groupingBy(
                            dto -> dto.getColaborador() != null ? dto.getColaborador().getId() : 0,
                            LinkedHashMap::new,
                            Collectors.toList()));

            int rowIdx      = ROW_DATA_START;
            int numRegistro = 1;

            for (List<SolicitudesGestionDTO> grupo : porColaborador.values()) {
                SolicitudesGestionDTO primero = grupo.get(0);

                // ── Fila de cabecera del colaborador ──────────────────────────
                Row rowColab = getOrCreateRow(sheet, rowIdx);
                setCell(rowColab, COL_NUM,    String.valueOf(numRegistro));
                setCell(rowColab, COL_COLAB,  nombreCompleto(primero));
                setCell(rowColab, COL_UNIDAD, nvl(primero.getUnidad()));

                // D = tipos de solicitud agregados (ej. "Vacaciones, Descansos")
                String tipos = grupo.stream()
                        .map(SolicitudesGestionDTO::getTipoSolicitud)
                        .filter(Objects::nonNull)
                        .distinct()
                        .map(this::formatarTipo)
                        .collect(Collectors.joining(", "));
                setCell(rowColab, COL_SOLICIT, tipos);

                // G = suma de días de todas las solicitudes del colaborador
                int totalDias = grupo.stream().mapToInt(SolicitudesGestionDTO::getDiasTotalSolicitud).sum();
                setCell(rowColab, COL_DIAS, totalDias);

                // I, J = nombres de responsables del colaborador
                setCell(rowColab, COL_JEFE1, nvl(primero.getPrimerJefe()));
                setCell(rowColab, COL_JEFE2, nvl(primero.getSegundoJefe()));
                rowIdx++;

                // ── Filas de desglose (una por solicitud/tipo) ────────────────
                for (SolicitudesGestionDTO sol : grupo) {
                    Row rowSol = getOrCreateRow(sheet, rowIdx);
                    setCell(rowSol, COL_SOLICIT, formatarTipoFolio(sol.getTipoSolicitud(), sol.getFolioSolicitud()));
                    setCell(rowSol, COL_FECHAS,  construirFechasStr(sol.getSolicitudes()));
                    setCell(rowSol, COL_DIAS,    sol.getDiasTotalSolicitud());
                    rowIdx++;
                }

                numRegistro++;
            }


            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            log.error("Error al generar reporte de solicitudes", e);
            throw new RuntimeException("Error al generar el excel de solicitudes", e);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Construye el texto de fechas separadas por coma con estatus de cada jefe.
     * Ej: "13/05/2026[P|A], 14/05/2026[P|A], 15/05/2026[A|A]"
     */
    private String construirFechasStr(List<FechaSolicitud> fechas) {
        if (fechas == null || fechas.isEmpty()) return "";
        return fechas.stream()
                .map(f -> {
                    String fecha = f.getFecha() != null ? f.getFecha().format(FMT_FECHA) : "?";
                    String e1    = abreviar(f.getEstatusPrimerJefe());
                    String e2    = abreviar(f.getEstatusSegundoJefe());
                    return fecha + "[" + e1 + "|" + e2 + "]";
                })
                .collect(Collectors.joining(", "));
    }

    /**
     * Estatus abreviado del primer día del grupo para usar en filas secundarias.
     */
    private String estatusAbreviadoGrupo(List<FechaSolicitud> fechas, boolean primerJefe) {
        if (fechas == null || fechas.isEmpty()) return "-";
        return fechas.stream()
                .map(f -> primerJefe ? f.getEstatusPrimerJefe() : f.getEstatusSegundoJefe())
                .filter(Objects::nonNull)
                .findFirst()
                .map(this::abreviar)
                .orElse("-");
    }

    private String formatarTipoFolio(String tipo, long folio) {
        return formatarTipo(tipo) + " #" + folio;
    }

    private String formatarTipo(String tipo) {
        if (tipo == null) return "";
        return switch (tipo) {
            case "VACACION" -> "Vacaciones";
            case "DESCANSO" -> "Descansos";
            default         -> tipo;
        };
    }

    /** APROBADA→A, PENDIENTE→P, CANCELADA→C, null→- */
    private String abreviar(String estatus) {
        if (estatus == null) return "-";
        return switch (estatus) {
            case "APROBADA"  -> "A";
            case "PENDIENTE" -> "P";
            case "CANCELADA" -> "C";
            default          -> estatus.substring(0, 1).toUpperCase();
        };
    }

    private String nombreCompleto(SolicitudesGestionDTO dto) {
        if (dto.getColaborador() == null) return "";
        String codigo = nvl(dto.getColaborador().getCodigo());
        String nombre = nvl(dto.getColaborador().getNombreCompleto());
        return codigo.isBlank() ? nombre : codigo + " " + nombre;
    }

    private String formatearFecha(LocalDate fecha) {
        return fecha != null ? fecha.format(FMT_FECHA) : "";
    }

    private String nvl(String value) {
        return value != null ? value : "";
    }

    private Row getOrCreateRow(Sheet sheet, int rowIdx) {
        Row row = sheet.getRow(rowIdx);
        return row != null ? row : sheet.createRow(rowIdx);
    }

    private void setCell(Sheet sheet, int rowIdx, int colIdx, String value) {
        setCell(getOrCreateRow(sheet, rowIdx), colIdx, value);
    }

    private void setCell(Row row, int colIdx, String value) {
        Cell cell = row.getCell(colIdx, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        cell.setCellValue(value != null ? value : "");
    }

    private void setCell(Row row, int colIdx, int value) {
        Cell cell = row.getCell(colIdx, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        cell.setCellValue(value);
    }
}
