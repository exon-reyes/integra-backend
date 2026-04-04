package integra.vacacion.service.query;

import integra.model.Empleado;
import integra.vacacion.core.EstatusPeriodo;
import integra.vacacion.domain.model.EstatusSolicitud;
import integra.vacacion.dto.response.DashboardGestionSolicitudResponse;
import integra.vacacion.dto.response.GestionSolicitudResponse;
import integra.vacacion.dto.response.GestionSolicitudResponse.DiaSolicitado;
import integra.vacacion.dto.response.GestionSolicitudResponse.Indicadores;
import integra.vacacion.dto.response.GestionSolicitudResponse.Responsable;
import integra.vacacion.dto.response.GestionSolicitudResponse.SolicitudAgrupada;
import integra.vacacion.dto.response.PeriodoVacacional;
import integra.vacacion.entity.EmpleadoTiempoEntity;
import integra.vacacion.repository.EmpleadoTiempoEntityRepository;
import integra.vacacion.repository.PeriodoVacacionalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GestionSolicitudQueryService {

    private final EmpleadoTiempoEntityRepository repository;
    private final PeriodoVacacionalRepository periodoVacacionalRepository;

    public DashboardGestionSolicitudResponse obtenerDashboardGestion() {
        List<EmpleadoTiempoEntity> registros = repository.findAll();

        int total = registros.size();
        int pendientes = 0;
        int aprobados = 0;
        int rechazados = 0;

        for (EmpleadoTiempoEntity registro : registros) {
            EstatusSolicitud estatus = registro.getEstatus();

            if (estatus == EstatusSolicitud.PENDIENTE || estatus == EstatusSolicitud.CREADA) {
                pendientes++;
            } else if (estatus == EstatusSolicitud.APROBADA || estatus == EstatusSolicitud.VIGENTE) {
                aprobados++;
            } else if (estatus == EstatusSolicitud.CANCELADA) {
                rechazados++;
            }
        }
        var dataEmpleados = periodoVacacionalRepository.findByEstatus(EstatusPeriodo.VIGENTE);
        List<Empleado> empleados = new ArrayList<>(dataEmpleados.size());
        dataEmpleados.forEach(data -> {
            Empleado empleado = new Empleado(data.getEmpleado().getId());
            empleado.setNombreCompleto(data.getEmpleado().getNombreCompleto());

            PeriodoVacacional periodo=new PeriodoVacacional();
            periodo.setAnioGestion(data.getAnioGestion());
            periodo.setDiasHabilitados(data.getDiasHabilitados());
            periodo.setDiasTomados(data.getDiasTomados());

            empleado.setPeriodoVacacional(periodo);
            empleados.add(empleado);
        });
        List<GestionSolicitudResponse> solicitudesRecientes = obtenerSolicitudesRecientes();

        return new DashboardGestionSolicitudResponse(total, pendientes, aprobados, rechazados,empleados,solicitudesRecientes);
    }

    public List<GestionSolicitudResponse> obtenerSolicitudesVigentes() {
        List<EmpleadoTiempoEntity> registros = repository.findByPeriodoEstatusConRelaciones(EstatusPeriodo.VIGENTE);

        Map<Integer, List<EmpleadoTiempoEntity>> porEmpleado = registros.stream()
                .collect(Collectors.groupingBy(et -> et.getEmpleado().getId()));

        return porEmpleado.entrySet().stream().map(entry -> mapear(entry.getKey(), entry.getValue())).toList();
    }

    private List<GestionSolicitudResponse> obtenerSolicitudesRecientes() {
        List<EmpleadoTiempoEntity> recientes = repository.findTop5ByPeriodoEstatusRecientes(EstatusPeriodo.VIGENTE);

        Map<Integer, List<EmpleadoTiempoEntity>> porEmpleado = recientes.stream()
                .collect(Collectors.groupingBy(et -> et.getEmpleado().getId()));

        return porEmpleado.entrySet().stream()
                .map(entry -> mapear(entry.getKey(), entry.getValue()))
                .toList();
    }

    private GestionSolicitudResponse mapear(Integer empleadoId, List<EmpleadoTiempoEntity> registros) {

        var empleado = registros.getFirst().getEmpleado();
        int diasHabilitados, diasDisponibles, diasTomados;
        diasHabilitados = registros.getFirst().getPeriodo().getDiasHabilitados();
        diasTomados = registros.getFirst().getPeriodo().getDiasTomados();
        diasDisponibles = registros.getFirst().getPeriodo().getDiasRestantes();

        String primerJefeNombre = empleado.getJefe() != null ? empleado.getJefe()
                .getNombreCompleto() : null;
        String segundoJefeNombre = empleado.getSegundoJefe() != null ? empleado.getSegundoJefe()
                .getNombreCompleto() : null;

        Map<Long, List<EmpleadoTiempoEntity>> porFolio = registros.stream()
                .collect(Collectors.groupingBy(et -> et.getFolio() != null ? et.getFolio() : et.getId()));

        List<SolicitudAgrupada> solicitudes = porFolio.entrySet().stream().map(entry -> {
            Long folio = entry.getKey();
            List<EmpleadoTiempoEntity> dias = entry.getValue();
            var primero = dias.getFirst();

            List<DiaSolicitado> diasDto = dias.stream()
                    .map(et -> new DiaSolicitado(et.getId(), et.getFecha()
                            .toString(), resolverEstatusDia(et), new Responsable(primerJefeNombre, et.getEstatusJefe() != null ? et.getEstatusJefe()
                            .name() : null), new Responsable(segundoJefeNombre, et.getEstatusRrhh() != null ? et.getEstatusRrhh()
                            .name() : null)))
                    .toList();

            return new SolicitudAgrupada(folio, primero.getTipo()
                    .name(), calcularEstatusSolicitud(dias), diasDto, calcularIndicadores(diasDto));
        }).toList();

        return new GestionSolicitudResponse(empleadoId, empleado.getNombreCompleto(), empleado.getUnidad() != null ? empleado.getUnidad()
                .getNombreCompleto() : null, primerJefeNombre, segundoJefeNombre, solicitudes, solicitudes.size(), diasHabilitados, diasDisponibles, diasTomados);
    }

    private String resolverEstatusDia(EmpleadoTiempoEntity et) {
        if (et.getEstatus() == EstatusSolicitud.APROBADA && et.getFecha().isBefore(LocalDate.now())) {
            return "DISFRUTADA";
        }
        return et.getEstatus().name();
    }

    private String calcularEstatusSolicitud(List<EmpleadoTiempoEntity> dias) {
        boolean todosAprobados = dias.stream().allMatch(et -> et.getEstatus() == EstatusSolicitud.APROBADA);
        if (todosAprobados) {
            boolean todosDisfrutados = dias.stream().allMatch(et -> et.getFecha().isBefore(LocalDate.now()));
            return todosDisfrutados ? "DISFRUTADA" : "APROBADA";
        }

        boolean algunoPendiente = dias.stream().anyMatch(et -> et.getEstatus() == EstatusSolicitud.PENDIENTE);
        if (algunoPendiente) return "PENDIENTE";

        return dias.getFirst().getEstatus().name();
    }

    private Indicadores calcularIndicadores(List<DiaSolicitado> dias) {
        int aprobados = 0, pendientes = 0, cancelados = 0, disfrutados = 0;
        for (DiaSolicitado d : dias) {
            switch (d.estatus()) {
                case "APROBADA" -> aprobados++;
                case "PENDIENTE" -> pendientes++;
                case "CANCELADA" -> cancelados++;
                case "DISFRUTADA" -> disfrutados++;
            }
        }
        return new Indicadores(dias.size(), aprobados, pendientes, cancelados, disfrutados);
    }
}
