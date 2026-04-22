package integra.asistencia.controller;

import integra.asistencia.actions.EmpleadoJornada;
import integra.asistencia.exception.AsistenciaDomainException;
import integra.asistencia.facade.ObtenerEmpleadoJornada;
import integra.global.exception.code.ErrorCode;
import integra.utils.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Pablo Reyes
 * @version 1.1
 * @since 1.0
 */
@RestController
@RequestMapping("/asistencia")
@RequiredArgsConstructor
public class AsistenciaQueryController {

    private final ObtenerEmpleadoJornada jornadaService;

    @GetMapping("/{nip}")
    public ResponseEntity<ResponseData<EmpleadoJornada>> consultar(@PathVariable String nip) {
        if (nip == null || nip.trim().isEmpty()) {
            throw new AsistenciaDomainException(ErrorCode.VAL_MISSING_FIELD, "El PIN no puede estar vacío");
        }
            EmpleadoJornada result = jornadaService.execute(nip);
            return ResponseEntity.ok(ResponseData.of(result, "Jornada de empleado consultado"));

    }

    @GetMapping("{id}/perfil")
    public ResponseEntity<ResponseData<EmpleadoJornada>> consultarPorId(@PathVariable Integer id) {
        if (id == null) {
            throw new AsistenciaDomainException(ErrorCode.VAL_MISSING_FIELD, "El ID no puede estar vacío");
        }

        try {
            EmpleadoJornada result = jornadaService.obtenerPorId(id);
            return ResponseEntity.ok(ResponseData.of(result, "Jornada de empleado consultado"));
        } catch (Exception e) {
            throw new RuntimeException("Error al consultar la jornada del empleado", e);
        }
    }

}
