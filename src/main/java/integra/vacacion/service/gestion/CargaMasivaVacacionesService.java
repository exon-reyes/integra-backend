package integra.vacacion.service.gestion;

import integra.vacacion.core.EstatusPeriodo;
import integra.vacacion.dto.request.FilaVacacionExcel;
import integra.vacacion.dto.response.InconsistenciaVacacionDTO;
import integra.vacacion.entity.PeriodoVacacionalEntity;
import integra.vacacion.repository.PeriodoVacacionalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CargaMasivaVacacionesService {

    private static final int MAX_FILAS = 5_000;
    private static final long MAX_BYTES = 10 * 1024 * 1024;

    private final PeriodoVacacionalRepository periodoRepository;

    @Transactional
    public byte[] procesar(MultipartFile archivo) {
        validarArchivo(archivo);

        List<FilaVacacionExcel> filas = parsearExcel(archivo);
        log.info("[CargaMasiva] Filas leídas: {}", filas.size());

        List<String> codigos = filas.stream().map(FilaVacacionExcel::codigoEmpleado).distinct().toList();

        // Batch con todos los periodos (incluye empleados dados de baja)
        Map<String, PeriodoVacacionalEntity> periodosPorCodigo = periodoRepository
                .findTodosLosPeriodosByCodigos(codigos)
                .stream()
                .collect(Collectors.toMap(
                        p -> p.getEmpleado().getCodigoEmpleado(),
                        p -> p,
                        (a, b) -> a
                ));

        List<InconsistenciaVacacionDTO> inconsistencias = new ArrayList<>();
        int actualizados = 0;

        for (FilaVacacionExcel fila : filas) {
            try {
                PeriodoVacacionalEntity periodo = periodosPorCodigo.get(fila.codigoEmpleado());

                if (periodo == null) {
                    inconsistencias.add(inconsistencia(fila, 0, 0, null, "No encontrado en BD", null, null));
                    continue;
                }

                String estatusEmpleado = periodo.getEmpleado().getEstatus();
                String unidad = periodo.getEmpleado().getUnidad() != null
                        ? periodo.getEmpleado().getUnidad().getNombreCompleto()
                        : null;

                // Empleado dado de baja
                if ("B".equals(estatusEmpleado)) {
                    inconsistencias.add(inconsistencia(fila, periodo.getDiasHabilitados(),
                            periodo.getDiasHabilitados() - safeInt(periodo.getDiasTomados()),
                            periodo.getFechaInicio(), "Empleado dado de baja", unidad, null));
                    log.warn("[CargaMasiva] Fila {}: {} — empleado dado de baja", fila.numeroFila(), fila.codigoEmpleado());
                    continue;
                }

                int habilitadosBD = periodo.getDiasHabilitados();
                int tomadosBD = safeInt(periodo.getDiasTomados());
                int disponiblesBD = habilitadosBD - tomadosBD;

                // Validar fecha de contratación Excel vs fecha de ingreso BD
                LocalDate fechaIngresoBD = empleadoFechaIngreso(periodo);
                if (fila.fechaRegistroExcel() != null && !fila.fechaRegistroExcel().equals(fechaIngresoBD)) {
                    inconsistencias.add(inconsistencia(fila, habilitadosBD, disponiblesBD,
                            fechaIngresoBD, "Fecha de contratación no coincide con BD", unidad, null));
                    log.warn("[CargaMasiva] Fila {}: {} — fecha Excel={} BD={}",
                            fila.numeroFila(), fila.codigoEmpleado(), fila.fechaRegistroExcel(), fechaIngresoBD);
                }

                // Inconsistencia en días habilitados
                if (fila.diasHabilitados() != habilitadosBD) {
                    inconsistencias.add(inconsistencia(fila, habilitadosBD, disponiblesBD,
                            periodo.getFechaInicio(), "Diferencia en días habilitados", unidad, null));
                    log.warn("[CargaMasiva] Fila {}: {} — habilitados Excel={} BD={}",
                            fila.numeroFila(), fila.codigoEmpleado(), fila.diasHabilitados(), habilitadosBD);
                    continue;
                }

                // Validar cuadre interno del Excel: habilitados - (disfrutados + aprobados) == disponibles
                int tomadosExcel = fila.diasDisfrutados() + fila.diasAprobados();
                int disponiblesCalculados = fila.diasHabilitados() - tomadosExcel;
                String detalleCuadre = null;
                if (disponiblesCalculados != fila.diasDisponibles()) {
                    detalleCuadre = String.format("%d habilitados - (%d disfrutados + %d aprobados) = %d, pero disponibles Excel = %d",
                            fila.diasHabilitados(), fila.diasDisfrutados(), fila.diasAprobados(),
                            disponiblesCalculados, fila.diasDisponibles());
                    log.warn("[CargaMasiva] Fila {}: {} — no cuadra: {}", fila.numeroFila(), fila.codigoEmpleado(), detalleCuadre);
                }

                // Periodo consumido
                if (fila.diasDisponibles() == 0 && fila.diasHabilitados() > 0) {
                    periodo.setEstatus(EstatusPeriodo.CONSUMIDO);
                    periodo.setDiasRestantes(0);
                    periodo.setDiasTomados(tomadosExcel);
                    if (detalleCuadre != null) {
                        inconsistencias.add(inconsistencia(fila, habilitadosBD, disponiblesBD,
                                periodo.getFechaInicio(), "Periodo consumido con cuadre inconsistente", unidad, detalleCuadre));
                    }
                    log.info("[CargaMasiva] Fila {}: {} — marcado como CONSUMIDO", fila.numeroFila(), fila.codigoEmpleado());
                    actualizados++;
                    continue;
                }

                // Actualización válida — fuente de verdad: Excel
                periodo.setDiasRestantes(fila.diasDisponibles());
                periodo.setDiasTomados(tomadosExcel);
                if (detalleCuadre != null) {
                    inconsistencias.add(inconsistencia(fila, habilitadosBD, disponiblesBD,
                            periodo.getFechaInicio(), "Actualizado con cuadre inconsistente", unidad, detalleCuadre));
                }
                actualizados++;
                log.debug("[CargaMasiva] Fila {}: {} — disponibles={}, disfrutados={}, aprobados={}",
                        fila.numeroFila(), fila.codigoEmpleado(), fila.diasDisponibles(), fila.diasDisfrutados(), fila.diasAprobados());

            } catch (Exception e) {
                log.error("[CargaMasiva] Error en fila {}: {}", fila.numeroFila(), e.getMessage());
                inconsistencias.add(inconsistencia(fila, 0, 0, null, "Error inesperado: " + e.getMessage(), null, null));
            }
        }

        log.info("[CargaMasiva] Actualizados: {}, Inconsistencias: {}", actualizados, inconsistencias.size());
        return generarExcelInconsistencias(inconsistencias);
    }

    // --- Parseo ---

    private List<FilaVacacionExcel> parsearExcel(MultipartFile archivo) {
        List<FilaVacacionExcel> filas = new ArrayList<>();
        try (Workbook wb = new XSSFWorkbook(archivo.getInputStream())) {
            Sheet sheet = wb.getSheetAt(0);
            int total = sheet.getLastRowNum();

            if (total > MAX_FILAS) {
                throw new IllegalArgumentException("El archivo supera el límite de " + MAX_FILAS + " registros");
            }

            for (int i = 1; i <= total; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String codigo = stringCell(row, 0);  // A
                if (codigo == null || codigo.isBlank()) continue; // fila sin código, omitir silenciosamente

                int habilitados  = intCell(row, 10);      // K
                int disfrutados  = intCell(row, 11);      // L
                int aprobados    = intCell(row, 12);      // M
                int disponibles  = intCell(row, 14);      // O
                LocalDate fechaExcel = dateCell(row, 5);  // F

                filas.add(new FilaVacacionExcel(i + 1, codigo.trim(), habilitados, disfrutados, aprobados, disponibles, fechaExcel));
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al leer el archivo Excel", e);
        }
        return filas;
    }

    // --- Generación de Excel de inconsistencias ---

    private byte[] generarExcelInconsistencias(List<InconsistenciaVacacionDTO> inconsistencias) {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("Inconsistencias");

            Row header = sheet.createRow(0);
            String[] cols = {"Fila", "Código Empleado", "Estatus Empleado", "Unidad",
                    "Habilitados Excel", "Habilitados BD",
                    "Disponibles Excel", "Disponibles BD",
                    "Fecha Registro Excel", "Fecha Registro BD", "Motivo", "Detalle Cuadre"};
            for (int i = 0; i < cols.length; i++) {
                header.createCell(i).setCellValue(cols[i]);
            }

            CellStyle dateStyle = wb.createCellStyle();
            dateStyle.setDataFormat(wb.createDataFormat().getFormat("dd/MM/yyyy"));

            int rowIdx = 1;
            for (InconsistenciaVacacionDTO inc : inconsistencias) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(inc.numeroFila());
                row.createCell(1).setCellValue(inc.codigoEmpleado());
                row.createCell(2).setCellValue(inc.estatusEmpleado() != null ? inc.estatusEmpleado() : "");
                row.createCell(3).setCellValue(inc.unidadEmpleado() != null ? inc.unidadEmpleado() : "");
                row.createCell(4).setCellValue(inc.diasHabilitadosExcel());
                row.createCell(5).setCellValue(inc.diasHabilitadosBD());
                row.createCell(6).setCellValue(inc.diasDisponiblesExcel());
                row.createCell(7).setCellValue(inc.diasDisponiblesBD());
                setDateCell(row, 8, inc.fechaRegistroExcel(), dateStyle);
                setDateCell(row, 9, inc.fechaRegistroBD(), dateStyle);
                row.createCell(10).setCellValue(inc.mensajeError());
                row.createCell(11).setCellValue(inc.detalleCuadre() != null ? inc.detalleCuadre() : "");
            }

            wb.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar Excel de inconsistencias", e);
        }
    }

    // --- Helpers ---

    private void validarArchivo(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("El archivo no puede estar vacío");
        }
        if (archivo.getSize() > MAX_BYTES) {
            throw new IllegalArgumentException("El archivo supera el tamaño máximo permitido de 10 MB");
        }
        String nombre = archivo.getOriginalFilename();
        if (nombre == null || !nombre.endsWith(".xlsx")) {
            throw new IllegalArgumentException("Solo se aceptan archivos .xlsx");
        }
    }

    private InconsistenciaVacacionDTO inconsistencia(FilaVacacionExcel fila, int habilitadosBD,
                                                      int disponiblesBD, LocalDate fechaBD,
                                                      String motivo, String unidad, String detalleCuadre) {
        return new InconsistenciaVacacionDTO(
                fila.numeroFila(), fila.codigoEmpleado(),
                null, unidad,
                fila.diasHabilitados(), habilitadosBD,
                fila.diasDisponibles(), disponiblesBD,
                fila.fechaRegistroExcel(), fechaBD, motivo, detalleCuadre
        );
    }

    private int safeInt(Integer value) {
        return value != null ? value : 0;
    }

    private LocalDate empleadoFechaIngreso(PeriodoVacacionalEntity periodo) {
        LocalDate reingreso = periodo.getEmpleado().getFechaReingreso();
        return reingreso != null ? reingreso : periodo.getEmpleado().getFechaAlta();
    }

    private String stringCell(Row row, int col) {
        Cell cell = row.getCell(col, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return null;
        return cell.getCellType() == CellType.STRING
                ? cell.getStringCellValue()
                : String.valueOf((int) cell.getNumericCellValue());
    }

    private int intCell(Row row, int col) {
        Cell cell = row.getCell(col, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return 0;
        return switch (cell.getCellType()) {
            case NUMERIC -> (int) cell.getNumericCellValue();
            case STRING -> {
                String val = cell.getStringCellValue().trim();
                yield val.isBlank() ? 0 : (int) Double.parseDouble(val);
            }
            case FORMULA -> (int) cell.getNumericCellValue();
            default -> 0;
        };
    }

    private LocalDate dateCell(Row row, int col) {
        Cell cell = row.getCell(col, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null || cell.getCellType() != CellType.NUMERIC) return null;
        try {
            return cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } catch (Exception e) {
            return null;
        }
    }

    private void setDateCell(Row row, int col, LocalDate fecha, CellStyle style) {
        Cell cell = row.createCell(col);
        if (fecha != null) {
            cell.setCellValue(fecha);
            cell.setCellStyle(style);
        }
    }
}
