package integra.vacacion.controller;

import integra.empleado.util.FiltroEmpleado;
import integra.utils.PageResponse;
import integra.vacacion.service.EmpleadoVinculaciones;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("vacaciones/vinculacion")
public class EmpleadoVinculacionController {

    private final EmpleadoVinculaciones service;

    @GetMapping
    public PageResponse<?> getVinculaciones(FiltroEmpleado filtro) {
        return service.getEmpleadoVinculaciones(filtro);
    }
}
