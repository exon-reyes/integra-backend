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


    Optional<PeriodoVacacionalEntity> findPeriodoVacacionalEntityByEmpleadoIdAndEstatus(Integer empleadoId, EstatusPeriodo estatus);

    @Transactional
    @Modifying
    @Query("update PeriodoVacacionalEntity p set p.diasRestantes = ?1 where p.id = ?2")
    void actualizarDisponibilidadad(Integer diasRestantes, Long id);

    List<PeriodoVacacionalEntity> findByEstatus(EstatusPeriodo estatus);


    boolean existsByEmpleadoIdAndAnioLaboral(Integer empleadoId, Integer anioLaboral);
}
