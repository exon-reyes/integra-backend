package integra.vacacion.service.gestion;

import integra.vacacion.dto.request.FiltroSolicitud;
import integra.vacacion.dto.response.FechaSolicitud;
import integra.vacacion.dto.response.SolicitudesGestionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Genera el reporte de solicitudes consolidado en formato pre-nómina.
 *
 * <p>Algoritmo de agrupamiento (ver documentacion/algoritmo-excel-vacaciones):
 * <ol>
 *   <li>Agrupa todos los registros por empleado.</li>
 *   <li>Dentro de cada empleado, ordena cronológicamente todos sus días solicitados.</li>
 *   <li>Itera con "memoria": si el día actual es consecutivo al anterior Y el tipo de
 *       solicitud es el mismo, extiende la Fecha de Fin del rango en curso.</li>
 *   <li>Si hay un salto de días (no consecutivo) o el tipo cambia, cierra el rango
 *       actual y abre uno nuevo.</li>
 * </ol>
 *
 * <p>Columnas del reporte:
 * <pre>
 *   A – Clave / Código del empleado
 *   B – Nombre completo del empleado
 *   C – Puesto
 *   D – Responsable (primer jefe)
 *   E – Tipo de ausencia
 *   F – Fecha de inicio  (dd/MM/yyyy)
 *   G – Fecha de fin     (dd/MM/yyyy)
 * </pre>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GenerarReporteSolicitudesConsolidadoExcelService {

    // ── Formato de fecha ──────────────────────────────────────────────────────
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ── Índices de columna (0-based) ──────────────────────────────────────────
    private static final int COL_CLAVE       = 0; // A
    private static final int COL_NOMBRE      = 1; // B
    private static final int COL_PUESTO      = 2; // C
    private static final int COL_RESPONSABLE = 3; // D
    private static final int COL_TIPO        = 4; // E
    private static final int COL_FECHA_INI   = 5; // F
    private static final int COL_FECHA_FIN   = 6; // G

    // ── Anchos de columna en unidades POI (1/256 de carácter) ────────────────
    private static final int[] COL_WIDTHS = {
            2500,  // A – Clave
            8000,  // B – Nombre
            6000,  // C – Puesto
            8000,  // D – Responsable
            5000,  // E – Tipo
            3500,  // F – Fecha inicio
            3500   // G – Fecha fin
    };

    // ── Colores ───────────────────────────────────────────────────────────────
    /** Azul oscuro corporativo para el encabezado */
    private static final byte[] COLOR_HEADER_BG = hexToRgb("1F3864");

    private final ObtenerSolicitudes obtenerSolicitudes;

    // =========================================================================
    //  Punto de entrada
    // =========================================================================

    @Transactional(readOnly = true)
    public byte[] generar(FiltroSolicitud filtro) {
        List<SolicitudesGestionDTO> solicitudes = obtenerSolicitudes.getAllSinPaginacion(filtro);

        // 1. Construir filas consolidadas aplicando el algoritmo
        List<FilaReporte> filas = consolidar(solicitudes);

        // 2. Generar el workbook
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            XSSFSheet sheet = workbook.createSheet("Solicitudes");

            // Anchos de columna
            for (int i = 0; i < COL_WIDTHS.length; i++) {
                sheet.setColumnWidth(i, COL_WIDTHS[i]);
            }

            // Estilos
            Styles styles = new Styles(workbook);

            // Fila de encabezado (índice 0)
            crearEncabezado(sheet, styles);

            // Filas de datos (a partir del índice 1)
            int rowIdx = 1;

            for (FilaReporte fila : filas) {
                Row row = sheet.createRow(rowIdx);

                // A – Clave del empleado
                row.createCell(COL_CLAVE).setCellValue(nvl(fila.clave));

                // B – Nombre completo
                row.createCell(COL_NOMBRE).setCellValue(fila.nombreEmpleado);

                // C – Puesto
                row.createCell(COL_PUESTO).setCellValue(nvl(fila.puesto));

                // D – Responsable
                row.createCell(COL_RESPONSABLE).setCellValue(nvl(fila.responsable));

                // E – Tipo de ausencia
                row.createCell(COL_TIPO).setCellValue(nvl(fila.tipoAusencia));

                // F – Fecha de inicio
                row.createCell(COL_FECHA_INI).setCellValue(formatFecha(fila.fechaInicio));

                // G – Fecha de fin
                row.createCell(COL_FECHA_FIN).setCellValue(formatFecha(fila.fechaFin));

                rowIdx++;
            }

            // Agregar filtros automáticos en el encabezado
            sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, COL_WIDTHS.length - 1));

            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            log.error("Error al generar reporte consolidado de solicitudes", e);
            throw new RuntimeException("Error al generar el excel consolidado de solicitudes", e);
        }
    }

    // =========================================================================
    //  Algoritmo de consolidación
    // =========================================================================

    /**
     * Aplica el algoritmo descrito en documentacion/algoritmo-excel-vacaciones.
     *
     * <p>Pasos:
     * <ol>
     *   <li>Agrupa solicitudes por empleado (LinkedHashMap para mantener orden).</li>
     *   <li>Por cada empleado, expande todos sus días individuales en una lista plana
     *       de {@link DiaExpandido} (fecha + tipo + metadatos).</li>
     *   <li>Ordena esa lista cronológicamente.</li>
     *   <li>Itera con memoria: compara cada día con el anterior para decidir si
     *       extiende el rango o abre uno nuevo.</li>
     * </ol>
     */
    private List<FilaReporte> consolidar(List<SolicitudesGestionDTO> solicitudes) {
        // Agrupar por ID de empleado manteniendo el orden de llegada
        Map<Integer, List<SolicitudesGestionDTO>> porEmpleado = solicitudes.stream()
                .collect(Collectors.groupingBy(
                        dto -> dto.getColaborador() != null ? dto.getColaborador().getId() : 0,
                        LinkedHashMap::new,
                        Collectors.toList()));

        List<FilaReporte> resultado = new ArrayList<>();

        for (List<SolicitudesGestionDTO> grupo : porEmpleado.values()) {
            // Metadatos del empleado (tomados de la primera solicitud)
            SolicitudesGestionDTO primero = grupo.get(0);
            String clave          = clave(primero);
            String nombreEmpleado = nombreCompleto(primero);
            String puesto         = puesto(primero);
            String responsable    = nvl(primero.getPrimerJefe());

            // Expandir todos los días del empleado en una lista plana
            List<DiaExpandido> dias = expandirDias(grupo, clave, nombreEmpleado, puesto, responsable);

            // Ordenar cronológicamente
            dias.sort(Comparator.comparing(d -> d.fecha));

            // Iterar con memoria para consolidar rangos
            FilaReporte rangoActual = null;

            for (DiaExpandido dia : dias) {
                if (rangoActual == null) {
                    // Primer día: abrir rango
                    rangoActual = new FilaReporte(dia);
                } else {
                    boolean mismoTipo      = dia.tipoSolicitud.equals(rangoActual.tipoSolicitud);
                    boolean esConsecutivo  = dia.fecha.equals(rangoActual.fechaFin.plusDays(1));

                    if (mismoTipo && esConsecutivo) {
                        // Escenario 1: mismo tipo y consecutivo → extender rango
                        rangoActual.fechaFin = dia.fecha;
                    } else {
                        // Escenario 2 (salto de días) o Escenario 3 (cambio de tipo):
                        // cerrar rango actual y abrir uno nuevo
                        resultado.add(rangoActual);
                        rangoActual = new FilaReporte(dia);
                    }
                }
            }

            // Cerrar el último rango del empleado
            if (rangoActual != null) {
                resultado.add(rangoActual);
            }
        }

        return resultado;
    }

    /**
     * Expande todas las solicitudes de un empleado en días individuales.
     * Cada {@link FechaSolicitud} dentro de una solicitud se convierte en un
     * {@link DiaExpandido} que lleva el tipo de solicitud de su solicitud padre.
     */
    private List<DiaExpandido> expandirDias(
            List<SolicitudesGestionDTO> solicitudes,
            String clave,
            String nombreEmpleado,
            String puesto,
            String responsable) {

        List<DiaExpandido> dias = new ArrayList<>();

        for (SolicitudesGestionDTO sol : solicitudes) {
            if (sol.getSolicitudes() == null) continue;
            String tipo = formatarTipo(sol.getTipoSolicitud());

            for (FechaSolicitud fs : sol.getSolicitudes()) {
                if (fs.getFecha() == null) continue;
                dias.add(new DiaExpandido(
                        fs.getFecha(),
                        tipo,
                        sol.getTipoSolicitud(), // clave interna para comparación
                        clave,
                        nombreEmpleado,
                        puesto,
                        responsable));
            }
        }

        return dias;
    }

    // =========================================================================
    //  Construcción del encabezado
    // =========================================================================

    private void crearEncabezado(XSSFSheet sheet, Styles styles) {
        Row header = sheet.createRow(0);
        header.setHeightInPoints(22);

        String[] titulos = {
                "Clave",
                "Nombre completo",
                "Puesto",
                "Responsable",
                "Tipo de ausencia",
                "Fecha de inicio",
                "Fecha de fin"
        };

        for (int i = 0; i < titulos.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(titulos[i]);
            cell.setCellStyle(styles.header);
        }
    }

    // =========================================================================
    //  Estilos POI
    // =========================================================================

    /** Solo contiene el estilo del encabezado. */
    private static class Styles {
        final CellStyle header;

        Styles(XSSFWorkbook wb) {
            XSSFFont fuenteHeader = wb.createFont();
            fuenteHeader.setBold(true);
            fuenteHeader.setColor(new XSSFColor(new byte[]{(byte) 255, (byte) 255, (byte) 255}, null));
            fuenteHeader.setFontHeightInPoints((short) 11);

            header = wb.createCellStyle();
            ((XSSFCellStyle) header).setFillForegroundColor(new XSSFColor(COLOR_HEADER_BG, null));
            header.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            header.setFont(fuenteHeader);
            header.setAlignment(HorizontalAlignment.CENTER);
            header.setVerticalAlignment(VerticalAlignment.CENTER);
        }
    }

    // =========================================================================
    //  Modelos internos
    // =========================================================================

    /** Representa un día individual expandido de una solicitud. */
    private static class DiaExpandido {
        final LocalDate fecha;
        final String tipoSolicitud;      // clave interna (VACACION, DESCANSO…)
        final String tipoAusenciaLabel;  // etiqueta formateada para mostrar
        final String clave;
        final String nombreEmpleado;
        final String puesto;
        final String responsable;

        DiaExpandido(LocalDate fecha, String tipoAusenciaLabel, String tipoSolicitud,
                     String clave, String nombreEmpleado, String puesto, String responsable) {
            this.fecha             = fecha;
            this.tipoAusenciaLabel = tipoAusenciaLabel;
            this.tipoSolicitud     = tipoSolicitud;
            this.clave             = clave;
            this.nombreEmpleado    = nombreEmpleado;
            this.puesto            = puesto;
            this.responsable       = responsable;
        }
    }

    /** Representa una fila consolidada del reporte final. */
    private static class FilaReporte {
        final String clave;
        final String nombreEmpleado;
        final String puesto;
        final String responsable;
        final String tipoAusencia;
        final String tipoSolicitud; // clave interna para comparación en el algoritmo
        LocalDate fechaInicio;
        LocalDate fechaFin;

        FilaReporte(DiaExpandido dia) {
            this.clave          = dia.clave;
            this.nombreEmpleado = dia.nombreEmpleado;
            this.puesto         = dia.puesto;
            this.responsable    = dia.responsable;
            this.tipoAusencia   = dia.tipoAusenciaLabel;
            this.tipoSolicitud  = dia.tipoSolicitud;
            this.fechaInicio    = dia.fecha;
            this.fechaFin       = dia.fecha;
        }
    }

    // =========================================================================
    //  Helpers
    // =========================================================================

    private String nombreCompleto(SolicitudesGestionDTO dto) {
        if (dto.getColaborador() == null) return "";
        return nvl(dto.getColaborador().getNombreCompleto());
    }

    private String clave(SolicitudesGestionDTO dto) {
        if (dto.getColaborador() == null) return "";
        return nvl(dto.getColaborador().getCodigo());
    }

    private String puesto(SolicitudesGestionDTO dto) {
        if (dto.getColaborador() == null) return "";
        if (dto.getColaborador().getPuesto() == null) return "";
        return nvl(dto.getColaborador().getPuesto().getNombre());
    }

    private String formatarTipo(String tipo) {
        if (tipo == null) return "";
        return switch (tipo.toUpperCase()) {
            case "VACACION" -> "Vacaciones";
            case "DESCANSO" -> "Solicitud día de descanso";
            default         -> tipo;
        };
    }

    private String formatFecha(LocalDate fecha) {
        return fecha != null ? fecha.format(FMT) : "";
    }

    private String nvl(String value) {
        return value != null ? value : "";
    }

    private static byte[] hexToRgb(String hex) {
        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);
        return new byte[]{(byte) r, (byte) g, (byte) b};
    }
}
