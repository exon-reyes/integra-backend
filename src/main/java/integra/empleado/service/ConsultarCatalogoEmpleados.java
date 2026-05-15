package integra.empleado.service;

import integra.core.service.ParamsDataProxy;
import integra.empleado.FiltroEmpleadoService;
import integra.empleado.exception.EmpleadoException;
import integra.empleado.query.InfoCatalogoEmpleados;
import integra.empleado.repository.EmpleadoRepository;
import integra.empleado.util.FiltroEmpleado;
import integra.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ConsultarCatalogoEmpleados {
    private final FiltroEmpleadoService filtroEmpleadoService;
    private final ParamsDataProxy systemIdProvider;
    private final EmpleadoRepository empleadoRepository;

    public Empleado obtenerDetalles(Integer id) {
        var data = empleadoRepository.findById(id).orElseThrow(() -> EmpleadoException.notFound(Long.valueOf(id)));
        Empleado empleado = new Empleado(data.getId(), data.getCodigoEmpleado(), data.getNombreCompleto());
        empleado.setNombre(data.getNombre(), data.getApellidoPaterno(), data.getApellidoMaterno());
        empleado.setPuesto(new Puesto(data.getPuesto().getId(), data.getPuesto().getNombre()));
        empleado.setUnidad(new Unidad(data.getUnidad().getId(), data.getUnidad().getNombreCompleto()));
        empleado.setEstatus(data.getEstatus());
        empleado.setFechaAlta(data.getFechaAlta());
        empleado.setFechaBaja(data.getFechaBaja());
        empleado.setFechaReingreso(data.getFechaReingreso());
        empleado.setSexo(data.getSexo());
        empleado.setContacto(new Contacto(data.getTelefono(), data.getEmail()));
        empleado.setAvatar(data.getPathAvatar());

        var dataDepartamento = data.getDepartamento();
        if (dataDepartamento != null) {
            empleado.setDepartamento(new Departamento(dataDepartamento.getId(), dataDepartamento.getNombre()));
        }
        empleado.setPrimerJefe(new Gestor(data.getJefe().getId(), data.getJefe().getNombreCompleto(), 1));
        empleado.setSegundoJefe(new Gestor(data.getSegundoJefe().getId(), data.getSegundoJefe()
                .getNombreCompleto(), 2));
        return empleado;
    }

    public List<Empleado> consultar(FiltroEmpleado filtros) {
        return filtroEmpleadoService.obtenerConFiltro(filtros).stream().map(this::mapToEmpleado).toList();
    }

//    public Empleado obtenerEmpleado(Integer id) {
//        return empleadoRepository.findById(id, InfoCatalogoEmpleados.class)
//                .map(this::mapToEmpleado)
//                .orElseThrow(() -> EmpleadoException.notFound(Long.valueOf(id)));
//    }

    public List<Empleado> obtenerSupervisores(Boolean activos) {
        FiltroEmpleado filtro = new FiltroEmpleado();
        filtro.setIdPuesto(systemIdProvider.getIdPuestoSupervisor());
        filtro.setActivos(activos);
        return consultar(filtro);
    }

    private Empleado mapToEmpleado(InfoCatalogoEmpleados data) {
        Empleado empleado = new Empleado(data.id(), data.codigoEmpleado(), data.nombreCompleto());
        empleado.setPuesto(new Puesto(data.puestoId(), data.puestoNombre()));
        empleado.setUnidad(new Unidad(data.unidadId(), data.unidadNombreCompleto()));
        empleado.setEstatus(data.estatus());
        empleado.setFechaAlta(data.fechaAlta());
        empleado.setFechaBaja(data.fechaBaja());
        empleado.setFechaReingreso(data.fechaReingreso());
        return empleado;
    }
}
