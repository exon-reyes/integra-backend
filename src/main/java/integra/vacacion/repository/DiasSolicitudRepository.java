package integra.vacacion.repository;

import integra.vacacion.entity.DiasSolicitudDescanso;
import integra.vacacion.query.DiaSolicitudProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DiasSolicitudRepository extends JpaRepository<DiasSolicitudDescanso, Long> {

    @Query(value = """
            SELECT d.id, d.folio_id AS folioId, d.fecha, d.estatus_nivel1 AS estatusNivel1, d.estatus_nivel2 AS estatusNivel2
            FROM dias_solicitud_descanso d
            WHERE d.folio_id IN :ids
            ORDER BY d.fecha ASC
            """, nativeQuery = true)
    List<DiaSolicitudProjection> findDiasBySolicitudIds(@Param("ids") List<Long> ids);

}