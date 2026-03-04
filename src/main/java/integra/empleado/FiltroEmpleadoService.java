package integra.empleado;

import integra.empleado.constants.EmpleadoEstatus;
import integra.empleado.entity.EmpleadoEntity;
import integra.empleado.query.InfoCatalogoEmpleados;
import integra.empleado.util.FiltroEmpleado;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FiltroEmpleadoService {

    @PersistenceContext
    private EntityManager entityManager;

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