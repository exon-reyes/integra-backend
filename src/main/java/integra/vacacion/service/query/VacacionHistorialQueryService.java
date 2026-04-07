//package integra.vacacion.service.query;
//
//import integra.vacacion.dto.response.HistorialSolicitud;
//import integra.vacacion.repository.EmpleadoTiempoHistorialRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//@RequiredArgsConstructor
//@Service
//@Transactional(readOnly = true)
//public class VacacionHistorialQueryService {
//
//    private final EmpleadoTiempoHistorialRepository historialRepository;
//
//    public List<HistorialSolicitud> obtenerLineaTiempo(Long empleadoTiempoId) {
//        return historialRepository.findByEmpleadoTiempoIdOrderByFechaEventoDesc(empleadoTiempoId)
//                .stream()
//                .map(entity -> new HistorialSolicitud(
//                        entity.getId(),
//                        entity.getEmpleadoTiempoId(),
//                        entity.getTipoEvento(),
//                        entity.getFechaEvento(),
//                        entity.getUsuarioId(),
//                        entity.getComentario()
//                )).toList();
//    }
//}
