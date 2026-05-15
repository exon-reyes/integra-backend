package integra.acceso.service.user;

import integra.config.mail.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final EmailService emailService;

    public void enviarCredenciales(String destinatario,
                                   String nombre,
                                   String usuario,
                                   String contrasena) throws Exception {

        int anio = java.time.Year.now().getValue();
        String html = CredencialesEmailTemplate.generar(nombre, usuario, contrasena, anio);

        emailService.sendHtmlEmail(
                destinatario,
                "Tus credenciales de acceso a la plataforma",
                html
        );
    }
}