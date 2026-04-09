package integra.vacacion.service.solicitud;

import integra.empleado.exception.EmpleadoException;
import integra.empleado.query.EmpleadoVacacionInfo;
import integra.empleado.service.EmpleadoService;
import integra.model.Empleado;
import integra.vacacion.domain.model.*;
import integra.vacacion.dto.response.FechaSolicitud;
import integra.vacacion.dto.response.PeriodoVacacional;
import integra.vacacion.entity.PeriodoVacacionalEntity;
import integra.vacacion.entity.SolicitudDescanso;
import integra.vacacion.exception.VacacionException;
import integra.vacacion.query.EmpleadoDescansoInfo;
import integra.vacacion.repository.PeriodoVacacionalRepository;
import integra.vacacion.repository.SolicitudDescansoRepository;
import integra.vacacion.util.VacacionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ObtenerSolicitudesUsuario {
    private final SolicitudDescansoRepository solicitudRepository;
    private final EmpleadoService empleadoService;
    private final PeriodoVacacionalRepository periodoVacacionalRepository;

    public DashboardSolicitudes obtenerSolicitudes(Integer empleadoId, int anio) {
        var dataEmpleado = empleadoService.obtenerPorId(empleadoId, EmpleadoDescansoInfo.class)
                .orElseThrow(() -> EmpleadoException.notFound(Long.valueOf(empleadoId)));

        var periodo = periodoVacacionalRepository.obtenerMejorPeriodo(empleadoId)
                .orElseThrow(VacacionException::sinPeriodoActivo);

        LocalDate desde = LocalDate.of(anio, 1, 1);
        LocalDate hasta = LocalDate.of(anio, 12, 31);

        var solicitudes = solicitudRepository.obtenerSolicitudesPorAnio(empleadoId, desde, hasta);
        var tempDataEmpleado = new EmpleadoVacacionInfo(dataEmpleado.id(), dataEmpleado.fechaAlta(), dataEmpleado.fechaReingreso(), dataEmpleado.fechaBaja());
        var fechaIngreso = VacacionUtil.obtenerFechaIngreso(tempDataEmpleado);
        var proximoAniversario = VacacionUtil.proximoAniversario(fechaIngreso);

        var empleado = new Empleado(dataEmpleado.id(), dataEmpleado.nombreCompleto());
        empleado.setAntiguedadAnios(VacacionUtil.calcularAntiguedad(fechaIngreso));
        empleado.setFechaIngreso(fechaIngreso);

        return DashboardSolicitudes.builder()
                .empleado(empleado)
                .periodoVacacional(mapPeriodo(periodo))
                .vacaciones(buildVacaciones(solicitudes))
                .descansos(buildDescansos(solicitudes))
                .proximoAniversario(proximoAniversario)
                .build();
    }

    private SolicitudesVacaciones buildVacaciones(List<SolicitudDescanso> solicitudes) {
        var aprobadas = extraerDias(solicitudes, TipoSolicitud.VACACION, EstatusSolicitud.APROBADA);
        var pendientes = extraerDias(solicitudes, TipoSolicitud.VACACION, EstatusSolicitud.PENDIENTE);
        var canceladas = extraerDias(solicitudes, TipoSolicitud.VACACION, EstatusSolicitud.CANCELADA);

        var hoy = LocalDate.now();
        var disfrutadas = aprobadas.stream().filter(f -> f.getFecha().isBefore(hoy)).toList();
        var futuras = aprobadas.stream().filter(f -> !f.getFecha().isBefore(hoy)).toList();

        var result = new SolicitudesVacaciones();
        result.setDisfrutadas(disfrutadas);
        result.setAprobadas(futuras);
        result.setPendientes(pendientes);
        result.setCanceladas(canceladas);
        result.setIndicadores(disfrutadas.size(), aprobadas.size(), pendientes.size(), canceladas.size());
        return result;
    }

    private SolicitudesDescanso buildDescansos(List<SolicitudDescanso> solicitudes) {
        var aprobadas = extraerDias(solicitudes, TipoSolicitud.DESCANSO, EstatusSolicitud.APROBADA);
        var pendientes = extraerDias(solicitudes, TipoSolicitud.DESCANSO, EstatusSolicitud.PENDIENTE);
        var canceladas = extraerDias(solicitudes, TipoSolicitud.DESCANSO, EstatusSolicitud.CANCELADA);

        var hoy = LocalDate.now();
        var disfrutadas = aprobadas.stream().filter(f -> f.getFecha().isBefore(hoy)).toList();
        var futuras = aprobadas.stream().filter(f -> !f.getFecha().isBefore(hoy)).toList();

        var result = new SolicitudesDescanso();
        result.setDisfrutadas(disfrutadas);
        result.setAprobadas(futuras);
        result.setPendientes(pendientes);
        result.setCanceladas(canceladas);
        result.setIndicadores(pendientes.size(), aprobadas.size(), canceladas.size(), disfrutadas.size());
        return result;
    }

    private List<FechaSolicitud> extraerDias(List<SolicitudDescanso> solicitudes, TipoSolicitud tipo, EstatusSolicitud estatusBuscado) {
        var dias = new ArrayList<FechaSolicitud>();
        solicitudes.stream()
                .filter(s -> s.getTipoSolicitud() == tipo)
                .forEach(s -> s.getDiasSolicitudDescansos().forEach(d -> {
                    if (d.getEstatusNivel2() == estatusBuscado) {
                        dias.add(new FechaSolicitud(d.getId(), d.getFecha(), estatusBuscado.name()));
                    }
                }));
        return dias;
    }

    private PeriodoVacacional mapPeriodo(PeriodoVacacionalEntity entity) {
        var periodo = new PeriodoVacacional();
        periodo.setId(entity.getId());
        periodo.setAnioGestion(entity.getAnioGestion());
        periodo.setAnioLaboral(entity.getAnioLaboral());
        periodo.setFechaInicio(entity.getFechaInicio());
        periodo.setFechaFin(entity.getFechaFin());
        periodo.setDiasHabilitados(entity.getDiasHabilitados());
        periodo.setDiasTomados(entity.getDiasTomados());
        periodo.setDiasRestantes(entity.getDiasRestantes());
        periodo.setFechaCaducidad(entity.getFechaCaducidad());
        periodo.setEstatus(entity.getEstatus().name());
        return periodo;
    }
}
