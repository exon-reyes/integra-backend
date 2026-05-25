package integra.vacacion.service.gestion;

import integra.empleado.entity.EmpleadoEntity;
import integra.empresa.entity.UnidadEntity;
import integra.model.Empleado;
import integra.vacacion.domain.model.EstatusSolicitud;
import integra.vacacion.domain.model.TipoSolicitud;
import integra.vacacion.dto.response.DetalleSolicitudDTO;
import integra.vacacion.dto.response.FechaSolicitud;
import integra.vacacion.entity.PeriodoVacacionalEntity;
import integra.vacacion.entity.SolicitudDescanso;
import integra.vacacion.exception.VacacionException;
import integra.vacacion.repository.SolicitudDescansoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DetallesSolicitud {
    private final SolicitudDescansoRepository repository;

    @Transactional(readOnly = true)
    public DetalleSolicitudDTO getByFolio(Long folio) {

        SolicitudDescanso data = repository.findByFolioSolicitud(folio)
                .orElseThrow(() -> VacacionException.folioNoEncontrado(folio));

        PeriodoVacacionalEntity periodo = data.getPeriodo();
        EmpleadoEntity dataEmpleado = data.getEmpleado();
        EmpleadoEntity dataResponsableNivel1 = data.getEmpleado().getJefe();
        EmpleadoEntity dataResponsableNivel2 = data.getEmpleado().getSegundoJefe();
        UnidadEntity dataUnidad = data.getEmpleado().getUnidad();

        DetalleSolicitudDTO result = new DetalleSolicitudDTO();
        result.setId(data.getId());
        result.setFechaCreacion(data.getFechaCreacion());
        Empleado empleado = new Empleado(dataEmpleado.getId(), dataEmpleado.getNombreCompleto());
        empleado.setUnidad(new integra.model.Unidad(dataUnidad.getId(), dataUnidad.getNombreCompleto()));
        empleado.setCodigo(dataEmpleado.getCodigoEmpleado());
        empleado.setFechaAlta(dataEmpleado.getFechaAlta());
        if (dataEmpleado.getPuesto() != null) {
            empleado.setPuesto(new integra.model.Puesto(dataEmpleado.getPuesto().getId(), dataEmpleado.getPuesto().getNombre()));
        }

        result.setEstatusGlobal(data.getEstatus());
        result.setTipoSolicitud(data.getTipoSolicitud());
        result.setEstatusPrimerResponsable(data.getEstatusNivel1());
        result.setEstatusSegundoResponsable(data.getEstatusNivel2());
        result.setFecha(data.getFechaCreacion());

        // Días en otras solicitudes PENDIENTE del mismo empleado y periodo (excluye la actual)
        // Conteo granular desde dias_solicitud_descanso para mayor precisión
        // Este campo representa SOLO las otras solicitudes, NO incluye los días de esta solicitud
        int diasOtrasSolicitudes = repository.sumDiasSolicitadosPendientes(
                dataEmpleado.getId(), periodo.getId(), data.getId()
        ).intValue();

        // diasSolicitados representa SOLO los días de OTRAS solicitudes pendientes
        // Los días de ESTA solicitud ya están en diasPendientesEstaSolicitud
        result.setDiasSolicitados(diasOtrasSolicitudes);
        // diasEstaSolicitud: conteo granular real por estatus (no usa campo denormalizado)
        long diasEstaSolicitudAprobados = data.getDiasSolicitudDescansos().stream()
                .filter(d -> d.getEstatusNivel2() == EstatusSolicitud.APROBADA)
                .count();
        long diasEstaSolicitudPendientes = data.getDiasSolicitudDescansos().stream()
                .filter(d -> d.getEstatusNivel2() == EstatusSolicitud.PENDIENTE)
                .count();
        long diasEstaSolicitudCancelados = data.getDiasSolicitudDescansos().stream()
                .filter(d -> d.getEstatusNivel2() == EstatusSolicitud.CANCELADA)
                .count();

        // Para la barra: mostrar días no cancelados de esta solicitud
        int diasEstaSolicitud = (int) (diasEstaSolicitudAprobados + diasEstaSolicitudPendientes);
        result.setDiasEstaSolicitud(diasEstaSolicitud);
        result.setDiasAprobadosEstaSolicitud((int) diasEstaSolicitudAprobados);
        result.setDiasPendientesEstaSolicitud((int) diasEstaSolicitudPendientes);
        result.setDiasCanceladosEstaSolicitud((int) diasEstaSolicitudCancelados);
        result.setDiasHabilitados(periodo.getDiasHabilitados());
        result.setDiasTomados(periodo.getDiasTomados());

        result.setFolioSolicitud(data.getFolioSolicitud());
        result.setEmpleado(empleado);
        result.setAnioGestion(periodo.getAnioGestion());

        if (dataResponsableNivel1 != null) {
            Empleado pj = new Empleado(dataResponsableNivel1.getId(), dataResponsableNivel1.getNombreCompleto());
            if (dataResponsableNivel1.getPuesto() != null) {
                pj.setPuesto(new integra.model.Puesto(dataResponsableNivel1.getPuesto().getId(), dataResponsableNivel1.getPuesto().getNombre()));
            }
            result.setPrimerJefe(pj);
        }
        if (dataResponsableNivel2 != null) {
            Empleado pj2 = new Empleado(dataResponsableNivel2.getId(), dataResponsableNivel2.getNombreCompleto());
            if (dataResponsableNivel2.getPuesto() != null) {
                pj2.setPuesto(new integra.model.Puesto(dataResponsableNivel2.getPuesto().getId(), dataResponsableNivel2.getPuesto().getNombre()));
            }
            result.setSegundoJefe(pj2);
        }
        result.setFechaSolicituds(data.getDiasSolicitudDescansos()
                .stream()
                .map(r -> new FechaSolicitud(r.getId(), r.getFecha(), r.getEstatusNivel1().name(), r.getEstatusNivel2()
                        .name()))
                .sorted((a, b) -> a.getFecha().compareTo(b.getFecha()))
                .toList());

        result.setEstatusPrimerResponsable(data.getEstatusNivel1());
        result.setEstatusSegundoResponsable(data.getEstatusNivel2());

        if (data.getTipoSolicitud().equals(TipoSolicitud.VACACION) && data.getEstatus() != EstatusSolicitud.APROBADA) {
            // Cálculo: diasHabilitados - diasTomados - diasPendientesEsta - diasOtrasSolicitudes
            result.setRestanteSiAprueba(periodo.getDiasHabilitados() - periodo.getDiasTomados() - (int)diasEstaSolicitudPendientes - diasOtrasSolicitudes);
        } else {
            result.setRestanteSiAprueba(periodo.getDiasRestantes());
        }

        return result;
    }
}
