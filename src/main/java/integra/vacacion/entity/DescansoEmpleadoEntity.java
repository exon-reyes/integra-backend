//package integra.vacacion.entity;
//
//import integra.vacacion.domain.model.EstatusSolicitud;
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.Setter;
//import org.hibernate.annotations.ColumnDefault;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//
//@Getter
//@Setter
//@Entity
//@Table(name = "descansos_empleado", schema = "integra")
//public class DescansoEmpleadoEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(name = "empleado_id", nullable = false)
//    private Integer empleadoId;
//
//    @Column(name = "fecha_descanso", nullable = false)
//    private LocalDate fechaDescanso;
//
//    @Column(name = "comentario", length = 100)
//    private String comentario;
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "estatus", nullable = false, length = 20)
//    private EstatusSolicitud estatus = EstatusSolicitud.PENDIENTE;
//
//    @Column(name = "aprobador_id")
//    private Integer aprobadorId;
//
//    @Column(name = "fecha_aprobacion")
//    private LocalDateTime fechaAprobacion;
//
//    @Column(name = "comentarios_aprobador", columnDefinition = "TEXT")
//    private String comentariosAprobador;
//
//    @Column(name = "activo")
//    private Boolean activo = true;
//
//    @ColumnDefault("current_timestamp()")
//    @Column(name = "created_at")
//    private LocalDateTime createdAt;
//    @Column(name = "periodo_id")
//    private Long periodoId;
//
//    @PrePersist
//    protected void onCreate() {
//        createdAt = LocalDateTime.now();
//    }
//}
