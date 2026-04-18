package integra.empleado;

import integra.empleado.constants.EmpleadoEstatus;
import integra.empleado.entity.EmpleadoEntity;
import integra.empleado.query.EmpleadoAsignacionInfo;
import integra.empleado.query.InfoCatalogoEmpleados;
import integra.empleado.util.FiltroEmpleado;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FiltroEmpleadoService {

    @PersistenceContext
    private EntityManager entityManager;

    private List<Predicate> buildPredicatesActivos(CriteriaBuilder cb, Root<EmpleadoEntity> root, FiltroEmpleado filtros) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.notEqual(root.get("estatus"), EmpleadoEstatus.BAJA));

        if (filtros == null) return predicates;
        if (filtros.getId() != null) {
            predicates.add(cb.equal(root.get("id"), filtros.getId()));
        }
        if (filtros.getClave() != null && !filtros.getClave().isBlank()) {
            predicates.add(cb.equal(root.get("codigoEmpleado"), filtros.getClave().trim()));
        }
        if (filtros.getIdPuesto() != null) {
            predicates.add(cb.equal(root.get("puesto").get("id"), filtros.getIdPuesto()));
        }
        if (filtros.getUnidadId() != null) {
            predicates.add(cb.equal(root.get("unidad").get("id"), filtros.getUnidadId()));
        }
        if (filtros.getIdResponsable() != null) {
            predicates.add(cb.equal(root.get("jefe").get("id"), filtros.getIdResponsable()));
        }
        if (filtros.getIdSupervisor() != null) {
            predicates.add(cb.equal(root.get("unidad").get("supervisor").get("id"), filtros.getIdSupervisor()));
        }
        if (filtros.getIdZona() != null) {
            predicates.add(cb.equal(root.get("unidad").get("zona").get("id"), filtros.getIdZona()));
        }
        return predicates;
    }

    public Page<EmpleadoAsignacionInfo> obtenerAsignacionesPaginado(FiltroEmpleado filtros) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<EmpleadoAsignacionInfo> query = cb.createQuery(EmpleadoAsignacionInfo.class);
        Root<EmpleadoEntity> root = query.from(EmpleadoEntity.class);
        Join<EmpleadoEntity, EmpleadoEntity> jefe = root.join("jefe", JoinType.LEFT);
        Join<EmpleadoEntity, EmpleadoEntity> segundoJefe = root.join("segundoJefe", JoinType.LEFT);

        query.select(cb.construct(EmpleadoAsignacionInfo.class,
                root.get("id"),
                root.get("codigoEmpleado"),
                root.get("puesto").get("nombre"),
                root.get("unidad").get("nombreCompleto"),
                root.get("nombreCompleto"),
                jefe.get("nombreCompleto"),
                segundoJefe.get("nombreCompleto")
        ));

        List<Predicate> predicates = buildPredicatesActivos(cb, root, filtros);
        query.where(predicates.toArray(new Predicate[0]));
        query.orderBy(cb.asc(root.get("nombreCompleto")));

        var pageable = filtros.toPageable();
        TypedQuery<EmpleadoAsignacionInfo> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<EmpleadoAsignacionInfo> resultado = typedQuery.getResultList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<EmpleadoEntity> countRoot = countQuery.from(EmpleadoEntity.class);
        countQuery.select(cb.count(countRoot))
                .where(buildPredicatesActivos(cb, countRoot, filtros).toArray(new Predicate[0]));
        long total = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(resultado, pageable, total);
    }

    public List<InfoCatalogoEmpleados> obtenerConFiltro(FiltroEmpleado filtros) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<InfoCatalogoEmpleados> query = cb.createQuery(InfoCatalogoEmpleados.class);
        Root<EmpleadoEntity> root = query.from(EmpleadoEntity.class);

        query.select(cb.construct(InfoCatalogoEmpleados.class, root.get("id"), root.get("codigoEmpleado"), root.get("puesto")
                .get("id"), root.get("puesto")
                .get("nombre"), root.get("estatus"), root.get("nombreCompleto"), root.get("unidad")
                .get("id"), root.get("unidad")
                .get("nombreCompleto"), root.get("fechaAlta"), root.get("fechaBaja"), root.get("fechaReingreso")));

        List<Predicate> predicates = buildPredicates(cb, root, filtros);

        if (!predicates.isEmpty()) {
            query.where(predicates.toArray(new Predicate[0]));
        }

        TypedQuery<InfoCatalogoEmpleados> typedQuery = entityManager.createQuery(query);
        typedQuery.setHint("org.hibernate.readOnly", true);
        typedQuery.setHint("org.hibernate.cacheable", true);

        return typedQuery.getResultList();
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<EmpleadoEntity> root, FiltroEmpleado filtros) {
        List<Predicate> predicates = new ArrayList<>();

        if (filtros == null) return predicates;
        if (filtros.getId() != null) {
            predicates.add(cb.equal(root.get("id"), filtros.getId()));
        }
        if (filtros.getClave() != null && !filtros.getClave().trim().isEmpty()) {
            predicates.add(cb.equal(root.get("codigoEmpleado"), filtros.getClave().trim()));
        }
        if (filtros.getIdPuesto() != null) {
            predicates.add(cb.equal(root.get("puesto").get("id"), filtros.getIdPuesto()));
        }

        if (filtros.getUnidadId() != null) {
            predicates.add(cb.equal(root.get("unidad").get("id"), filtros.getUnidadId()));
        }
        if (Boolean.TRUE.equals(filtros.getActivos())) {
            predicates.add(cb.notEqual(root.get("estatus"), EmpleadoEstatus.BAJA));
        } else if (filtros.getEstatus() != null) {
            predicates.add(cb.equal(root.get("estatus"), filtros.getEstatus()));
        }
        if (filtros.getIdResponsable() != null) {
            predicates.add(cb.equal(root.get("jefe").get("id"), filtros.getIdResponsable()));
        }
        if (filtros.getIdSupervisor() != null) {
            predicates.add(cb.equal(root.get("unidad").get("supervisor").get("id"), filtros.getIdSupervisor()));
        }

        if (filtros.getIdZona() != null) {
            predicates.add(cb.equal(root.get("unidad").get("zona").get("id"), filtros.getIdZona()));
        }

        return predicates;
    }
}