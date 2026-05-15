package integra.empleado.dto;

public record ActualizarAvatarRequest(
        String avatarName,
        String base64Image) {
}
