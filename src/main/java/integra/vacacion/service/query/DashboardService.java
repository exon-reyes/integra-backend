//package integra.vacacion.service.query;
//
//import integra.empleado.exception.EmpleadoException;
//import integra.empleado.query.EmpleadoVacacionInfo;
//import integra.empleado.service.EmpleadoService;
//import integra.model.Empleado;
//import integra.vacacion.core.EstatusPeriodo;
//import integra.vacacion.domain.model.*;
//import integra.vacacion.dto.response.PeriodoVacacional;
//import integra.vacacion.entity.PeriodoVacacionalEntity;
//import integra.vacacion.exception.VacacionException;
//import integra.vacacion.repository.PeriodoVacacionalRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//import java.time.temporal.ChronoUnit;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//public class DashboardService {
//
//    private final VacacionTiempoQueryService vacacionTiempoService;
//    private final PeriodoVacacionalRepository periodoVacacionalRepository;
//    private final EmpleadoService empleadoService;
//
//    public DashboardSolicitudes obtenerDashboard(Integer empleadoId) {
//
//        LocalDate hoy = LocalDate.now();
//
//
//        EmpleadoVacacionInfo dataEmpleado = empleadoService.obtenerPorId(empleadoId, EmpleadoVacacionInfo.class)
//                .orElseThrow(() -> EmpleadoException.notFound(Long.valueOf(empleadoId)));
//
//        Empleado empleado = new Empleado(empleadoId);
//        LocalDate fechaIngreso = dataEmpleado.fechaReingreso() != null ? dataEmpleado.fechaReingreso() : dataEmpleado.fechaAlta();
//        empleado.setAntiguedadAnios((int) ChronoUnit.YEARS.between(fechaIngreso, hoy));
//        empleado.setFechaAlta(dataEmpleado.fechaAlta());
//        empleado.setFechaReingreso(dataEmpleado.fechaReingreso());
//        empleado.setFechaBaja(dataEmpleado.fechaBaja());
//
//        LocalDate proximoAniversario = fechaIngreso.plusYears(empleado.getAntiguedadAnios() + 1);
//
//        // =============================
//        // PERIODO
//        // =============================
//        PeriodoVacacionalEntity periodoEntity = periodoVacacionalRepository.findPeriodoVacacionalEntityByEmpleadoIdAndEstatus(empleadoId, EstatusPeriodo.VIGENTE)
//                .orElseThrow(VacacionException::sinPeriodoActivo);
//
//        PeriodoVacacional periodo = new PeriodoVacacional();
//        periodo.setId(periodoEntity.getId());
//        periodo.setDiasRestantes(periodoEntity.getDiasRestantes());
//        periodo.setDiasHabilitados(periodoEntity.getDiasHabilitados());
//        periodo.setAnioGestion(periodoEntity.getAnioGestion());
//
//        // =============================
//        // SOLICITUDES
//        // =============================
//        List<SolicitudEmpleado> solicitudes = vacacionTiempoService.obtenerSolicitudes(empleadoId, periodoEntity.getId());
//
//        //  UNA SOLA PASADA
//        Map<TipoSolicitud, Map<EstatusSolicitud, List<SolicitudEmpleado>>> agrupadas = solicitudes.stream()
//                .collect(Collectors.groupingBy(SolicitudEmpleado::getTipo, Collectors.groupingBy(SolicitudEmpleado::getEstatus)));
//
//        // =============================
//        // VACACIONES
//        // =============================
//        Map<EstatusSolicitud, List<SolicitudEmpleado>> vac = agrupadas.getOrDefault(TipoSolicitud.VACACION, Map.of());
//
//        List<SolicitudEmpleado> vacPendientes = vac.getOrDefault(EstatusSolicitud.PENDIENTE, List.of());
//        List<SolicitudEmpleado> vacAprobadas = vac.getOrDefault(EstatusSolicitud.APROBADA, List.of());
//        List<SolicitudEmpleado> vacCanceladas = vac.getOrDefault(EstatusSolicitud.CANCELADA, List.of());
//
//        Map<Boolean, List<SolicitudEmpleado>> particionAprobadas = vacAprobadas.stream()
//                .collect(Collectors.partitioningBy(s -> s.getFecha().isBefore(hoy)));
//        List<SolicitudEmpleado> disfrutadas = particionAprobadas.getOrDefault(true, List.of());
//        List<SolicitudEmpleado> aprobadas = particionAprobadas.getOrDefault(false, List.of());
//
//        SolicitudesVacaciones vacaciones = new SolicitudesVacaciones();
//        vacaciones.setPendientes(vacPendientes);
//        vacaciones.setAprobadas(aprobadas);
//        vacaciones.setDisfrutadas(disfrutadas);
//        vacaciones.setCanceladas(vacCanceladas);
//
//        vacaciones.setIndicadores(disfrutadas.size(), aprobadas.size(), vacPendientes.size(), vacCanceladas.size());
//
//        // =============================
//        // DESCANSOS
//        // =============================
//        Map<EstatusSolicitud, List<SolicitudEmpleado>> des = agrupadas.getOrDefault(TipoSolicitud.DESCANSO, Map.of());
//        List<SolicitudEmpleado> desPendientes = des.getOrDefault(EstatusSolicitud.PENDIENTE, List.of());
//        List<SolicitudEmpleado> desAprobadas = des.getOrDefault(EstatusSolicitud.APROBADA, List.of());
//        List<SolicitudEmpleado> desCanceladas = des.getOrDefault(EstatusSolicitud.CANCELADA, List.of());
//
//        SolicitudesDescanso descansos = new SolicitudesDescanso(desPendientes, desAprobadas, desCanceladas);
//        descansos.setIndicadores(desPendientes.size(), desAprobadas.size(), desCanceladas.size());
//
//        return DashboardSolicitudes.builder()
//                .empleado(empleado)
//                .periodoVacacional(periodo)
//                .proximoAniversario(proximoAniversario)
//                .vacaciones(vacaciones)
//                .descansos(descansos)
//                .build();
//    }
//
//
//}