package integra.operatividad.repository;

import integra.empresa.entity.OperatividadEntity;
import integra.model.HorarioOperativo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperatividadEntityRepository extends JpaRepository<OperatividadEntity, Integer> {
}