package integra.vacacion.repository;

import integra.vacacion.entity.DescansoEmpleadoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Repository
public interface DescansoEmpleadoRepository extends JpaRepository<DescansoEmpleadoEntity, Long> {

    @Query("SELECT d.fechaDescanso FROM DescansoEmpleadoEntity d WHERE d.empleadoId = :empleadoId AND d.activo = true")
    Set<LocalDate> findFechasDescansoByEmpleado(@Param("empleadoId") Integer empleadoId);

    List<DescansoEmpleadoEntity> findByEmpleadoIdAndActivoTrue(Integer empleadoId);

    @Query("SELECT d FROM DescansoEmpleadoEntity d WHERE d.empleadoId = :empleadoId " +
            "AND d.fechaDescanso BETWEEN :inicio AND :fin AND d.activo = true")
    List<DescansoEmpleadoEntity> findDescansosEnRango(
            @Param("empleadoId") Integer empleadoId,
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin
    );

    boolean existsByEmpleadoIdAndActivoTrue(Integer empleadoId);
}
