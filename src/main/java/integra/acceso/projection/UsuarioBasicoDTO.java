package integra.acceso.projection;

public record UsuarioBasicoDTO(
        Long id,
        String username,
        Integer empleadoId,
        String email,
        String nombreCompleto,
        Boolean activo,
        String roles,
        String departamento,
        String puesto
) {
}