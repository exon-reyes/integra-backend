package integra.asistencia.specification;

import integra.asistencia.actions.EmpleadoReporteCommand;
import integra.asistencia.entity.AsistenciaModel;
import integra.empleado.entity.EmpleadoEntity;
import integra.empresa.entity.PuestoEntity;
import integra.empresa.entity.UnidadEntity;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AsistenciaSpecification {

    public static Specification<AsistenciaModel> findByCriteriaWithFetch(
            final EmpleadoReporteCommand request) {

        return (root, query, cb) -> {

            root.fetch("empleado", JoinType.LEFT).fetch("puesto", JoinType.LEFT);
            root.fetch("empleado", JoinType.LEFT).fetch("unidad", JoinType.LEFT);
            root.fetch("pausas", JoinType.LEFT);

            query.distinct(true); // 🔥 importante

            List<Predicate> predicates = buildPredicates(root, cb, request);
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static List<Predicate> buildPredicates(
            Root<AsistenciaModel> root,
            CriteriaBuilder cb,
            EmpleadoReporteCommand request) {

        List<Predicate> predicates = new ArrayList<>();

        Join<AsistenciaModel, EmpleadoEntity> empleadoJoin =
                root.join("empleado");

        Join<EmpleadoEntity, PuestoEntity> puestoJoin =
                empleadoJoin.join("puesto");

        // -------------------------
        // Empleado directo
        // -------------------------
        if (request.getEmpleadoId() != null) {
            predicates.add(
                    cb.equal(empleadoJoin.get("id"),
                            request.getEmpleadoId())
            );
        }

        // -------------------------
        // Empleado por responsable
        // -------------------------
        if (request.getEmpleadoResponsableId() != null) {

            predicates.add(
                    cb.equal(
                            empleadoJoin.get("jefe").get("id"),
                            request.getEmpleadoResponsableId()
                    )
            );
        }

        // -------------------------
        // Unidad
        // -------------------------
        if (request.getUnidadId() != null) {
            predicates.add(
                    cb.equal(
                            empleadoJoin.get("unidad").get("id"),
                            request.getUnidadId()
                    )
            );
        }

        // -------------------------
        // Puesto
        // -------------------------
        if (request.getPuestoId() != null) {
            predicates.add(
                    cb.equal(
                            puestoJoin.get("id"),
                            request.getPuestoId()
                    )
            );
        }

        // -------------------------
        // Supervisor por unidad
        // -------------------------
        if (request.getSupervisorId() != null) {
            Join<EmpleadoEntity, UnidadEntity> unidadJoin =
                    empleadoJoin.join("unidad");

            predicates.add(
                    cb.equal(
                            unidadJoin.get("supervisor").get("id"),
                            request.getSupervisorId()
                    )
            );
        }

        // -------------------------
        // Zona
        // -------------------------
        if (request.getZonaId() != null) {
            Join<EmpleadoEntity, UnidadEntity> unidadJoin =
                    empleadoJoin.join("unidad");

            predicates.add(
                    cb.equal(
                            unidadJoin.get("zona").get("id"),
                            request.getZonaId()
                    )
            );
        }

        // -------------------------
        // Rango fechas
        // -------------------------
        if (request.getDesde() != null) {
            predicates.add(
                    cb.greaterThanOrEqualTo(
                            root.get("inicioJornada"),
                            request.getDesde()
                    )
            );
        }

        if (request.getHasta() != null) {
            LocalDateTime endOfDay =
                    request.getHasta()
                            .toLocalDate()
                            .atTime(23, 59, 59);

            predicates.add(
                    cb.lessThanOrEqualTo(
                            root.get("inicioJornada"),
                            endOfDay
                    )
            );
        }

        return predicates;
    }
}
