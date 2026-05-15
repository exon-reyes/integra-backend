package integra.vacacion.repository;

import integra.vacacion.entity.HistorialSolicitudDescanso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistorialSolicitudRepository extends JpaRepository<HistorialSolicitudDescanso, Long> {
}