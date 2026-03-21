//package integra.vacacion.service;
//
//import integra.empleado.entity.EmpleadoEntity;
//import integra.empleado.repository.EmpleadoRepository;
//import integra.vacacion.domain.model.Solicitud;
//import integra.vacacion.dto.response.SolicitudVacacionDTO;
//import integra.vacacion.entity.SolicitudVacacionEntity;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class SolicitudVacacionMapper {
//
//    private final EmpleadoRepository empleadoRepository;
//
//    public SolicitudVacacionDTO toDTO(SolicitudVacacionEntity entity) {
//        EmpleadoEntity empleado = empleadoRepository.findById(entity.getEmpleadoId()).orElse(null);
//
//        String nombreEmpleado = "";
//        String departamento = "";
//        String puesto = "";
//
//        if (empleado != null) {
//            nombreEmpleado = empleado.getNombreCompleto();
//            departamento = empleado.getDepartamento() != null ? empleado.getDepartamento().getNombre() : "";
//            puesto = empleado.getPuesto() != null ? empleado.getPuesto().getNombre() : "";
//        }
//
//        String nombreAprobador = "";
//        if (entity.getAprobadorId() != null) {
//            EmpleadoEntity aprobador = empleadoRepository.findById(entity.getAprobadorId()).orElse(null);
//            nombreAprobador = aprobador != null ? aprobador.getNombreCompleto() : "";
//        }
//
//        return new SolicitudVacacionDTO(
//                entity.getId(),
//                entity.getEmpleadoId(),
//                nombreEmpleado,
//                departamento,
//                puesto,
//                entity.getFechaInicio(),
//                entity.getFechaFin(),
//                entity.getDiasSolicitados(),
//                entity.getComentario(),
//                entity.getEstatus().name(),
//                entity.getComentariosAprobador(),
//                entity.getAprobadorId(),
//                nombreAprobador,
//                entity.getFechaAprobacion(),
//                entity.getCreatedAt()
//        );
//    }
//    public Solicitud convertFromEntity(SolicitudVacacionEntity t) {
//        Solicitud solicitud = new Solicitud();
//        solicitud.setFechaInicio(t.getFechaInicio());
//        solicitud.setFechaFin(t.getFechaFin());
//        solicitud.setEstatus(t.getEstatus().name());
//        solicitud.setComentarioAprobador(t.getComentariosAprobador());
//        solicitud.setDiasSolicitados(t.getDiasSolicitados());
//        solicitud.setComentario(t.getComentario());
//        solicitud.setPeriodoId(t.getPeriodoId());
//        return solicitud;
//    }
//}
