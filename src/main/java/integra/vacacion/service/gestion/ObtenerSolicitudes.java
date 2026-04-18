package integra.vacacion.service.gestion;

import integra.empleado.entity.EmpleadoEntity;
import integra.empresa.entity.UnidadEntity;
import integra.vacacion.domain.model.EstatusSolicitud;
import integra.vacacion.dto.request.FiltroSolicitud;
import integra.vacacion.dto.response.SolicitudResumen;
import integra.vacacion.dto.response.SolicitudesGestionDTO;
import integra.vacacion.entity.SolicitudDescanso;
import integra.vacacion.query.DiaSolicitudProjection;
import integra.vacacion.repository.DiasSolicitudRepository;
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
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ObtenerSolicitudes {

    private final DiasSolicitudRepository diasRepository;
    @PersistenceContext
    private EntityManager em;

    public Page<SolicitudesGestionDTO> getAll(FiltroSolicitud filtro) {
        Pageable pageable = PageRequest.of(filtro.getCurrentPage(), filtro.getPageSize());

        long total = contarSolicitudes(filtro);
        if (total == 0) return new PageImpl<>(List.of(), pageable, 0);

        List<SolicitudResumen> resumenes = buscarSolicitudes(filtro, pageable);

        // 1. Obtener los IDs de las solicitudes de la página actual
        List<Long> ids = resumenes.stream().map(SolicitudResumen::id).toList();

        // 2. Agrupar los días por Folio ID usando un Map
        // Esto es CRUCIAL para que cada solicitud solo tenga sus días
        Map<Long, List<DiaSolicitudProjection>> diasPorFolio = diasRepository.findDiasBySolicitudIds(ids).stream()
                // Asegúrate de que DiaSolicitudProjection tenga getFolioId()
                .collect(Collectors.groupingBy(DiaSolicitudProjection::getFolioId));

        // 3. Mapeo final pasando solo la lista correspondiente del Map
        List<SolicitudesGestionDTO> dtos = resumenes.stream()
                .map(r -> SolicitudGestionMapper.toDTO(r, diasPorFolio.getOrDefault(r.id(), List.of()) // Solo pasa sus días
                ))
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, total);
    }

    private long contarSolicitudes(FiltroSolicitud filtro) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<SolicitudDescanso> root = query.from(SolicitudDescanso.class);

        // Solo hacemos el Join si el filtro realmente lo requiere
        Predicate[] predicates = buildPredicates(cb, root, filtro).toArray(new Predicate[0]);

        query.select(cb.count(root)).where(predicates);
        return em.createQuery(query).getSingleResult();
    }

    private List<SolicitudResumen> buscarSolicitudes(FiltroSolicitud filtro, Pageable pageable) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<SolicitudResumen> query = cb.createQuery(SolicitudResumen.class);
        Root<SolicitudDescanso> root = query.from(SolicitudDescanso.class);

        // Joins necesarios para la proyección del resumen
        Join<SolicitudDescanso, EmpleadoEntity> emp = root.join("empleado", JoinType.INNER);
        Join<EmpleadoEntity, UnidadEntity> unidad = emp.join("unidad", JoinType.LEFT);

        query.select(cb.construct(SolicitudResumen.class, root.get("id"), root.get("folioSolicitud"), root.get("tipoSolicitud"), root.get("estatus"), root.get("estatusNivel1"), root.get("estatusNivel2"), root.get("diasSolicitados"), emp.get("id"), emp.get("codigoEmpleado"), emp.get("nombreCompleto"), unidad.get("nombreCompleto")));

        query.where(buildPredicates(cb, root, filtro).toArray(new Predicate[0]));
        query.orderBy(cb.desc(root.get("id"))); // ID suele ser más rápido que Folio si es secuencial

        return em.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .setHint("org.hibernate.readOnly", true) // Hint de optimización para lectura
                .getResultList();
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<SolicitudDescanso> root, FiltroSolicitud filtro) {
        List<Predicate> predicates = new ArrayList<>();
        if (filtro == null) return predicates;

        if (filtro.getEstatus() != null && !filtro.getEstatus().isBlank()) {
            try {
                predicates.add(cb.equal(root.get("estatus"), EstatusSolicitud.valueOf(filtro.getEstatus())));
            } catch (IllegalArgumentException ignored) {
            }
        }

        boolean needsEmpJoin = filtro.getResponsableId() != null
                || filtro.getRrhhId() != null
                || filtro.getSupervisorId() != null
                || filtro.getUnidadId() != null
                || filtro.getEmpleadoId() != null;

        if (needsEmpJoin) {
            Join<SolicitudDescanso, EmpleadoEntity> emp = root.join("empleado");

            if (filtro.getEmpleadoId() != null) {
                predicates.add(cb.equal(emp.get("id"), filtro.getEmpleadoId()));
            }
            if (filtro.getResponsableId() != null) {
                predicates.add(cb.equal(emp.get("jefe").get("id"), filtro.getResponsableId()));
            }
            if (filtro.getRrhhId() != null) {
                predicates.add(cb.equal(emp.get("segundoJefe").get("id"), filtro.getRrhhId()));
            }
            if (filtro.getUnidadId() != null) {
                predicates.add(cb.equal(emp.get("unidad").get("id"), filtro.getUnidadId()));
            }
            if (filtro.getSupervisorId() != null) {
                Join<EmpleadoEntity, UnidadEntity> unidad = emp.join("unidad", JoinType.INNER);
                predicates.add(cb.equal(unidad.get("supervisor").get("id"), filtro.getSupervisorId()));
            }
        }

        return predicates;
    }
}