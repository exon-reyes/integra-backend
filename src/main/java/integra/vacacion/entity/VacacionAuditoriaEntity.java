//package integra.vacacion.entity;
//
//import integra.vacacion.domain.model.EstatusSolicitud;
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.time.LocalDateTime;
//
//@Getter
//@Setter
//@Entity
//@Table(name = "vacaciones_auditoria", schema = "integra")
//public class VacacionAuditoriaEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id", nullable = false)
//    private Long id;
//
//    @Column(name = "solicitud_id", nullable = false)
//    private Long solicitudId;
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "accion", nullable = false, length = 50)
//    private EstatusSolicitud accion;
//
//    @Column(name = "usuario_id", nullable = false)
//    private Integer usuarioId;
//
//    @Column(name = "detalles", columnDefinition = "TEXT")
//    private String detalles;
//
//    @Column(name = "created_at")
//    private LocalDateTime createdAt;
//
//    @PrePersist
//    protected void onCreate() {
//        createdAt = LocalDateTime.now();
//    }
//
//
//}
