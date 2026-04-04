package integra.vacacion.repository;

import integra.vacacion.core.EstatusPeriodo;
import integra.vacacion.entity.EmpleadoTiempoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmpleadoTiempoEntityRepository extends JpaRepository<EmpleadoTiempoEntity, Long> {
    List<EmpleadoTiempoEntity> findByEmpleadoIdAndPeriodoId(Integer empleadoId, Long periodoId);

    @Query("""
            SELECT et FROM EmpleadoTiempoEntity et
            JOIN FETCH et.empleado e
            LEFT JOIN FETCH e.unidad
            LEFT JOIN FETCH e.jefe
            LEFT JOIN FETCH e.segundoJefe
            WHERE et.periodo.estatus = :estatus
              AND et.activo = true
            ORDER BY e.nombreCompleto, et.fecha
            """)
    List<EmpleadoTiempoEntity> findByPeriodoEstatusConRelaciones(EstatusPeriodo estatus);

    @Query("""
            SELECT et FROM EmpleadoTiempoEntity et
            JOIN FETCH et.empleado e
            LEFT JOIN FETCH e.unidad
            LEFT JOIN FETCH e.jefe
            LEFT JOIN FETCH e.segundoJefe
            WHERE et.periodo.estatus = :estatus
              AND et.activo = true
            ORDER BY et.createdAt DESC
            LIMIT 5
            """)
    List<EmpleadoTiempoEntity> findTop5ByPeriodoEstatusRecientes(EstatusPeriodo estatus);

    List<EmpleadoTiempoEntity> findAllByFolio(Long folio);
}