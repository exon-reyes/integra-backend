package integra.vacacion.domain.service;

import integra.vacacion.dto.response.CalculoDiasDTO;
import integra.vacacion.repository.DescansoEmpleadoRepository;
import integra.vacacion.repository.FestivoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CalculoDiasLaboralesService {

    private final FestivoRepository festivoRepository;
    private final DescansoEmpleadoRepository descansoRepository;

    public CalculoDiasDTO calcular(LocalDate inicio, LocalDate fin, Integer empleadoId, Integer saldoDisponible) {
        if (inicio == null || fin == null || inicio.isAfter(fin)) {
            return new CalculoDiasDTO(null, null, 0, 0, 0, 0, saldoDisponible, false,
                    null, null, "Rango de fechas inválido");
        }

        Set<LocalDate> fechasDescanso = descansoRepository.findFechasDescansoByEmpleado(empleadoId);
        List<LocalDate> festivosEnRango = festivoRepository.findFestivosBetween(inicio, fin)
                .stream().map(festivo -> festivo.getFecha()).toList();

        long diasNaturales = ChronoUnit.DAYS.between(inicio, fin) + 1;

        int diasLaborables = 0;
        int diasFestivosExcluidos = 0;
        int diasDescansoExcluidos = 0;

        List<LocalDate> diasFestivosList = new ArrayList<>();
        List<LocalDate> diasDescansoList = new ArrayList<>();

        LocalDate fecha = inicio;
        while (!fecha.isAfter(fin)) {
            boolean esFestivo = festivosEnRango.contains(fecha);
            boolean esDescanso = fechasDescanso.contains(fecha);

            if (esFestivo) {
                diasFestivosExcluidos++;
                diasFestivosList.add(fecha);
            }

            if (esDescanso) {
                diasDescansoExcluidos++;
                diasDescansoList.add(fecha);
            }

            if (!esFestivo && !esDescanso) {
                diasLaborables++;
            }

            fecha = fecha.plusDays(1);
        }

        boolean puedeSolicitar = diasLaborables <= saldoDisponible;
        String mensajeError = null;

        if (!puedeSolicitar) {
            mensajeError = String.format("Días solicitados (%d) exceden el saldo disponible (%d)",
                    diasLaborables, saldoDisponible);
        }

        return new CalculoDiasDTO(
                inicio, fin,
                (int) diasNaturales,
                diasLaborables,
                diasFestivosExcluidos,
                diasDescansoExcluidos,
                saldoDisponible,
                puedeSolicitar,
                diasFestivosList,
                diasDescansoList,
                mensajeError
        );
    }

    public int calcularDiasLaborables(LocalDate inicio, LocalDate fin, Integer empleadoId) {
        Set<LocalDate> fechasDescanso = descansoRepository.findFechasDescansoByEmpleado(empleadoId);
        List<LocalDate> festivos = festivoRepository.findFestivosBetween(inicio, fin)
                .stream().map(f -> f.getFecha()).toList();

        int diasLaborables = 0;
        LocalDate fecha = inicio;
        while (!fecha.isAfter(fin)) {
            if (!festivos.contains(fecha) && !fechasDescanso.contains(fecha)) {
                diasLaborables++;
            }
            fecha = fecha.plusDays(1);
        }

        return diasLaborables;
    }
}
