package integra.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Locale;
import java.util.Objects;

@Slf4j
public class ImageUtils {

    private static final DateTimeFormatter FILENAME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss", Locale.ROOT);
    private static final String IMAGE_FORMAT = "jpg";
    private static final float COMPRESSION_QUALITY = 0.3f;

    static {
        ImageIO.scanForPlugins();
    }

    /**
     * Guarda un MultipartFile directamente al disco sin recomprimir.
     * La imagen ya llega optimizada (resize + compresión) desde el cliente Angular.
     * Costo de CPU: mínimo (solo transferencia de bytes al disco).
     */
    public static String saveMultipartImage(MultipartFile file, Integer id, Path directory) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo de imagen no puede estar vacío");
        }
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido: " + id);
        }

        String filename = generateFilename(id);
        Path filePath = directory.resolve(filename).normalize();
        if (!filePath.startsWith(directory.normalize())) {
            throw new SecurityException("Acceso denegado: ruta fuera del directorio permitido");
        }

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
        return filename;
    }

    /**
     * @deprecated Usar {@link #saveMultipartImage} para transferencias desde el checador.
     * Mantener solo para módulos que aún envíen Base64.
     */
    public static String saveBase64Image(String base64Data, Integer id, Path directory) throws IOException {
        validateInput(base64Data, id);

        String cleanData = cleanBase64Data(base64Data);
        String filename = generateFilename(id);

        byte[] imageBytes = Base64.getDecoder().decode(cleanData);
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
        if (image == null) throw new IOException("No se pudo decodificar la imagen base64");
        byte[] compressed = compressImage(image);

        Path filePath = directory.resolve(filename).normalize();
        if (!filePath.startsWith(directory.normalize())) {
            throw new SecurityException("Acceso denegado: ruta fuera del directorio permitido");
        }
        Files.write(filePath, compressed);
        return filename;
    }

    public static Resource getImage(String filename, Path directory) {
        Path filePath = getVerifiedFilePath(filename, directory);
        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                throw new RuntimeException("Archivo no encontrado: " + filename);
            }
            return resource;
        } catch (IOException e) {
            throw new RuntimeException("Error obteniendo imagen: " + filename, e);
        }
    }

    public static Resource getResizedImage(String filename, int width, int height, Path directory) throws IOException {
        Path filePath = getVerifiedFilePath(filename, directory);
        BufferedImage originalImage = ImageIO.read(filePath.toFile());

        if (originalImage == null) {
            throw new IOException("No se pudo leer la imagen: " + filename);
        }

        BufferedImage resizedImage = resizeImage(originalImage, width, height);
        byte[] compressedImage = compressImage(resizedImage);
        return createResource(compressedImage, filename);
    }

    private static void validateInput(String data, Integer id) {
        if (data == null || data.trim().isEmpty()) {
            throw new IllegalArgumentException("Los datos de la imagen no pueden estar vacíos");
        }
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido: " + id);
        }
    }

    private static String cleanBase64Data(String data) {
        return data.contains(",") ? data.substring(data.indexOf(",") + 1) : data;
    }

    private static String generateFilename(Integer id) {
        String timestamp = LocalDateTime.now().format(FILENAME_FORMATTER);
        return String.format("%d-%s.%s", id, timestamp, IMAGE_FORMAT);
    }

    private static Path getVerifiedFilePath(String filename, Path directory) {
        Objects.requireNonNull(filename, "El nombre de archivo no puede ser nulo");
        Path filePath = directory.resolve(filename).normalize();
        if (!filePath.startsWith(directory.normalize())) {
            throw new SecurityException("Acceso denegado: ruta fuera del directorio permitido");
        }
        if (!Files.exists(filePath)) {
            throw new RuntimeException("Archivo no encontrado: " + filename);
        }
        return filePath;
    }

    private static BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = outputImage.createGraphics();
        try {
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(originalImage, 0, 0, width, height, null);
        } finally {
            g2d.dispose();
        }
        return outputImage;
    }

    private static byte[] compressImage(BufferedImage image) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {

            ImageWriter writer = ImageIO.getImageWritersByFormatName(IMAGE_FORMAT).next();
            try {
                writer.setOutput(ios);
                ImageWriteParam param = writer.getDefaultWriteParam();
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(COMPRESSION_QUALITY);
                writer.write(null, new IIOImage(image, null, null), param);
            } finally {
                writer.dispose();
            }

            return baos.toByteArray();
        }
    }

    private static Resource createResource(byte[] imageData, String filename) {
        return new ByteArrayResource(imageData) {
            @Override
            public String getFilename() {
                return filename;
            }
        };
    }
}
