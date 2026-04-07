package integra.vacacion.service.gestion;

import integra.empleado.entity.EmpleadoEntity;
import integra.empresa.entity.UnidadEntity;
import integra.model.Empleado;
import integra.model.Unidad;
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
        empleado.setUnidad(new Unidad(dataUnidad.getId(), dataUnidad.getNombreCompleto()));

        result.setEstatusGlobal(data.getEstatus());
        result.setTipoSolicitud(data.getTipoSolicitud());
        result.setEstatusPrimerResponsable(data.getEstatusNivel1());
        result.setEstatusSegundoResponsable(data.getEstatusNivel2());
        result.setFecha(data.getFechaCreacion());

        result.setDiasSolicitados(data.getDiasSolicitados());
        result.setDiasHabilitados(periodo.getDiasHabilitados());
        result.setDiasTomados(periodo.getDiasTomados());

        result.setFolioSolicitud(data.getFolioSolicitud());
        result.setEmpleado(empleado);
        result.setAnioGestion(periodo.getAnioGestion());

        if (dataResponsableNivel1 != null) {
            result.setPrimerJefe(new Empleado(dataResponsableNivel1.getId(), dataResponsableNivel1.getNombreCompleto()));
        }
        if (dataResponsableNivel2 != null) {
            result.setSegundoJefe(new Empleado(dataResponsableNivel2.getId(), dataResponsableNivel2.getNombreCompleto()));
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
            result.setRestanteSiAprueba(periodo.getDiasHabilitados() - periodo.getDiasTomados() - data.getDiasSolicitados());
        } else {
            result.setRestanteSiAprueba(periodo.getDiasRestantes());
        }

        return result;
    }
}
