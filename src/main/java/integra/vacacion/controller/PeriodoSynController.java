package integra.vacacion.controller;

import integra.vacacion.service.command.AniversarioScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("vacaciones/sync")
public class PeriodoSynController {

    private final AniversarioScheduler scheduler;

    @PostMapping("/periodos")
    public ResponseEntity<Void> sincronizarPeriodos() {
        scheduler.ejecutarManual();
        return ResponseEntity.ok().build();
    }
}
