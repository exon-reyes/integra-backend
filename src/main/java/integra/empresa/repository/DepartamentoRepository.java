package integra.empresa.repository;

import integra.empresa.entity.DepartamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartamentoRepository extends JpaRepository<DepartamentoEntity, Integer> {
    <T> List<T> findBy(Class<T> type);
}