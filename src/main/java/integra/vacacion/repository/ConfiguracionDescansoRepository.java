package integra.vacacion.repository;

import integra.vacacion.entity.ConfiguracionDescansoEmpleadoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ConfiguracionDescansoRepository extends JpaRepository<ConfiguracionDescansoEmpleadoEntity, Long> {

    @Query("SELECT c.diaDescanso FROM ConfiguracionDescansoEmpleadoEntity c WHERE c.empleadoId = :empleadoId AND c.activo = true")
    Set<Integer> findDiasDescansoByEmpleado(@Param("empleadoId") Integer empleadoId);

    List<ConfiguracionDescansoEmpleadoEntity> findByEmpleadoIdAndActivoTrue(Integer empleadoId);
}
