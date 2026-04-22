package integra.vacacion.service.gestion;

import integra.model.Empleado;
import integra.vacacion.domain.model.EstatusSolicitud;
import integra.vacacion.dto.response.FechaSolicitud;
import integra.vacacion.dto.response.SolicitudResumen;
import integra.vacacion.dto.response.SolicitudesGestionDTO;
import integra.vacacion.query.DiaSolicitudProjection;

import java.util.List;

public final class SolicitudGestionMapper {

    private SolicitudGestionMapper() {
    }

    public static SolicitudesGestionDTO toDTO(SolicitudResumen r, List<DiaSolicitudProjection> dias) {
        SolicitudesGestionDTO dto = new SolicitudesGestionDTO();
        dto.setId(r.id());
        dto.setFolioSolicitud(r.folioSolicitud());
        dto.setTipoSolicitud(r.tipoSolicitud() != null ? r.tipoSolicitud().name() : null);
        dto.setEstatusGeneral(r.estatusGeneral() != null ? r.estatusGeneral().name() : null);
        dto.setEstatusJefe(r.estatusNivel1() != null ? r.estatusNivel1().name() : null);
        dto.setEstatusRrhh(r.estatusNivel2() != null ? r.estatusNivel2().name() : null);
        dto.setDiasTotalSolicitud(r.diasSolicitados() != null ? r.diasSolicitados() : 0);
        dto.setDiasAprobados(calcularAprobados(dias));
        dto.setPrimerJefe(r.primerJefeNombre());
        dto.setSegundoJefe(r.segundoJefeNombre());
        dto.setFechaCreacion(r.fechaCreacion());

        if (r.empleadoId() != null) {
            dto.setColaborador(new Empleado(r.empleadoId(), r.empleadoCodigo(), r.empleadoNombre()));
            dto.setUnidad(r.unidadNombre());
        }

        dto.setSolicitudes(dias.stream()
                .map(d -> new FechaSolicitud(
                        d.getId(),
                        d.getFecha(),
                        d.getEstatusNivel2() != null ? d.getEstatusNivel2().name() : null,
                        d.getEstatusNivel1() != null ? d.getEstatusNivel1().name() : null,
                        d.getEstatusNivel2() != null ? d.getEstatusNivel2().name() : null,
                        null))
                .toList());

        return dto;
    }

    private static int calcularAprobados(List<DiaSolicitudProjection> dias) {
        return (int) dias.stream().filter(d -> EstatusSolicitud.APROBADA == d.getEstatusNivel2()).count();
    }
}

