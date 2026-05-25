package integra.vacacion.service.gestion;

import integra.empleado.entity.EmpleadoEntity;
import integra.empresa.entity.PuestoEntity;
import integra.empresa.entity.UnidadEntity;
import integra.vacacion.core.EstatusPeriodo;
import integra.vacacion.dto.request.FiltroPeriodo;
import integra.vacacion.dto.response.PeriodoVacacionalResumen;
import integra.vacacion.entity.PeriodoVacacionalEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ObtenerPeriodos {

    @PersistenceContext
    private EntityManager em;

    public Page<PeriodoVacacionalResumen> consultar(FiltroPeriodo filtro) {
        Pageable pageable = PageRequest.of(filtro.getCurrentPage(), filtro.getPageSize());

        long total = contar(filtro);
        if (total == 0) return new PageImpl<>(List.of(), pageable, 0);

        List<PeriodoVacacionalResumen> resultados = buscar(filtro, pageable);
        return new PageImpl<>(resultados, pageable, total);
    }

    private long contar(FiltroPeriodo filtro) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<PeriodoVacacionalEntity> root = query.from(PeriodoVacacionalEntity.class);

        query.select(cb.count(root))
                .where(buildPredicates(cb, root, filtro).toArray(new Predicate[0]));

        return em.createQuery(query).getSingleResult();
    }

    private List<PeriodoVacacionalResumen> buscar(FiltroPeriodo filtro, Pageable pageable) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<PeriodoVacacionalResumen> query = cb.createQuery(PeriodoVacacionalResumen.class);
        Root<PeriodoVacacionalEntity> root = query.from(PeriodoVacacionalEntity.class);

        Join<PeriodoVacacionalEntity, EmpleadoEntity> emp = root.join("empleado", JoinType.INNER);
        Join<EmpleadoEntity, PuestoEntity> puesto = emp.join("puesto", JoinType.LEFT);
        Join<EmpleadoEntity, UnidadEntity> unidad = emp.join("unidad", JoinType.LEFT);

        query.select(cb.construct(PeriodoVacacionalResumen.class,
                root.get("id"),
                emp.get("codigoEmpleado"),
                emp.get("nombreCompleto"),
                puesto.get("nombre"),
                unidad.get("nombreCompleto"),
                root.get("anioLaboral"),
                root.get("fechaInicio"),
                root.get("fechaFin"),
                root.get("diasHabilitados"),
                root.get("diasTomados"),
                root.get("diasRestantes"),
                root.get("estatus")
        ));

        query.where(buildPredicates(cb, root, filtro).toArray(new Predicate[0]));
        query.orderBy(cb.desc(root.get("anioLaboral")), cb.asc(emp.get("nombreCompleto")));

        return em.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .setHint("org.hibernate.readOnly", true)
                .getResultList();
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb,
                                            Root<PeriodoVacacionalEntity> root,
                                            FiltroPeriodo filtro) {
        List<Predicate> predicates = new ArrayList<>();
        if (filtro == null) return predicates;

        if (filtro.getEstatus() != null && !filtro.getEstatus().isBlank()) {
            try {
                predicates.add(cb.equal(root.get("estatus"), EstatusPeriodo.valueOf(filtro.getEstatus())));
            } catch (IllegalArgumentException ignored) {
            }
        }

        if (filtro.getAnioLaboral() != null) {
            predicates.add(cb.equal(root.get("anioLaboral"), filtro.getAnioLaboral()));
        }

        if (filtro.getEmpleadoId() != null || filtro.getUnidadId() != null || filtro.getSupervisorId() != null || filtro.getResponsableId() != null) {
            Join<PeriodoVacacionalEntity, EmpleadoEntity> emp = root.join("empleado");

            if (filtro.getEmpleadoId() != null) {
                predicates.add(cb.equal(emp.get("id"), filtro.getEmpleadoId()));
            }
            if (filtro.getUnidadId() != null) {
                predicates.add(cb.equal(emp.get("unidad").get("id"), filtro.getUnidadId()));
            }
            if (filtro.getSupervisorId() != null) {
                Join<EmpleadoEntity, UnidadEntity> unidad = emp.join("unidad", JoinType.INNER);
                predicates.add(cb.equal(unidad.get("supervisor").get("id"), filtro.getSupervisorId()));
            }
            if (filtro.getResponsableId() != null) {
                predicates.add(cb.equal(emp.get("jefe").get("id"), filtro.getResponsableId()));
            }
        }

        return predicates;
    }
}
