package integra.vacacion.repository;

import integra.vacacion.domain.model.EstatusSolicitud;
import integra.vacacion.dto.request.FiltroSolicitud;
import integra.vacacion.entity.SolicitudDescanso;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Especificaciones dinámicas para consultar SolicitudDescanso.
 * Cada predicado se agrega sólo si el campo del filtro tiene valor.
 */
public class SolicitudDescansoSpecification {

    private SolicitudDescansoSpecification() {}

    public static Specification<SolicitudDescanso> withFilter(FiltroSolicitud filtro) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtro por estatus general
            if (filtro.getEstatus() != null && !filtro.getEstatus().isBlank()) {
                predicates.add(cb.equal(
                        root.get("estatus"),
                        EstatusSolicitud.valueOf(filtro.getEstatus())
                ));
            }

            // Filtro por jefe (nivel 1) o RRHH (nivel 2): se usa un único join al empleado
            boolean filtrarPorJefe  = filtro.getJefeId()  != null;
            boolean filtrarPorRrhh  = filtro.getRrhhId()  != null;

            if (filtrarPorJefe || filtrarPorRrhh) {
                Join<Object, Object> empleado = root.join("empleado", JoinType.INNER);

                if (filtrarPorJefe) {
                    predicates.add(cb.equal(
                            empleado.get("jefe").get("id"),
                            filtro.getJefeId()
                    ));
                }

                if (filtrarPorRrhh) {
                    predicates.add(cb.equal(
                            empleado.get("segundoJefe").get("id"),
                            filtro.getRrhhId()
                    ));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
