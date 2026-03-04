package integra.empresa.controller;

import integra.acceso.util.Autoridades;
import integra.empresa.query.unidad.FiltroUnidad;
import integra.empresa.query.unidad.InfoUnidad;
import integra.empresa.service.unidad.UnidadQueryService;
import integra.model.HorarioOperativo;
import integra.model.Unidad;
import integra.utils.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("unidades")
@RequiredArgsConstructor
class CatalogoUnidadController {
    private final UnidadQueryService queryService;

    @GetMapping("filtro")
    public ResponseEntity<ResponseData<List<Unidad>>> obtenerUnidadesPorFiltro(FiltroUnidad filtro) {
        return ResponseEntity.ok(ResponseData.of(queryService.buscar(filtro.supervisorId(), filtro.zonaId(), filtro.activo()), "Unidades filtradas"));
    }

    @GetMapping("contacto/{id}")
    @PreAuthorize(Autoridades.UNIDADES_CONSULTAR)
    public ResponseEntity<ResponseData<Unidad>> obtenerContactoUnidad(@PathVariable Integer id) {
        return ResponseEntity.ok(ResponseData.of(queryService.obtenerContacto(id), "Información de contacto"));
    }

    @GetMapping("horario/{id}")
    public ResponseEntity<ResponseData<List<HorarioOperativo>>> obtenerHorarioUnidad(@PathVariable Integer id) {
        return ResponseEntity.ok(ResponseData.of(queryService.obtenerHorario(id), "Información de horario"));
    }
}