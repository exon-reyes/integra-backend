package integra.asistencia.actions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO con el resumen de días laborados y no laborados de un empleado en un mes.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResumenMesAsistencia {
    private int diasLaborados;
    private int diasNoLaborados;
    private String nombreMes;
    private int anio;
}
