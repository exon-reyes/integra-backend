package integra.vacacion.repository;

import integra.proceso.entity.VinculacionGestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VinculacionGestionRepository extends JpaRepository<VinculacionGestionEntity, Integer> {

    @Query("SELECT v FROM VinculacionGestionEntity v WHERE v.empleadoId = :empleadoId " +
            "AND v.tipoProceso.nombre = 'VACACIONES' AND v.activo = true ORDER BY v.nivelAutoridad ASC")
    List<VinculacionGestionEntity> findGestoresVacaciones(@Param("empleadoId") Integer empleadoId);

    @Query("SELECT v FROM VinculacionGestionEntity v WHERE v.gestor.id = :gestorId " +
            "AND v.tipoProceso.nombre = 'VACACIONES' AND v.activo = true")
    List<VinculacionGestionEntity> findEmpleadosPorGestor(@Param("gestorId") Integer gestorId);

    @Query("SELECT v FROM VinculacionGestionEntity v WHERE v.empleadoId = :empleadoId " +
            "AND v.tipoProceso.nombre = 'VACACIONES' AND v.activo = true " +
            "AND v.nivelAutoridad = :nivel")
    Optional<VinculacionGestionEntity> findGestorPorNivel(
            @Param("empleadoId") Integer empleadoId,
            @Param("nivel") Integer nivel);
}
