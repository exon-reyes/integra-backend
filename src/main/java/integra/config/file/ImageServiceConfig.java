package integra.config.file;

import integra.asistencia.service.WorkImageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ImageServiceConfig {

    @Bean
    @Primary
    public WorkImageService workTimeImageService() {
        return new WorkImageService("img/asistencia", "RELOJ CHECADOR");
    }

    @Bean
    public WorkImageService avatarImageService() {
        return new WorkImageService("img/avatars", "AVATARS");
    }
}
