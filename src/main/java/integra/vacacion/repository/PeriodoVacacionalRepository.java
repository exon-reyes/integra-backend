package integra.vacacion.repository;

import integra.vacacion.core.EstatusPeriodo;
import integra.vacacion.dto.response.ReportePeriodoVacacionalProjection;
import integra.vacacion.entity.PeriodoVacacionalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PeriodoVacacionalRepository extends JpaRepository<PeriodoVacacionalEntity, Long> {


    List<PeriodoVacacionalEntity> findByEmpleadoId(Integer empleadoId);


    @Query("SELECT p FROM PeriodoVacacionalEntity p WHERE p.fechaCaducidad < :fecha " + "AND p.estatus = 'VIGENTE'")
    List<PeriodoVacacionalEntity> findPeriodosVencidos(@Param("fecha") LocalDate fecha);


    @Query("select p from PeriodoVacacionalEntity p where p.empleado.id = ?1 and p.estatus = ?2")
    Optional<PeriodoVacacionalEntity> obtenerPeriodo(Integer empleadoId, EstatusPeriodo estatus);

    @Query(value = """
            SELECT p.* FROM periodos_vacacionales p
            WHERE p.empleado_id = :empleadoId
            ORDER BY FIELD(p.estatus, 'VIGENTE', 'CONSUMIDO', 'VENCIDO'), p.fecha_fin DESC
            LIMIT 1
            """, nativeQuery = true)
    Optional<PeriodoVacacionalEntity> obtenerMejorPeriodo(@Param("empleadoId") Integer empleadoId);


    @Transactional
    @Modifying
    @Query("update PeriodoVacacionalEntity p set p.diasRestantes = ?1 where p.id = ?2")
    void actualizarDisponibilidadad(Integer diasRestantes, Long id);

    List<PeriodoVacacionalEntity> findByEstatus(EstatusPeriodo estatus);


    boolean existsByEmpleadoIdAndAnioLaboral(Integer empleadoId, Integer anioLaboral);

    @Query(value = """
            SELECT 
                e.codigo_empleado as clave,
                e.nombre_completo as colaborador,
                e.estatus as estatus,
                u.nombre_completo as unidadAsociada,
                e.fecha_alta as fechaIngreso,
                pu.nombre as puesto,
                j.nombre_completo as responsable,
                sj.nombre_completo as responsableNivel2,
                p.anio_laboral as anioLaboral,
                p.fecha_inicio as fechaInicio,
                p.fecha_fin as fechaFin,
                p.fecha_caducidad as fechaCaducidad,
                p.dias_habilitados as habilitadas,
                p.dias_tomados as tomadas,
                p.dias_restantes as restantes,
                p.estatus as estatusPeriodo,
                p.anio_gestion as anioGestion
            FROM (
                SELECT p2.*,
                       ROW_NUMBER() OVER (
                           PARTITION BY p2.empleado_id
                           ORDER BY FIELD(p2.estatus, 'VIGENTE', 'CONSUMIDO', 'VENCIDO') ASC, p2.fecha_fin DESC
                       ) as rn
                FROM integra.periodos_vacacionales p2
            ) p
            JOIN integra.empleado e ON e.id = p.empleado_id
            LEFT JOIN integra.unidad u ON u.id = e.unidad_id
            LEFT JOIN integra.puesto pu ON pu.id = e.puesto_id
            LEFT JOIN integra.empleado j ON j.id = e.jefe_id
            LEFT JOIN integra.empleado sj ON sj.id = e.segundo_jefe_id
            WHERE p.rn = 1 AND e.estatus = 'A'
            """, nativeQuery = true)
    List<ReportePeriodoVacacionalProjection> obtenerReporteMejoresPeriodos();
}
