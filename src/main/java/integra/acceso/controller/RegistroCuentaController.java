package integra.acceso.controller;

import integra.acceso.service.account.AccountRegistrationService;
import integra.utils.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class RegistroCuentaController {
    private final AccountRegistrationService registrationService;

    @PostMapping("/register-request")
    public ResponseEntity<ResponseData<?>> requestRegistration(@RequestBody Map<String, String> request) {
        String employeeCode = request.get("employeeCode");
        if (employeeCode != null && !employeeCode.isBlank()) {
            return ResponseEntity.ok(ResponseData.of(null, registrationService.initiateRegistration(employeeCode)));
        }
        // Always return OK
        return ResponseEntity.ok(ResponseData.of(true, "Si el colaborador es válido y no tiene cuenta, se enviará un correo."));
    }

    @PostMapping("/validate-registration-token")
    public ResponseEntity<?> validateToken(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        try {
            registrationService.validateToken(token);
            return ResponseEntity.ok(Map.of("valid", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("valid", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/register-confirm")
    public ResponseEntity<?> confirmRegistration(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String username = request.get("username");
        String password = request.get("password");

        try {
            registrationService.completeRegistration(token, username, password);
            return ResponseEntity.ok(Map.of("message", "Cuenta creada exitosamente."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error interno al crear la cuenta."));
        }
    }
}