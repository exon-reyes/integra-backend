package integra.empresa.repository;

import integra.empresa.entity.UnidadEntity;
import integra.empresa.query.unidad.InfoUnidad;
import integra.empresa.repository.UnidadQueryRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;

import java.util.ArrayList;
import java.util.List;

public class UnidadQueryRepositoryImpl implements UnidadQueryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<InfoUnidad> findByFiltros(
            Integer supervisorId,
            Integer zonaId,
            Boolean activo
    ) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<InfoUnidad> cq = cb.createQuery(InfoUnidad.class);

        Root<UnidadEntity> root = cq.from(UnidadEntity.class);

        Join<Object, Object> zona = root.join("zona", JoinType.LEFT);
        Join<Object, Object> supervisor = root.join("supervisor", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();

        if (supervisorId != null) {
            predicates.add(cb.equal(supervisor.get("id"), supervisorId));
        }

        if (zonaId != null) {
            predicates.add(cb.equal(zona.get("id"), zonaId));
        }

        if (activo != null) {
            predicates.add(cb.equal(root.get("activo"), activo));
        }

        cq.select(cb.construct(
                InfoUnidad.class,
                root.get("id"),
                root.get("clave"),
                root.get("nombreCompleto"),
                root.get("activo"),
                zona.get("id"),
                zona.get("nombre"),
                supervisor.get("id"),
                supervisor.get("nombreCompleto")
        ));

        if (!predicates.isEmpty()) {
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        return entityManager.createQuery(cq).getResultList();
    }
}