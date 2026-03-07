package integra.vacacion.repository;

import integra.vacacion.entity.PeriodoVedaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PeriodoVedaRepository extends JpaRepository<PeriodoVedaEntity, Long> {

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM PeriodoVedaEntity p " +
            "WHERE p.activo = true AND p.fechaInicio <= :fin AND p.fechaFin >= :inicio")
    boolean existsActivoBetween(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    List<PeriodoVedaEntity> findByActivoTrueOrderByFechaInicioDesc();

    @Query("SELECT p FROM PeriodoVedaEntity p WHERE p.activo = true AND p.fechaFin >= :fecha ORDER BY p.fechaInicio ASC")
    List<PeriodoVedaEntity> findVedasActivasDesde(@Param("fecha") LocalDate fecha);
}
