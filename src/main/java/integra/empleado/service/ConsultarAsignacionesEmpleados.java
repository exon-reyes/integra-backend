package integra.empleado.service;

import integra.empleado.FiltroEmpleadoService;
import integra.empleado.util.FiltroEmpleado;
import integra.model.Empleado;
import integra.model.Gestor;
import integra.model.Puesto;
import integra.model.Unidad;
import integra.utils.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ConsultarAsignacionesEmpleados {

    private final FiltroEmpleadoService filtroEmpleadoService;

    public PageResponse<Empleado> consultar(FiltroEmpleado filtros) {
        return new PageResponse<>(filtroEmpleadoService.obtenerAsignacionesPaginado(filtros).map(info -> {
            Empleado empleado = new Empleado(info.id(), info.codigoEmpleado(), info.nombreCompleto());
            empleado.setPuesto(new Puesto(null, info.puestoNombre()));
            empleado.setUnidad(new Unidad(info.unidadNombreCompleto()));
            if (info.jefeNombreCompleto() != null) {
                empleado.setPrimerJefe(new Gestor(null, info.jefeNombreCompleto(), 1));
            }
            if (info.segundoJefeNombreCompleto() != null) {
                empleado.setSegundoJefe(new Gestor(null, info.segundoJefeNombreCompleto(), 2));
            }
            return empleado;
        }));
    }
}
