package integra.asistencia.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "unidad", schema = "integra", indexes = {
        @Index(name = "idx_unidad_activo",
                columnList = "activo"),
        @Index(name = "idx_unidad_observaciones_pendientes",
                columnList = "observaciones_pendientes")}, uniqueConstraints = {
        @UniqueConstraint(name = "idx_clave_unidad",
                columnNames = {"clave"}),
        @UniqueConstraint(name = "idx_nombre_unidad",
                columnNames = {"nombre"}),
        @UniqueConstraint(name = "idx_telefono",
                columnNames = {"telefono"}),
        @UniqueConstraint(name = "idx_unidad_email",
                columnNames = {"email"}),
        @UniqueConstraint(name = "idx_codigo_autorizacion_kiosco",
                columnNames = {"codigo_autorizacion_kiosco"})})
public class Unidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 10)
    @Column(name = "clave", length = 10)
    private String clave;

    @Size(max = 100)
    @NotNull
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Size(max = 80)
    @Column(name = "localizacion", length = 80)
    private String localizacion;

    @Size(max = 15)
    @Column(name = "telefono", length = 15)
    private String telefono;

    @ColumnDefault("1")
    @Column(name = "activo")
    private Boolean activo;

    @Size(max = 255)
    @ColumnDefault("concat(`clave`, ' ', `nombre`)")
    @Column(name = "nombre_completo")
    private String nombreCompleto;

    @Size(max = 255)
    @Column(name = "direccion")
    private String direccion;

    @Size(max = 100)
    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "ultima_auditoria")
    private Instant ultimaAuditoria;

    @Column(name = "proxima_auditoria")
    private Instant proximaAuditoria;

    @ColumnDefault("0")
    @Column(name = "observaciones_pendientes")
    private Integer observacionesPendientes;

    @Column(name = "creado")
    private LocalDate creado;

    @Column(name = "actualizado")
    private Instant actualizado;

    @ColumnDefault("1")
    @Column(name = "requiere_camara")
    private Boolean requiereCamara;

    @Size(max = 5)
    @Column(name = "codigo_autorizacion_kiosco", length = 5)
    private String codigoAutorizacionKiosco;

    @Column(name = "requiere_codigo")
    private Boolean requiereCodigo;

    @ColumnDefault("1")
    @Column(name = "version_kiosco")
    private Integer versionKiosco;

    @Column(name = "tiempo_compensacion")
    private LocalTime tiempoCompensacion;

    @Column(name = "tiempo_espera_kiosco")
    private Integer tiempoEsperaKiosco;


}