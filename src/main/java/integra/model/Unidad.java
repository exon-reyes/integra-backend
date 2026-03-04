package integra.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class Unidad {
    private Integer id;
    private String clave;
    private String nombre;
    private String nombreCompleto;
    private Boolean activo;
    private Boolean operativa;
    private Contacto contacto;
    private Boolean requiereCamara;
    private String codigoAutorizacionKiosco;
    private Boolean requiereReset;
    private Integer versionKiosco;
    private Empleado supervisor;
    private LocalTime tiempoCompensacion;
    private Integer tiempoEsperaKiosco;

    public Unidad(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public Unidad(Integer id, String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
        this.id = id;
    }

    public Unidad(Integer id, String nombreCompleto, Boolean requiereCamara, String codigoAutorizacionKiosco, Boolean requiereReset, Integer versionKiosco, LocalTime tiempoCompensacion) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.requiereCamara = requiereCamara;
        this.versionKiosco = versionKiosco;
        this.codigoAutorizacionKiosco = codigoAutorizacionKiosco;
        this.requiereReset = requiereReset;
        this.tiempoCompensacion = tiempoCompensacion;
    }

    public Unidad(String clave, String nombre) {
        this.clave = clave;
        this.nombre = nombre;
    }

    /**
     * Información básica de la unidad
     *
     * @param id             autoincremental
     * @param clave          unica de la unidad
     * @param nombreCompleto de la unidad
     */
    public Unidad(Integer id, String clave, String nombreCompleto) {
        this.id = id;
        this.clave = clave;
        this.nombreCompleto = nombreCompleto;
    }

    public Unidad(Integer id, String clave, String nombre, Contacto contacto) {
        this.id = id;
        this.clave = clave;
        this.nombre = nombre;
        this.contacto = contacto;
    }

    public Unidad(Integer id, String clave, String nombre, Boolean estatus, Zona zona, Empleado supervisor) {
        this.id = id;
        this.clave = clave;
        this.nombre = nombre;
        this.activo = estatus;
        this.contacto = new Contacto(zona);
        this.supervisor = supervisor;
    }

    public Unidad(Integer id, String clave, String nombre, boolean activo, Contacto contacto) {
        this.id = id;
        this.clave = clave;
        this.nombre = nombre;
        this.contacto = contacto;
        this.activo = activo;
    }

    public Unidad(Integer id, String clave, String nombre, Boolean activo, Contacto contacto) {
        this.id = id;
        this.clave = clave;
        this.nombre = nombre;
        this.activo = activo;
        this.contacto = contacto;
    }

    public Unidad(Integer id, String clave, String nombre, Boolean activo, Contacto contacto, Empleado supervisor) {
        this.id = id;
        this.clave = clave;
        this.nombre = nombre;
        this.activo = activo;
        this.contacto = contacto;
        this.supervisor = supervisor;
    }

}
