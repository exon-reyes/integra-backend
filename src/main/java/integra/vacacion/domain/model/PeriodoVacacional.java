//package integra.vacacion.domain.model;
//
//import java.time.LocalDate;
//
//public class PeriodoVacacional {
//
//    private Long id;
//
//    private Integer empleadoId;
//
//    private int periodoNumero;
//
//    private LocalDate fechaInicio;
//
//    private LocalDate fechaFin;
//
//    private int diasAsignados;
//
//    private int diasTomados;
//
//    private LocalDate fechaCaducidad;
//
//    private EstatusSolicitud estado;
//
//    public int getDiasRestantes() {
//        return diasAsignados - diasTomados;
//    }
//
//    public boolean estaVigente() {
//        return estado == EstatusSolicitud.VIGENTE;
//    }
//
//    public void registrarUsoDias(int dias) {
//        if (dias > getDiasRestantes()) {
//            throw new IllegalStateException("No hay suficientes días disponibles");
//        }
//
//        this.diasTomados += dias;
//    }
//}