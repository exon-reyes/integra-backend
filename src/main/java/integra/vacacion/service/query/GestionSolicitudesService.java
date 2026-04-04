package integra.vacacion.service.query;

import integra.empleado.entity.EmpleadoEntity;
import integra.empresa.entity.UnidadEntity;
import integra.model.Empleado;
import integra.vacacion.core.EstatusPeriodo;
import integra.vacacion.domain.model.EstatusSolicitud;
import integra.vacacion.domain.model.TipoSolicitud;
import integra.vacacion.dto.request.FiltroSolicitud;
import integra.vacacion.dto.response.*;
import integra.vacacion.entity.EmpleadoTiempoEntity;
import integra.vacacion.entity.PeriodoVacacionalEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GestionSolicitudesService {

    @PersistenceContext
    private EntityManager em;

    public Page<SolicitudesGestionDTO> getSolicitudesVigentes(FiltroSolicitud filtro) {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        // 1. Total Count query: countDistinct para obtener el total de folios que coinciden con los filtros actuales.
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<EmpleadoTiempoEntity> countRoot = countQuery.from(EmpleadoTiempoEntity.class);
        Join<EmpleadoTiempoEntity, EmpleadoEntity> countEmp = countRoot.join("empleado", JoinType.INNER);
        countQuery.select(cb.countDistinct(countRoot.get("folio")));

        List<Predicate> countPredicates = buildPredicates(cb, countRoot, countEmp, filtro);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(new Predicate[0]));
        }
        Long totalFolios = em.createQuery(countQuery).getSingleResult();

        if (totalFolios == 0) {
            return new PageImpl<>(new ArrayList<>(), PageRequest.of(filtro.getCurrentPage(), filtro.getPageSize()), 0);
        }

        /**Se ejecuta la búsqueda de folios usando folioQuery.select, limitando a la página (setFirstResult y setMaxResults),
         *  ordenándolos en BD según el nombre y la fecha para que el paginador siempre mantenga coherencia.**/
        CriteriaQuery<Long> folioQuery = cb.createQuery(Long.class);
        Root<EmpleadoTiempoEntity> folioRoot = folioQuery.from(EmpleadoTiempoEntity.class);
        Join<EmpleadoTiempoEntity, EmpleadoEntity> folioEmp = folioRoot.join("empleado", JoinType.INNER);
        folioQuery.select(folioRoot.get("folio"));

        List<Predicate> folioPredicates = buildPredicates(cb, folioRoot, folioEmp, filtro);
        if (!folioPredicates.isEmpty()) {
            folioQuery.where(folioPredicates.toArray(new Predicate[0]));
        }
        folioQuery.groupBy(folioRoot.get("folio"), folioEmp.get("nombreCompleto"));

        folioQuery.orderBy(cb.asc(folioEmp.get("nombreCompleto")), cb.asc(cb.least(folioRoot.<Comparable>get("fecha"))));

        TypedQuery<Long> typedFolioQuery = em.createQuery(folioQuery);
        typedFolioQuery.setFirstResult(filtro.getCurrentPage() * filtro.getPageSize());
        typedFolioQuery.setMaxResults(filtro.getPageSize());
        List<Long> folios = typedFolioQuery.getResultList();

        if (folios.isEmpty()) {
            return new PageImpl<>(new ArrayList<>(), PageRequest.of(filtro.getCurrentPage(), filtro.getPageSize()), totalFolios);
        }

        //3. Con los identificadores (folios) concretos de la página actual, se arma la DTO leyendo ahora sí la información detallada garantizando que un agrupador no se parta a medias entre páginas.
        CriteriaQuery<InfoSolicitudGestion> query = cb.createQuery(InfoSolicitudGestion.class);
        Root<EmpleadoTiempoEntity> root = query.from(EmpleadoTiempoEntity.class);
        Join<EmpleadoTiempoEntity, EmpleadoEntity> emp = root.join("empleado", JoinType.INNER);
        Join<EmpleadoEntity, UnidadEntity> unidad = emp.join("unidad", JoinType.LEFT);

        query.select(cb.construct(InfoSolicitudGestion.class, root.get("id"), emp.get("id"), emp.get("codigoEmpleado"), emp.get("nombreCompleto"), unidad.get("nombreCompleto"), root.get("folio"), root.get("fecha"), root.get("estatus"), root.get("estatusJefe"), root.get("estatusRrhh"), root.get("tipo")));

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(root.get("folio").in(folios));
        predicates.addAll(buildPredicates(cb, root, emp, filtro));

        query.where(predicates.toArray(new Predicate[0]));
        query.orderBy(cb.asc(emp.get("nombreCompleto")), cb.asc(root.get("fecha")));

        TypedQuery<InfoSolicitudGestion> typedQuery = em.createQuery(query);
        typedQuery.setHint("org.hibernate.readOnly", true);

        return new PageImpl<>(mapToDTO(typedQuery.getResultList()), PageRequest.of(filtro.getCurrentPage(), filtro.getPageSize()), totalFolios);
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<EmpleadoTiempoEntity> root, Join<EmpleadoTiempoEntity, EmpleadoEntity> emp, FiltroSolicitud filtro) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("activo"), true));

        if (filtro == null) return predicates;

        if (filtro.getEstatus() != null && !filtro.getEstatus().isBlank()) {
            predicates.add(cb.equal(root.get("estatus"), filtro.getEstatus()));
        }
        if (filtro.getJefeId() != null) {
            predicates.add(cb.equal(emp.get("jefe").get("id"), filtro.getJefeId()));
        }
        if (filtro.getRrhhId() != null) {
            predicates.add(cb.equal(emp.get("segundoJefe").get("id"), filtro.getRrhhId()));
        }

        return predicates;
    }

    private List<SolicitudesGestionDTO> mapToDTO(List<InfoSolicitudGestion> rows) {
        Map<Long, SolicitudesGestionDTO> map = new LinkedHashMap<>();

        for (InfoSolicitudGestion row : rows) {
            if (row.folio() == null) continue;

            SolicitudesGestionDTO dto = map.computeIfAbsent(row.folio(), f -> {
                SolicitudesGestionDTO d = new SolicitudesGestionDTO();
                d.setFolioSolicitud(f);
                d.setColaborador(new Empleado(row.empleadoId(), row.codigoEmpleado(), row.nombreCompleto()));
                d.setUnidad(row.unidadNombre());
                d.setTipoSolicitud(row.tipo().name().equals("VACACION") ? "Vacaciones" : "Permiso/Descanso");
                d.setSolicitudes(new ArrayList<>());
                return d;
            });

            dto.getSolicitudes().add(new FechaSolicitud(row.id(), row.fecha(), row.estatus().name()));

            if (row.estatusJefe() != null) dto.setEstatusJefe(row.estatusJefe().name());
            if (row.estatusRrhh() != null) dto.setEstatusRrhh(row.estatusRrhh().name());
        }

        for (SolicitudesGestionDTO dto : map.values()) {
            long aprobados = dto.getSolicitudes()
                    .stream()
                    .filter(s -> EstatusSolicitud.APROBADA.name().equals(s.getEstatus()))
                    .count();
            dto.setDiasAprobados((int) aprobados);
            dto.setDiasTotalSolicitud(dto.getSolicitudes().size());
            dto.setEstatusGeneral(resolverEstatusGeneral(dto.getSolicitudes()));
        }

        return new ArrayList<>(map.values());
    }

    private String resolverEstatusGeneral(List<FechaSolicitud> solicitudes) {
        boolean tienePendientes = solicitudes.stream()
                .anyMatch(s -> EstatusSolicitud.PENDIENTE.name()
                        .equals(s.getEstatus()) || EstatusSolicitud.CREADA.name().equals(s.getEstatus()));
        boolean tieneAprobados = solicitudes.stream()
                .anyMatch(s -> EstatusSolicitud.APROBADA.name().equals(s.getEstatus()));
        boolean todoCancelado = solicitudes.stream()
                .allMatch(s -> EstatusSolicitud.CANCELADA.name().equals(s.getEstatus()));

        if (todoCancelado) return EstatusSolicitud.CANCELADA.name();
        if (tienePendientes) return EstatusSolicitud.PENDIENTE.name();
        if (tieneAprobados) return EstatusSolicitud.APROBADA.name();
        return EstatusSolicitud.PENDIENTE.name();
    }

    public DetalleSolicitudDTO obtenerDetalles(Long folioSolicitud) {


        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<InfoDetalleSolicitud> query = cb.createQuery(InfoDetalleSolicitud.class);
        Root<EmpleadoTiempoEntity> root = query.from(EmpleadoTiempoEntity.class);
        Join<EmpleadoTiempoEntity, EmpleadoEntity> emp = root.join("empleado", JoinType.INNER);
        Join<EmpleadoTiempoEntity, PeriodoVacacionalEntity> periodo = root.join("periodo", JoinType.INNER);
        Join<EmpleadoEntity, UnidadEntity> unidad = emp.join("unidad", JoinType.LEFT);
        Join<EmpleadoEntity, EmpleadoEntity> jefe1 = emp.join("jefe", JoinType.LEFT);
        Join<EmpleadoEntity, EmpleadoEntity> jefe2 = emp.join("segundoJefe", JoinType.LEFT);

        query.select(cb.construct(InfoDetalleSolicitud.class, root.get("id"), root.get("fecha"), root.get("estatus"), root.get("estatusJefe"), root.get("estatusRrhh"), root.get("comentario"), root.get("tipo"), emp.get("id"), emp.get("codigoEmpleado"), emp.get("nombreCompleto"), unidad.get("nombreCompleto"), jefe1.get("id"), jefe1.get("nombreCompleto"), jefe2.get("id"), jefe2.get("nombreCompleto"), periodo.get("diasHabilitados"), periodo.get("diasTomados"), periodo.get("anioGestion")));

        query.where(cb.equal(root.get("folio"), folioSolicitud), cb.equal(periodo.get("estatus"), EstatusPeriodo.VIGENTE));
        query.orderBy(cb.asc(root.get("fecha")));

        TypedQuery<InfoDetalleSolicitud> typedQuery = em.createQuery(query);
        typedQuery.setHint("org.hibernate.readOnly", true);

        List<InfoDetalleSolicitud> rows = typedQuery.getResultList();
        if (rows.isEmpty()) return null;

        return buildDetalle(rows, folioSolicitud);
    }

    private DetalleSolicitudDTO buildDetalle(List<InfoDetalleSolicitud> rows, Long folio) {
        InfoDetalleSolicitud first = rows.getFirst();

        DetalleSolicitudDTO dto = new DetalleSolicitudDTO();
        dto.setFolioSolicitud(folio);
        Empleado empleado = new Empleado(first.empleadoId(), first.codigoEmpleado(), first.nombreCompleto());
        if (first.unidadNombre() != null) {
            empleado.setUnidad(new integra.model.Unidad(first.unidadNombre()));
        }

        dto.setEmpleado(empleado);
        dto.setTipoSolicitud(first.tipo());
        dto.setDiasHabilitados(first.diasHabilitados());
        dto.setDiasTomados(first.diasTomados());
        dto.setAnioGestion(first.anioGestion());
        dto.setDiasSolicitados(rows.size());

        if (first.primerJefeId() != null) {
            dto.setPrimerJefe(new Empleado(first.primerJefeId(), first.primerJefeNombre()));
        }
        if (first.segundoJefeId() != null) {
            dto.setSegundoJefe(new Empleado(first.segundoJefeId(), first.segundoJefeNombre()));
        }

        // Estatus general por responsable (consistente en todo el folio)
        dto.setEstatusPrimerResponsable(resolverEstatusResponsable(rows, InfoDetalleSolicitud::estatusJefe));
        dto.setEstatusSegundoResponsable(resolverEstatusResponsable(rows, InfoDetalleSolicitud::estatusRrhh));

        // Días pendientes de aprobación en este folio
        long diasPendientesAprobacion = rows.stream()
                .filter(r -> r.estatus() != EstatusSolicitud.APROBADA && r.estatus() != EstatusSolicitud.CANCELADA)
                .count();
        if (first.tipo() == TipoSolicitud.VACACION) {
            dto.setRestanteSiAprueba(first.diasHabilitados() - first.diasTomados() - (int) diasPendientesAprobacion);

        }else{
            dto.setRestanteSiAprueba(first.diasHabilitados()-first.diasTomados());
        }
        // Lista de fechas con detalle por día
        List<FechaSolicitud> fechas = rows.stream()
                .map(r -> new FechaSolicitud(r.registroId(), r.fecha(), r.estatus()
                        .name(), r.estatusJefe() != null ? r.estatusJefe()
                                                           .name() : null, r.estatusRrhh() != null ? r.estatusRrhh()
                                                                                                     .name() : null, r.comentario()))
                .toList();
        dto.setFechaSolicituds(fechas);
        dto.setEstatusGlobal(EstatusSolicitud.valueOf(resolverEstatusGeneral(fechas)));

        return dto;
    }

    private EstatusSolicitud resolverEstatusResponsable(List<InfoDetalleSolicitud> rows, java.util.function.Function<InfoDetalleSolicitud, EstatusSolicitud> extractor) {
        boolean tienePendiente = rows.stream()
                .map(extractor)
                .anyMatch(e -> e == EstatusSolicitud.PENDIENTE || e == EstatusSolicitud.CREADA);
        if (tienePendiente) return EstatusSolicitud.PENDIENTE;

        boolean tieneAprobada = rows.stream().map(extractor).anyMatch(e -> e == EstatusSolicitud.APROBADA);
        if (tieneAprobada) return EstatusSolicitud.APROBADA;

        return EstatusSolicitud.CANCELADA;
    }
}
