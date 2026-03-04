package integra.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class Rol {
    private Long id;
    private String nombre;
    private String descripcion;
    private Boolean activo;
    private Boolean rolDefault;
    private Long version;
    private Set<Permiso> permisos;

    public Rol(Long id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public Rol(Long id) {
        this.id = id;
    }

    public Rol(Long id, String nombre, String descripcion, Boolean activo, Boolean esDefault, Long version) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = activo;
        this.rolDefault = esDefault;
        this.version = version;
    }
}
