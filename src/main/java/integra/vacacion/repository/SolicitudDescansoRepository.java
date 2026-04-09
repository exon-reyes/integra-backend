package integra.vacacion.repository;

import integra.vacacion.entity.SolicitudDescanso;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SolicitudDescansoRepository extends JpaRepository<SolicitudDescanso, Long>, JpaSpecificationExecutor<SolicitudDescanso> {

    @EntityGraph(attributePaths = {"empleado.puesto", "empleado.unidad", "periodo", "diasSolicitudDescansos"})
    Optional<SolicitudDescanso> findByFolioSolicitud(Long folioSolicitud);

    @Query("""
            SELECT DISTINCT s FROM SolicitudDescanso s
            JOIN FETCH s.diasSolicitudDescansos d
            WHERE s.empleado.id = :empleadoId
              AND d.fecha BETWEEN :desde AND :hasta
            ORDER BY d.fecha ASC
            """)
    List<SolicitudDescanso> obtenerSolicitudesPorAnio(@Param("empleadoId") Integer empleadoId, @Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);

    @Query("SELECT DISTINCT s FROM SolicitudDescanso s JOIN FETCH s.diasSolicitudDescansos d WHERE s.empleado.id = :empleadoId AND d.fecha >= :hoy  ORDER BY d.fecha ASC")
    List<SolicitudDescanso> findProximasSolicitudesPorEmpleado(@Param("empleadoId") Integer empleadoId, @Param("hoy") LocalDate hoy);
}
