package integra.vacacion.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public final class FechaSolicitud {
    private  Long id;
    private  LocalDate fecha;
    private  String estatus;
    private String estatusPrimerJefe;
    private String estatusSegundoJefe;
    private String comentario;

    public FechaSolicitud(Long id, LocalDate fecha, String estatus) {
        this.id = id;
        this.fecha = fecha;
        this.estatus = estatus;
    }

    public FechaSolicitud(Long id, LocalDate fecha, String estatus, String estatusPrimerJefe, String estatusSegundoJefe, String comentario) {
        this.id = id;
        this.fecha = fecha;
        this.estatus = estatus;
        this.estatusPrimerJefe = estatusPrimerJefe;
        this.estatusSegundoJefe = estatusSegundoJefe;
        this.comentario = comentario;
    }
}
