package integra.asistencia.repository;

import integra.asistencia.entity.CruceEmpleadoKiosco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CruceEmpleadoKioscoRepository extends JpaRepository<CruceEmpleadoKiosco, Integer>, JpaSpecificationExecutor<CruceEmpleadoKiosco> {
}