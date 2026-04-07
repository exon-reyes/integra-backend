package integra.vacacion.repository;

import integra.vacacion.core.EstatusPeriodo;
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
}
