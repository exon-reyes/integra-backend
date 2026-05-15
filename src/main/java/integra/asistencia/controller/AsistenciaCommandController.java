package integra.asistencia.controller;

import integra.asistencia.actions.*;
import integra.asistencia.service.jornada.*;
import integra.utils.ResponseData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/asistencia")
@RequiredArgsConstructor
public class AsistenciaCommandController {
    private final IniciarJornada iniciarJornada;
    private final IniciarPausa iniciarPausa;
    private final FinalizarJornada finalizarJornada;
    private final FinalizarPausa finalizarPausa;
    private final RegistroManual registroManual;
    private final ActualizarJornada actualizarJornada;
    private final ActualizarPausa actualizarPausa;
    private final EliminarJornada eliminarJornada;
    private final EliminarPausa eliminarPausa;
    private final CrearJornadaCompleta crearJornadaCompleta;
    private final CrearPausaCompleta crearPausaCompleta;

    @PostMapping(value = "iniciar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseData<?>> iniciar(
            @RequestPart("datos") @Valid RegistroDTO dto,
            @RequestPart(value = "foto", required = false) MultipartFile foto) {
        iniciarJornada.execute(new IniciarJornadaCommand(dto.getEmpleadoId(), foto,
                dto.getUnidadId(), dto.getUnidadAsignadaId(), dto.getHora()));
        return success("Jornada iniciada");
    }

    @PostMapping(value = "/finalizar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseData<?>> finalizar(
            @RequestPart("datos") @Valid RegistroDTO dto,
            @RequestPart(value = "foto", required = false) MultipartFile foto) {
        finalizarJornada.execute(new FinalizarJornadaCommand(dto.getEmpleadoId(), foto,
                dto.getUnidadId(), dto.getFinDeposito(), dto.getUnidadAsignadaId(), null));
        return success("Jornada finalizada");
    }

    @PostMapping(value = "/pausa/iniciar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseData<?>> iniciarPausa(
            @RequestPart("datos") @Valid RegistroDTO dto,
            @RequestPart(value = "foto", required = false) MultipartFile foto) {
        iniciarPausa.execute(new IniciarPausaCommand(dto.getEmpleadoId(), dto.getPausa(),
                foto, dto.getUnidadId(), dto.getUnidadAsignadaId(), null));
        return success("Pausa iniciada");
    }

    @PostMapping(value = "/pausa/finalizar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseData<?>> finalizarPausa(
            @RequestPart("datos") @Valid RegistroDTO dto,
            @RequestPart(value = "foto", required = false) MultipartFile foto) {
        finalizarPausa.execute(new FinalizarPausaCommand(dto.getEmpleadoId(), dto.getPausa(),
                foto, dto.getUnidadId(), dto.getUnidadAsignadaId(), null));
        return success("Pausa registrada");
    }

    @PostMapping("/manual")
    public ResponseEntity<ResponseData<?>> registroManual(@Valid @RequestBody RegistroManualDTO request) {
        registroManual.execute(request);
        return success("Asistencia registrada");
    }

    @PutMapping("/jornada")
    public ResponseEntity<ResponseData<?>> actualizarJornada(@Valid @RequestBody ActualizarJornadaDTO dto) {
        actualizarJornada.execute(dto);
        return success("Jornada actualizada");
    }

    @PutMapping("/pausa")
    public ResponseEntity<ResponseData<?>> actualizarPausa(@Valid @RequestBody ActualizarPausaDTO dto) {
        actualizarPausa.execute(dto);
        return success("Pausa actualizada");
    }

    @DeleteMapping("/jornada/{id}")
    public ResponseEntity<ResponseData<?>> eliminarJornada(@PathVariable Integer id) {
        eliminarJornada.execute(id);
        return success("Jornada eliminada");
    }

    @DeleteMapping("/pausa/{id}")
    public ResponseEntity<ResponseData<?>> eliminarPausa(@PathVariable Integer id) {
        eliminarPausa.execute(id);
        return success("Pausa eliminada");
    }

    @PostMapping("/jornada/completa")
    public ResponseEntity<ResponseData<?>> crearJornadaCompleta(@Valid @RequestBody CrearJornadaCompletaDTO dto) {
        crearJornadaCompleta.execute(dto);
        return success("Jornada completa registrada");
    }

    @PostMapping("/pausa/completa")
    public ResponseEntity<ResponseData<?>> crearPausaCompleta(@Valid @RequestBody CrearPausaCompletaDTO dto) {
        crearPausaCompleta.execute(dto);
        return success("Pausa completa registrada");
    }

    private ResponseEntity<ResponseData<?>> success(String message) {
        return ResponseEntity.ok(ResponseData.of(null, message));
    }
}
