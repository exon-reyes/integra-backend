package integra.acceso.service.user;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class SyncUsuariosExcelService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    @Value("${integra.sync.usuarios.output-dir:./integra-config/sync-usuarios/}")
    private String outputDir;

    public record FilaUsuario(String clave, String nombre, String usuario, String password, String email) {}

    public String generarExcel(List<FilaUsuario> filas) {
        new File(outputDir).mkdirs();
        String nombre = "sync_usuarios_" + LocalDateTime.now().format(FMT) + ".xlsx";
        String ruta = outputDir + nombre;

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Usuarios Creados");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("clave");
            header.createCell(1).setCellValue("nombre");
            header.createCell(2).setCellValue("usuario");
            header.createCell(3).setCellValue("contraseña");
            header.createCell(4).setCellValue("correo");

            int idx = 1;
            for (FilaUsuario fila : filas) {
                Row row = sheet.createRow(idx++);
                row.createCell(0).setCellValue(fila.clave());
                row.createCell(1).setCellValue(fila.nombre());
                row.createCell(2).setCellValue(fila.usuario());
                row.createCell(3).setCellValue(fila.password());
                row.createCell(4).setCellValue(fila.email());
            }

            try (FileOutputStream fos = new FileOutputStream(ruta)) {
                wb.write(fos);
            }
            log.info("[SyncUsuarios] Excel generado: {}", ruta);
        } catch (Exception e) {
            log.error("[SyncUsuarios] Error al generar Excel: {}", e.getMessage());
        }

        return ruta;
    }
}
