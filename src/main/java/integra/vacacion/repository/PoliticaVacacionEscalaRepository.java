package integra.vacacion.repository;

import integra.vacacion.entity.PoliticaVacacionEscalaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface PoliticaVacacionEscalaRepository extends JpaRepository<PoliticaVacacionEscalaEntity, Long> {

    Optional<PoliticaVacacionEscalaEntity> findByActivaTrue();

    @Query("""
            SELECT p FROM PoliticaVacacionEscalaEntity p
            WHERE p.fechaVigenciaInicio <= :fechaAlta
            AND (p.fechaVigenciaFin IS NULL OR p.fechaVigenciaFin >= :fechaAlta)
            ORDER BY p.fechaVigenciaInicio DESC
            LIMIT 1
            """)
    Optional<PoliticaVacacionEscalaEntity> findPoliticaVigenteParaFecha(@Param("fechaAlta") LocalDate fechaAlta);
}
