//package integra.vacacion.service.command;
//
//import integra.empleado.repository.EmpleadoRepository;
//import integra.vacacion.domain.model.EstatusSolicitud;
//import integra.vacacion.domain.model.Solicitud;
//import integra.vacacion.dto.request.ConfiguracionDescansoRequest;
//import integra.vacacion.dto.response.ConfiguracionDescansoDTO;
//import integra.vacacion.dto.response.ConfiguracionDescansoDTO.DescansoPendienteDTO;
//import integra.vacacion.entity.DescansoEmpleadoEntity;
//import integra.vacacion.entity.PeriodoVacacionalEntity;
//import integra.vacacion.exception.VacacionException;
//import integra.vacacion.repository.DescansoEmpleadoRepository;
//import integra.vacacion.repository.PeriodoVacacionalRepository;
//import integra.vacacion.repository.SolicitudVacacionRepository;
//import integra.vacacion.service.SolicitudVacacionMapper;
//import integra.vacacion.service.validation.DescansoValidationService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Set;
//
//@Service
//@RequiredArgsConstructor
//public class ConfiguracionDescansoService {
//
//    private final DescansoEmpleadoRepository descansoRepository;
//    private final EmpleadoRepository empleadoRepository;
//    private final DescansoValidationService descansoValidationService;
//    private final PeriodoVacacionalRepository periodoVacacionalRepository;
//    private final SolicitudVacacionRepository solicitudRepository;
//    private final SolicitudVacacionMapper vacacionMapper;
//
//
//    @Transactional
//    public ConfiguracionDescansoDTO configurar(Integer empleadoId, ConfiguracionDescansoRequest request) {
//        if (!empleadoRepository.existsById(empleadoId)) {
//            throw VacacionException.empleadoNoEncontrado(empleadoId);
//        }
//
//        PeriodoVacacionalEntity ultimoPeriodo = periodoVacacionalRepository.findByEmpleadoIdAndEstatus(empleadoId, PeriodoVacacionalEntity.EstatusPeriodo.VIGENTE)
//                .getFirst();
//
//        List<Solicitud> solicitudesVacaciones = solicitudRepository.findByEmpleadoIdAndPeriodoId(empleadoId, ultimoPeriodo.getId())
//                .stream()
//                .map(vacacionMapper::convertFromEntity)
//                .toList();
//
//
//        // Validar que ninguna fecha sea anterior a hoy
//        descansoValidationService.validarFechasDescanso(request.diasDescanso());
//        //Validar cruce con vacaciones ya solicitadas
//        Set<LocalDate> descansosFiltrados = descansoValidationService.filtrarDescansosSinConflictoVacaciones(solicitudesVacaciones, request.diasDescanso());
//
//        // Crear nuevos descansos en estado PENDIENTE
//        for (LocalDate fecha : descansosFiltrados) {
//            DescansoEmpleadoEntity descanso = new DescansoEmpleadoEntity();
//            descanso.setEmpleadoId(empleadoId);
//            descanso.setFechaDescanso(fecha);
//            descanso.setComentario(request.comentario());
//            descanso.setActivo(false); // Solo activo cuando APROBADO
//            descanso.setEstatus(EstatusSolicitud.PENDIENTE);
//            descanso.setPeriodoId(ultimoPeriodo.getId());
//            descansoRepository.save(descanso);
//        }
//        return new ConfiguracionDescansoDTO(empleadoId, descansosFiltrados, Set.of(), true);
//    }
//
//    @Transactional
//    public void aprobarDescansos(Integer empleadoId, Integer aprobadorId, String comentarios) {
//        List<DescansoEmpleadoEntity> pendientes = descansoRepository.findByEmpleadoIdAndEstatus(empleadoId, EstatusSolicitud.PENDIENTE);
//
//        if (pendientes.isEmpty()) {
//            throw VacacionException.solicitudNoEncontrada(null);
//        }
//
//        pendientes.forEach(d -> {
//            d.setEstatus(EstatusSolicitud.APROBADA);
//            d.setActivo(true);
//            d.setAprobadorId(aprobadorId);
//            d.setFechaAprobacion(LocalDateTime.now());
//            d.setComentariosAprobador(comentarios);
//            descansoRepository.save(d);
//        });
//    }
//
//    @Transactional
//    public void rechazarDescansos(Integer empleadoId, Integer aprobadorId, String comentarios) {
//        List<DescansoEmpleadoEntity> pendientes = descansoRepository.findByEmpleadoIdAndEstatus(empleadoId, EstatusSolicitud.PENDIENTE);
//
//        if (pendientes.isEmpty()) {
//            throw VacacionException.solicitudNoEncontrada(null);
//        }
//
//        pendientes.forEach(d -> {
//            d.setEstatus(EstatusSolicitud.RECHAZADA);
//            d.setActivo(false);
//            d.setAprobadorId(aprobadorId);
//            d.setFechaAprobacion(LocalDateTime.now());
//            d.setComentariosAprobador(comentarios);
//            descansoRepository.save(d);
//        });
//    }
//
//    @Transactional(readOnly = true)
//    public ConfiguracionDescansoDTO obtener(Integer empleadoId) {
//        // Descansos aprobados (activo = true)
//        Set<LocalDate> aprobados = descansoRepository.findFechasDescansoByEmpleado(empleadoId);
//        // Descansos vacacionesPendientes de aprobación
//        List<DescansoEmpleadoEntity> pendientesList = descansoRepository.findByEmpleadoIdAndEstatus(empleadoId, EstatusSolicitud.PENDIENTE);
//        Set<LocalDate> pendientes = pendientesList.stream()
//                .map(DescansoEmpleadoEntity::getFechaDescanso)
//                .collect(java.util.stream.Collectors.toSet());
//        List<DescansoPendienteDTO> pendientesConId = pendientesList.stream()
//                .map(d -> new DescansoPendienteDTO(d.getId(), d.getFechaDescanso()))
//                .collect(java.util.stream.Collectors.toList());
//
//        boolean configurado = !aprobados.isEmpty() || !pendientes.isEmpty();
//        return new ConfiguracionDescansoDTO(empleadoId, aprobados, pendientes, pendientesConId, configurado);
//    }
//
//    @Transactional
//    public void eliminarDescanso(Long descansoId, Integer empleadoId) {
//        DescansoEmpleadoEntity descanso = descansoRepository.findByIdAndEmpleadoId(descansoId, empleadoId)
//                .orElseThrow(() -> VacacionException.solicitudNoEncontrada(descansoId));
//
//        if (descanso.getEstatus() != EstatusSolicitud.PENDIENTE) {
//            throw new RuntimeException("Solo se pueden eliminar descansos en estado pendiente");
//        }
//
//        descansoRepository.delete(descanso);
//    }
//}
