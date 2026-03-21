//package integra.vacacion.service.query;
//
//import integra.vacacion.domain.model.Descanso;
//import integra.vacacion.repository.DescansoEmpleadoRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//public class DescansoQueryService {
//    private final DescansoEmpleadoRepository descansoRepository;
//
//    public List<Descanso> obtenerDescansos(Integer empleadoId, Long periodId) {
//        return descansoRepository.findByEmpleadoIdAndPeriodoId(empleadoId, periodId).stream().map(t -> {
//            Descanso descanso = new Descanso();
//            descanso.setId(t.getId());
//            descanso.setFecha(t.getFechaDescanso());
//            descanso.setEstatus(t.getEstatus());
//            descanso.setActivo(t.getActivo());
//            descanso.setComentarioAprobador(t.getComentariosAprobador());
//            descanso.setFechaAprobacion(t.getFechaAprobacion());
//            descanso.setComentario(t.getComentario());
//            return descanso;
//        }).toList();
//    }
//}
