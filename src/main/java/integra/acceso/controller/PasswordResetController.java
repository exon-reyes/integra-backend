package integra.acceso.controller;

import integra.acceso.command.PasswordReset;
import integra.acceso.command.PasswordResetCompletion;
import integra.acceso.service.account.ComplearPasswordReset;
import integra.acceso.service.account.InitiatePasswordResetUseCase;
import integra.acceso.service.account.ValidateTokenUseCase;
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
public class PasswordResetController {
    private final InitiatePasswordResetUseCase initiatePasswordResetUseCase;
    private final ValidateTokenUseCase validateTokenUseCase;
    private final ComplearPasswordReset completePasswordResetUseCase;

    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseData<?>> forgotPassword(@RequestBody Map<String, String> request) {
        String username = request.get("username"); // Handles both username and email
        if (username != null && !username.isBlank()) {
            initiatePasswordResetUseCase.execute(new PasswordReset(username));
        }
        return ResponseEntity.ok(ResponseData.of(true, "Si el correo está registrado, recibirás en breve el enlace de recuperación. Puede tardar un momento."));

    }

    @PostMapping("/validate-reset-token")
    public ResponseEntity<?> validateToken(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        try {
            validateTokenUseCase.execute(token);
            return ResponseEntity.ok(Map.of("valid", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("valid", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");

        try {
            completePasswordResetUseCase.execute(new PasswordResetCompletion(token, newPassword));
            return ResponseEntity.ok(Map.of("message", "Contraseña actualizada correctamente."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
