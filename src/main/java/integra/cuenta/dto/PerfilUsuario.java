package integra.cuenta.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PerfilUsuario {
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String correo;
    private String telefono;
    private String usuario;
    private Long usuarioId;
    private Integer unidadAsignadaId;
    private String unidadAsignadaNombre;
    private String departamento;
    private String puesto;
    private String area;
    private String estatus;
    private String avatar;
}
