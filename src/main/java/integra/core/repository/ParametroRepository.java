package integra.core.repository;

import integra.core.entity.ParametrosAppEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ParametroRepository extends JpaRepository<ParametrosAppEntity, Integer> {
    @Transactional
    @Modifying
    @Query("update ParametrosAppEntity p set p.valor = ?1 where p.id = ?2")
    int updateValorById(String valor, Integer id);

}