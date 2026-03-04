package integra.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Empleado {
    private Integer id;
    private String codigo;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String nombreCompleto;
    private Puesto puesto;
    private Unidad unidad;
    private LocalDate fechaAlta;
    private LocalDate fechaBaja;
    private LocalDate fechaReingreso;
    private String estatus;
    private Departamento departamento;
    private Contacto contacto;
    private String sexo;
    private List<Gestor> gestores;

    public Empleado(Integer id, String codigo, String nombreCompleto) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.codigo = codigo;
    }

    public Empleado(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public Empleado(Integer id, String nombreCompleto) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
    }

    public Empleado(Integer id, String codigo, String nombre, String apellidoPaterno, String apellidoMaterno) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
    }

    public void setNombre(String nombre, String apellidoPaterno, String apellidoMaterno) {
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
    }

    public void setGestor(Gestor gestor) {
        if (this.gestores == null)
            this.gestores = new ArrayList<>();
        this.gestores.add(gestor);
    }

    public void setAreaTrabajo(Integer idPuesto, String puesto, Integer idDep, String departamento) {
        this.puesto = new Puesto(idPuesto, puesto);
        this.departamento = new Departamento(idDep, departamento);

    }
}
