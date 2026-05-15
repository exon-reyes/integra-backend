package integra.vacacion.service;

import integra.empleado.query.EmpleadoAniversarioInfo;
import integra.empleado.repository.EmpleadoRepository;
import integra.vacacion.dto.request.FiltroAniversario;
import integra.vacacion.dto.response.EmpleadoAniversarioDTO;
import integra.vacacion.util.VacacionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AniversarioService {

    private final EmpleadoRepository empleadoRepository;

    public List<EmpleadoAniversarioDTO> obtenerAniversariosDelMes(FiltroAniversario filtro) {
        YearMonth yearMonth = YearMonth.of(filtro.getAnio(), filtro.getMes());
        LocalDate fechaInicio = yearMonth.atDay(1);
        LocalDate fechaFin = yearMonth.atEndOfMonth();

        List<EmpleadoAniversarioInfo> empleados = empleadoRepository.findEmpleadosParaAniversario("B");
        List<EmpleadoAniversarioDTO> result = new ArrayList<>();

        for (EmpleadoAniversarioInfo emp : empleados) {
            if (!cumpleFiltro(emp, filtro)) continue;

            LocalDate fechaIngreso = emp.fechaReingreso() != null ? emp.fechaReingreso() : emp.fechaAlta();
            LocalDate proximoAniversario = calcularAniversarioEnAnio(fechaIngreso, filtro.getAnio());

            int aniosCumplidos = VacacionUtil.calcularAntiguedad(fechaIngreso);
            if (aniosCumplidos < 1) continue;

            if (!proximoAniversario.isBefore(fechaInicio) && !proximoAniversario.isAfter(fechaFin)) {

                result.add(new EmpleadoAniversarioDTO(
                        emp.id(), emp.codigoEmpleado(), emp.nombreCompleto(),
                        emp.fechaAlta(), emp.fechaReingreso(),
                        aniosCumplidos, proximoAniversario,
                        fechaIngreso.getMonthValue(),
                        emp.unidadNombreCompleto(), emp.puestoNombre()
                ));
            }
        }

        return result;
    }

    private boolean cumpleFiltro(EmpleadoAniversarioInfo emp, FiltroAniversario filtro) {
        if (filtro.getResponsableId() != null && !Objects.equals(emp.jefeId(), filtro.getResponsableId())) {
            return false;
        }
        if (filtro.getSupervisorId() != null && !Objects.equals(emp.supervisorUnidadId(), filtro.getSupervisorId())) {
            return false;
        }
        return true;
    }

    private LocalDate calcularAniversarioEnAnio(LocalDate fechaIngreso, int anioObjetivo) {
        return fechaIngreso.withYear(anioObjetivo);
    }

}
