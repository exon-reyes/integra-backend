package integra.vacacion.repository;

import integra.vacacion.entity.PoliticaVacacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PoliticaVacacionRepository extends JpaRepository<PoliticaVacacionEntity, Long> {

    List<PoliticaVacacionEntity> findByActivaTrue();

    Optional<PoliticaVacacionEntity> findFirstByActivaTrue();
}
