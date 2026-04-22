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
        return new PageImpl<>(mapearConDias(resumenes), pageable, total);
    }

    public List<SolicitudesGestionDTO> getAllSinPaginacion(FiltroSolicitud filtro) {
        List<SolicitudResumen> resumenes = buscarSolicitudesSinPaginacion(filtro);
        return mapearConDias(resumenes);
    }

    private List<SolicitudesGestionDTO> mapearConDias(List<SolicitudResumen> resumenes) {
        List<Long> ids = resumenes.stream().map(SolicitudResumen::id).toList();

        Map<Long, List<DiaSolicitudProjection>> diasPorFolio = diasRepository.findDiasBySolicitudIds(ids).stream()
                .collect(Collectors.groupingBy(DiaSolicitudProjection::getFolioId));

        return resumenes.stream()
                .map(r -> SolicitudGestionMapper.toDTO(r, diasPorFolio.getOrDefault(r.id(), List.of())))
                .collect(Collectors.toList());
    }

    private long contarSolicitudes(FiltroSolicitud filtro) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<SolicitudDescanso> root = query.from(SolicitudDescanso.class);

        Predicate[] predicates = buildPredicates(cb, root, filtro).toArray(new Predicate[0]);
        query.select(cb.count(root)).where(predicates);
        return em.createQuery(query).getSingleResult();
    }

    private List<SolicitudResumen> buscarSolicitudes(FiltroSolicitud filtro, Pageable pageable) {
        return buildResumenQuery(filtro)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .setHint("org.hibernate.readOnly", true)
                .getResultList();
    }

    private List<SolicitudResumen> buscarSolicitudesSinPaginacion(FiltroSolicitud filtro) {
        return buildResumenQuery(filtro)
                .setHint("org.hibernate.readOnly", true)
                .getResultList();
    }

    private jakarta.persistence.TypedQuery<SolicitudResumen> buildResumenQuery(FiltroSolicitud filtro) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<SolicitudResumen> query = cb.createQuery(SolicitudResumen.class);
        Root<SolicitudDescanso> root = query.from(SolicitudDescanso.class);

        Join<SolicitudDescanso, EmpleadoEntity> emp = root.join("empleado", JoinType.INNER);
        Join<EmpleadoEntity, UnidadEntity> unidad = emp.join("unidad", JoinType.LEFT);
        Join<EmpleadoEntity, EmpleadoEntity> jefe = emp.join("jefe", JoinType.LEFT);
        Join<EmpleadoEntity, EmpleadoEntity> segundoJefe = emp.join("segundoJefe", JoinType.LEFT);

        query.select(cb.construct(
                SolicitudResumen.class,
                root.get("id"),
                root.get("folioSolicitud"),
                root.get("tipoSolicitud"),
                root.get("estatus"),
                root.get("estatusNivel1"),
                root.get("estatusNivel2"),
                root.get("diasSolicitados"),
                emp.get("id"),
                emp.get("codigoEmpleado"),
                emp.get("nombreCompleto"),
                unidad.get("nombreCompleto"),
                jefe.get("nombreCompleto"),
                segundoJefe.get("nombreCompleto"),
                root.get("fechaCreacion")
        ));

        query.where(buildPredicates(cb, root, filtro).toArray(new Predicate[0]));
        query.orderBy(cb.desc(root.get("id")));

        return em.createQuery(query);
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

        if (filtro.getFechaDesde() != null && filtro.getFechaHasta() != null) {
            predicates.add(cb.between(root.get("fechaCreacion"), filtro.getFechaDesde(), filtro.getFechaHasta()));
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