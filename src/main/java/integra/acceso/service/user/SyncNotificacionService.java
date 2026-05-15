package integra.acceso.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyncNotificacionService {

    private final NotificacionService notificacionService;

    public record ResultadoNotificacion(int enviados, int omitidos, List<String> errores) {}

    public ResultadoNotificacion enviarCredencialesDesdeExcel(MultipartFile archivo) {
        List<SyncUsuariosExcelService.FilaUsuario> filas = leerExcel(archivo);

        int enviados = 0;
        int omitidos = 0;
        List<String> errores = new ArrayList<>();

        for (SyncUsuariosExcelService.FilaUsuario fila : filas) {
            if (fila.email() == null || fila.email().isBlank()) {
                omitidos++;
                log.warn("[SyncNotificacion] Sin correo para usuario: {}", fila.usuario());
                continue;
            }
            try {
                notificacionService.enviarCredenciales(fila.email(), fila.nombre(), fila.usuario(), fila.password());
                enviados++;
                log.info("[SyncNotificacion] Correo enviado a: {}", fila.email());
                Thread.sleep(9000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                log.warn("[SyncNotificacion] Envío interrumpido en: {}", fila.email());
                break;
            } catch (Exception e) {
                omitidos++;
                String msg = "Error al enviar a %s: %s".formatted(fila.email(), e.getMessage());
                errores.add(msg);
                log.error("[SyncNotificacion] {}", msg);
            }
        }

        return new ResultadoNotificacion(enviados, omitidos, errores);
    }

    private List<SyncUsuariosExcelService.FilaUsuario> leerExcel(MultipartFile archivo) {
        List<SyncUsuariosExcelService.FilaUsuario> filas = new ArrayList<>();
        try (InputStream fis = archivo.getInputStream();
             Workbook wb = new XSSFWorkbook(fis)) {

            Sheet sheet = wb.getSheetAt(0);
            // fila 0 es header, empezar desde 1
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                filas.add(new SyncUsuariosExcelService.FilaUsuario(
                        cellValue(row, 0),
                        cellValue(row, 1),
                        cellValue(row, 2),
                        cellValue(row, 3),
                        cellValue(row, 4)
                ));
            }
        } catch (Exception e) {
            log.error("[SyncNotificacion] Error al leer Excel {}: {}", archivo.getOriginalFilename(), e.getMessage());
        }
        return filas;
    }

    private String cellValue(Row row, int col) {
        var cell = row.getCell(col);
        return cell != null ? cell.getStringCellValue() : "";
    }
}
