package integra.empleado.controller;

import integra.empleado.service.ConsultarCatalogoEmpleados;
import integra.empleado.util.FiltroEmpleado;
import integra.model.Empleado;
import integra.utils.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("empleados")
class EmpleadoController {
    private final ConsultarCatalogoEmpleados catalagoEmpleados;

    @GetMapping()
    public ResponseEntity<ResponseData<List<Empleado>>> obtenerEmpleados(FiltroEmpleado filtro) {
        return ResponseEntity.ok(ResponseData.of(catalagoEmpleados.consultarConFiltro(filtro), "Empleado"));
    }

    @GetMapping("supervisores")
    public ResponseEntity<ResponseData<List<Empleado>>> obtenerSupervisores(@RequestParam(required = false) Boolean activos) {
        return ResponseEntity.ok(ResponseData.of(catalagoEmpleados.obtenerSupervisores(activos), "Supervisores activos"));
    }
    @GetMapping("{id}/detalles")
    public ResponseEntity<ResponseData<Empleado>> obtenerDetallesEmpleado(@PathVariable Integer id) {
        return ResponseEntity.ok(ResponseData.of(catalagoEmpleados.obtenerDetalles(id), "Detalles de empleado"));
    }


}
