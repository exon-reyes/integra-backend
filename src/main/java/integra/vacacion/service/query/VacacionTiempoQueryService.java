//package integra.vacacion.service.query;
//
//import integra.vacacion.domain.model.SolicitudEmpleado;
//import integra.vacacion.entity.EmpleadoTiempoEntity;
//import integra.vacacion.entity.PeriodoVacacionalEntity;
//import integra.vacacion.repository.EmpleadoTiempoEntityRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//public class VacacionTiempoQueryService {
//
//    private final EmpleadoTiempoEntityRepository tiempoEntityRepository;
//
//    private static SolicitudEmpleado apply(EmpleadoTiempoEntity data, LocalDate hoy) {
//        SolicitudEmpleado s = new SolicitudEmpleado();
//        s.setId(data.getId());
//        s.setFecha(data.getFecha());
//        s.setEmpleadoId(data.getEmpleado().getId());
//        s.setEstatus(data.getEstatus());
//        s.setFechaAprobacion(data.getFechaAprobacion());
//        s.setComentariosAprobador(data.getComentariosAprobador());
//        s.setTipo(data.getTipo());
//        s.setComentario(data.getComentario());
//        s.setPeriodoId(data.getPeriodo().getId());
//        s.setActivo(data.getActivo());
//        s.setCreatedAt(data.getCreatedAt());
//        return s;
//    }
//
//    public List<SolicitudEmpleado> obtenerSolicitudes(Integer empleadoId, Long periodoId) {
//        LocalDate hoy = LocalDate.now();
//        return tiempoEntityRepository.findByEmpleadoIdAndPeriodoId(empleadoId, periodoId)
//                .stream()
//                .map(e -> apply(e, hoy))
//                .toList();
//    }
//
//
//
//}