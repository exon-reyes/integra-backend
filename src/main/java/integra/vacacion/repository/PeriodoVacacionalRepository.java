package integra.vacacion.repository;

import integra.vacacion.entity.PeriodoVacacionalEntity;
import integra.vacacion.entity.PeriodoVacacionalEntity.EstatusPeriodo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PeriodoVacacionalRepository extends JpaRepository<PeriodoVacacionalEntity, Long> {

    List<PeriodoVacacionalEntity> findByEmpleadoIdOrderByAnioLaboralDesc(Integer empleadoId);

    List<PeriodoVacacionalEntity> findByEmpleadoId(Integer empleadoId);

    List<PeriodoVacacionalEntity> findByEmpleadoIdAndEstatus(Integer empleadoId, EstatusPeriodo estatus);

    @Query("SELECT COALESCE(SUM(p.diasRestantes), 0) FROM PeriodoVacacionalEntity p " +
            "WHERE p.empleadoId = :empleadoId AND p.estatus = 'VIGENTE'")
    Integer sumDiasRestantesByEmpleado(@Param("empleadoId") Integer empleadoId);

    @Query("SELECT p FROM PeriodoVacacionalEntity p WHERE p.empleadoId = :empleadoId " +
            "AND p.estatus = 'VIGENTE' AND p.diasRestantes > 0 ORDER BY p.fechaCaducidad ASC")
    List<PeriodoVacacionalEntity> findPeriodosDisponiblesOrdenados(@Param("empleadoId") Integer empleadoId);

    @Query("SELECT p FROM PeriodoVacacionalEntity p WHERE p.empleadoId = :empleadoId " +
            "AND p.fechaCaducidad BETWEEN :inicio AND :fin AND p.estatus = 'VIGENTE'")
    List<PeriodoVacacionalEntity> findPeriodosProximosAVencer(
            @Param("empleadoId") Integer empleadoId,
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin);

    @Query("SELECT p FROM PeriodoVacacionalEntity p WHERE p.fechaCaducidad < :fecha " +
            "AND p.estatus = 'VIGENTE'")
    List<PeriodoVacacionalEntity> findPeriodosVencidos(@Param("fecha") LocalDate fecha);

    Optional<PeriodoVacacionalEntity> findByEmpleadoIdAndAnioLaboral(Integer empleadoId, Integer anioLaboral);

    boolean existsByEmpleadoIdAndAnioLaboral(Integer empleadoId, Integer anioLaboral);
}
