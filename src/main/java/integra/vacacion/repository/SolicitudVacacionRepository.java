package integra.vacacion.repository;

import integra.vacacion.dto.response.CalendarioEquipoDTO;
import integra.vacacion.dto.response.SolicitudVacacionDTO;
import integra.vacacion.entity.SolicitudVacacionEntity;
import integra.vacacion.entity.SolicitudVacacionEntity.EstatusSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SolicitudVacacionRepository extends JpaRepository<SolicitudVacacionEntity, Long> {

    List<SolicitudVacacionEntity> findByEmpleadoIdOrderByCreatedAtDesc(Integer empleadoId);

    List<SolicitudVacacionEntity> findByEmpleadoIdAndEstatus(Integer empleadoId, EstatusSolicitud estatus);

    @Query("SELECT s FROM SolicitudVacacionEntity s WHERE s.estatus = :estatus ORDER BY s.createdAt ASC")
    List<SolicitudVacacionEntity> findByEstatus(@Param("estatus") EstatusSolicitud estatus);

    @Query("SELECT s FROM SolicitudVacacionEntity s WHERE s.empleadoId = :empleadoId AND s.fechaInicio <= :fin AND s.fechaFin >= :inicio AND s.estatus IN :estatus")
    List<SolicitudVacacionEntity> findSolicitudesActivasEnRango(
            @Param("empleadoId") Integer empleadoId,
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin,
            @Param("estatus") List<EstatusSolicitud> estatus);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM SolicitudVacacionEntity s " +
            "WHERE s.empleadoId = :empleadoId AND s.estatus = :estatus AND " +
            "s.fechaFin >= :fechaReferencia")
    boolean tieneSolicitudesPendientesOActivas(
            @Param("empleadoId") Integer empleadoId,
            @Param("estatus") EstatusSolicitud estatus,
            @Param("fechaReferencia") LocalDate fechaReferencia);

    @Query("SELECT s FROM SolicitudVacacionEntity s WHERE s.estatus = 'PENDIENTE' AND " +
            "(s.fechaInicio BETWEEN :inicio AND :fin OR s.fechaFin BETWEEN :inicio AND :fin)")
    List<SolicitudVacacionEntity> findSolicitudesEnRangoFechas(
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin);

    @Query("SELECT s FROM SolicitudVacacionEntity s WHERE s.empleadoId = :gestorId AND s.estatus = 'PENDIENTE'")
    List<SolicitudVacacionEntity> findSolicitudesParaAprobar(@Param("gestorId") Integer gestorId);

    @Query("""
                SELECT new integra.vacacion.dto.response.CalendarioEquipoDTO(
                    s.empleadoId, 
                    e.nombreCompleto, 
                    d.nombre, 
                    s.fechaInicio, 
                    s.fechaFin, 
                    s.diasSolicitados, 
                    CAST(s.estatus AS string)
                ) 
                FROM SolicitudVacacionEntity s 
                JOIN EmpleadoEntity e ON s.empleadoId = e.id 
                LEFT JOIN e.departamento d 
                WHERE d.id = :departamentoId 
                AND s.estatus = 'APROBADA'
            """)
    List<CalendarioEquipoDTO> findCalendarioPorDepartamento(@Param("departamentoId") Integer departamentoId);

    @Query("""
                SELECT new integra.vacacion.dto.response.SolicitudVacacionDTO(
                    s.id, s.empleadoId, e.nombreCompleto, 
                    COALESCE(d.nombre, ''), COALESCE(p.nombre, ''), 
                    s.fechaInicio, s.fechaFin, s.diasSolicitados, 
                    s.motivo, CAST(s.estatus AS string), 
                    s.comentariosAprobador, s.aprobadorId, 
                    COALESCE(a.nombreCompleto, ''), 
                    s.fechaAprobacion, s.createdAt
                )
                FROM SolicitudVacacionEntity s
                JOIN EmpleadoEntity e ON s.empleadoId = e.id
                LEFT JOIN e.departamento d
                LEFT JOIN e.puesto p
                LEFT JOIN EmpleadoEntity a ON s.aprobadorId = a.id
                WHERE s.empleadoId IN (
                    SELECT v.empleadoId FROM VinculacionGestionEntity v 
                    WHERE v.gestor.id = :gestorId 
                    AND v.tipoProceso.nombre = 'VACACIONES' 
                    AND v.activo = true
                ) AND s.estatus = 'PENDIENTE'
            """)
    List<SolicitudVacacionDTO> findSolicitudesPendientesPorGestorDirecto(@Param("gestorId") Integer gestorId);
}
