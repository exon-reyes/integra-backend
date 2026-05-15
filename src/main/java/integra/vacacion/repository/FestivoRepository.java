package integra.vacacion.repository;

import integra.vacacion.entity.FestivoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FestivoRepository extends JpaRepository<FestivoEntity, Long> {

    @Query("SELECT f FROM FestivoEntity f WHERE f.fecha BETWEEN :inicio AND :fin")
    List<FestivoEntity> findFestivosBetween(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);
}
