package integra.acceso.service.account;

import integra.config.mail.EmailService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class EmailPasswordResetNotificador implements PasswordResetNotifier {

    private EmailService emailService;

    @Override
    public void enviarLink(String email, String username, String resetLink) {
        String htmlBody = generarHTMLEmail(username, resetLink);

        try {
            emailService.sendHtmlEmail(email, "Solicitud de restablecimiento de contraseña", htmlBody);
        } catch (Exception e) {
            log.error("Error al enviar el correo de restablecimiento de contraseña", e);
            throw new RuntimeException("Error enviando notificación", e);
        }
    }

    private String generarHTMLEmail(String username, String resetLink) {
        return "<div style=\"font-family: Arial, sans-serif; padding: 20px; color: #333;\">" +
                "<div style=\"margin-bottom: 20px;\">" +
                "<h1 style=\"color: #1e40af;\">Restablecer Contraseña</h1>" +
                "</div>" +
                "<p>Hola " + username + "</p>" +
                "<p>Hemos recibido una solicitud para restablecer la contraseña de tu cuenta en Integra.</p>" +
                "<p>Para continuar, haz clic en el siguiente botón:</p>" +
                "<div style=\"margin: 30px 0;\">" +
                "<a href='" + resetLink + "' style=\"background-color: #1e40af; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; font-weight: bold;\">Restablecer mi contraseña</a>" +
                "</div>" +
                "<p>Si no solicitaste este cambio, puedes ignorar este correo, o contactar al administrador. Tu contraseña seguirá siendo la misma.</p>" +
                "<p style=\"font-size: 12px; color: #666;\">Este enlace expirará en 15 minutos.</p>" +
                "<hr style=\"border: none; border-top: 1px solid #eee; margin: 20px 0;\" />" +
                "<p style=\"font-size: 12px; color: #999; text-align: center;\">Integra 2026</p>" +
                "<p style=\"font-size: 10px; color: #999; text-align: center;\">Este es un mensaje automático. A fin de proteger tu cuenta, no reenvíes este correo.</p>" +
                "</div>";
    }
}