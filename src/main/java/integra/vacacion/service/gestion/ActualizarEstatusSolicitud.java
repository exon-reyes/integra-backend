package integra.vacacion.service.gestion;

import integra.vacacion.core.EstatusPeriodo;
import integra.vacacion.domain.model.EstatusSolicitud;
import integra.vacacion.domain.model.TipoSolicitud;
import integra.vacacion.dto.request.NuevoEstatusSolicitud;
import integra.vacacion.entity.HistorialSolicitudDescanso;
import integra.vacacion.entity.SolicitudDescanso;
import integra.vacacion.exception.VacacionException;
import integra.vacacion.repository.DiasSolicitudRepository;
import integra.vacacion.repository.HistorialSolicitudRepository;
import integra.vacacion.repository.SolicitudDescansoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class ActualizarEstatusSolicitud {

    private final SolicitudDescansoRepository solicitudRepository;
    private final HistorialSolicitudRepository historialRepository;
    private final DiasSolicitudRepository diaRepository;

    /**
     * ACTUALIZACIÓN GLOBAL (Mando Maestro)
     * Se usa cuando el gestor aprueba/cancela/regresa a pendiente toda la solicitud.
     */
    public void actualizarGlobal(NuevoEstatusSolicitud request) {
        var data = solicitudRepository.findById(request.getId())
                .orElseThrow(() -> VacacionException.solicitudNoEncontrada(request.getId()));

        EstatusSolicitud nuevoEstatus = request.getNuevoEstatus();
        LocalDate hoy = LocalDate.now();

        if (request.getNivel() == 2) {
            sincronizarDiasYContadores(data, nuevoEstatus, hoy);
            data.setEstatus(nuevoEstatus);
            data.setEstatusNivel2(nuevoEstatus);
            data.setFechaAccionNivel2(hoy);
        } else {
            actualizarNivel1Transitorio(data, request, hoy);
        }

        registrarHistorial(data, request, "Acción Global");
        verificarEstadoDelPeriodo(data);
        solicitudRepository.save(data);
    }

    /**
     * ACTUALIZACIÓN GRANULAR (Día por Día / Múltiples Días)
     * Se usa cuando el gestor edita días específicos de la tabla en bloque.
     */
    public void actualizarDiasGranular(NuevoEstatusSolicitud request) {
        if (request.getDiasIds() == null || request.getDiasIds().isEmpty()) return;
        var dias = diaRepository.findAllById(request.getDiasIds());
        if (dias.isEmpty()) return;

        var solicitud = dias.get(0).getFolio();
        LocalDate hoy = LocalDate.now();

        for (var dia : dias) {
            EstatusSolicitud anteriorDiaN2 = dia.getEstatusNivel2();
            EstatusSolicitud nuevoDia = request.getNuevoEstatus();

            if (request.getNivel() == 2) {
                dia.setEstatusNivel2(nuevoDia);
                dia.setFechaAccionNivel2(hoy);

                if (solicitud.getTipoSolicitud() == TipoSolicitud.VACACION && anteriorDiaN2 != nuevoDia) {
                    var periodo = solicitud.getPeriodo();

                    // Impacto en días restantes (saldo)
                    if (nuevoDia == EstatusSolicitud.CANCELADA && anteriorDiaN2 != EstatusSolicitud.CANCELADA) {
                        periodo.setDiasRestantes(periodo.getDiasRestantes() + 1);
                    } else if (anteriorDiaN2 == EstatusSolicitud.CANCELADA && nuevoDia != EstatusSolicitud.CANCELADA) {
                        periodo.setDiasRestantes(periodo.getDiasRestantes() - 1);
                    }

                    // Impacto en días tomados
                    if (nuevoDia == EstatusSolicitud.APROBADA && anteriorDiaN2 != EstatusSolicitud.APROBADA) {
                        periodo.setDiasTomados(periodo.getDiasTomados() + 1);
                    } else if (anteriorDiaN2 == EstatusSolicitud.APROBADA && nuevoDia != EstatusSolicitud.APROBADA) {
                        periodo.setDiasTomados(periodo.getDiasTomados() - 1);
                    }
                }
            } else {
                dia.setEstatusNivel1(nuevoDia);
                dia.setFechaAccionNivel1(hoy);
            }
        }

        registrarHistorial(solicitud, request, "Acción Granular en múltiples fechas (" + dias.size() + " días)");
        verificarEstadoDelPeriodo(solicitud);
        diaRepository.saveAll(dias);
    }

    private void sincronizarDiasYContadores(SolicitudDescanso data, EstatusSolicitud nuevoGlobal, LocalDate fecha) {
        boolean esVacacion = data.getTipoSolicitud() == TipoSolicitud.VACACION;
        var periodo = data.getPeriodo();

        data.getDiasSolicitudDescansos().forEach(dia -> {
            EstatusSolicitud anteriorN2 = dia.getEstatusNivel2();

            // Respetar días ya cancelados granularmente: la aprobación global no los revierte
            if (anteriorN2 == EstatusSolicitud.CANCELADA && nuevoGlobal == EstatusSolicitud.APROBADA) {
                return;
            }

            if (anteriorN2 == nuevoGlobal) return;

            dia.setEstatusNivel2(nuevoGlobal);
            dia.setFechaAccionNivel2(fecha);

            if (!esVacacion) return;

            // Actualizar contadores del periodo
            // Restantes
            if (nuevoGlobal == EstatusSolicitud.CANCELADA && anteriorN2 != EstatusSolicitud.CANCELADA) {
                periodo.setDiasRestantes(periodo.getDiasRestantes() + 1);
            } else if (anteriorN2 == EstatusSolicitud.CANCELADA && nuevoGlobal != EstatusSolicitud.CANCELADA) {
                periodo.setDiasRestantes(periodo.getDiasRestantes() - 1);
            }

            // Tomados
            if (nuevoGlobal == EstatusSolicitud.APROBADA && anteriorN2 != EstatusSolicitud.APROBADA) {
                periodo.setDiasTomados(periodo.getDiasTomados() + 1);
            } else if (anteriorN2 == EstatusSolicitud.APROBADA && nuevoGlobal != EstatusSolicitud.APROBADA) {
                periodo.setDiasTomados(periodo.getDiasTomados() - 1);
            }
        });
    }

    private void actualizarNivel1Transitorio(SolicitudDescanso data, NuevoEstatusSolicitud request, LocalDate hoy) {
        data.setEstatusNivel1(request.getNuevoEstatus());
        data.setFechaAccionNivel1(hoy);

        // Sincronización para Nivel 1
        data.getDiasSolicitudDescansos().forEach(dia -> {
            dia.setEstatusNivel1(request.getNuevoEstatus());
            dia.setFechaAccionNivel1(hoy);
        });

        // REGLA: Nivel 1 puede cancelar Globalmente si el estatus es PENDIENTE
        if (request.getNuevoEstatus() == EstatusSolicitud.CANCELADA && data.getEstatus() == EstatusSolicitud.PENDIENTE) {
            data.setEstatus(EstatusSolicitud.CANCELADA);
        }
    }

    private void verificarEstadoDelPeriodo(SolicitudDescanso data) {
        // Solo las solicitudes de VACACION pueden cambiar el estatus del periodo
        if (data.getTipoSolicitud() != TipoSolicitud.VACACION) return;

        var periodo = data.getPeriodo();
        // Solo transicionar entre VIGENTE y CONSUMIDO; no tocar VENCIDO (lo gestiona el scheduler)
        if (periodo.getEstatus() == EstatusPeriodo.VENCIDO) return;

        periodo.setEstatus(
            Objects.equals(periodo.getDiasTomados(), periodo.getDiasHabilitados())
                ? EstatusPeriodo.CONSUMIDO
                : EstatusPeriodo.VIGENTE
        );
    }

    private void registrarHistorial(SolicitudDescanso data, NuevoEstatusSolicitud request, String contexto) {
        var evento = new HistorialSolicitudDescanso();
        String actor = (request.getNivel() == 1) ? "Jefe Directo" : "Recursos Humanos";
        evento.setSolicitud(data);
        evento.setFecha(LocalDateTime.now());
        evento.setDescripcion(String.format("%s: %s por %s.", contexto, request.getNuevoEstatus(), actor));
        historialRepository.save(evento);
    }
}