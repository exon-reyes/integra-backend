package integra.empleado.service;

import integra.asistencia.service.WorkImageService;
import integra.empleado.entity.EmpleadoEntity;
import integra.empleado.exception.EmpleadoException;
import integra.empleado.repository.EmpleadoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@Transactional
@Slf4j
public class ActualizarAvatarEmpleado {
    private static final String AVATAR_DEFAULT = "avatar1.svg";
    private final EmpleadoRepository empleadoRepository;
    private final WorkImageService avatarImageService;

    public ActualizarAvatarEmpleado(EmpleadoRepository empleadoRepository, @Qualifier("avatarImageService") WorkImageService avatarImageService) {
        this.empleadoRepository = empleadoRepository;
        this.avatarImageService = avatarImageService;
    }

    public void actualizarAvatar(Integer idEmpleado, String avatarName, String base64Image) {

        EmpleadoEntity empleado = empleadoRepository.findById(idEmpleado)
                .orElseThrow(() -> EmpleadoException.notFound(idEmpleado.longValue()));

        if (base64Image != null && !base64Image.isEmpty()) {
            try {
                String imagePath = avatarImageService.saveImg(base64Image, idEmpleado);
                empleado.setPathAvatar(imagePath);
                log.info("Avatar image saved for employee {}", idEmpleado);
            } catch (IOException e) {
                throw new RuntimeException("Error al guardar la imagen del avatar, contacte al administrador", e);
            }
        } else if (avatarName != null && !avatarName.trim().isEmpty()) {
            empleado.setPathAvatar(avatarName.trim());
            log.info("Avatar name updated for employee {}", idEmpleado);
        } else {
            throw new IllegalArgumentException("Debe proporcionar un nombre de avatar o una imagen en base64");
        }
    }

    public void eliminarAvatar(Integer idEmpleado) {
        EmpleadoEntity empleado = empleadoRepository.findById(idEmpleado)
                .orElseThrow(() -> EmpleadoException.notFound(idEmpleado.longValue()));

        String pathAvatar = empleado.getPathAvatar();
        if (pathAvatar != null) {
            if (!pathAvatar.endsWith(".svg")) {
                avatarImageService.deleteImg(pathAvatar);
            }
            empleado.setPathAvatar(null);
            empleadoRepository.save(empleado);
            log.info("Avatar deleted for employee {}", idEmpleado);
        }
    }

    @Transactional(readOnly = true)
    public Resource obtenerAvatarEnBytes(Integer idEmpleado) {

        String pathAvatar = empleadoRepository.findAvatarById(idEmpleado).orElse(null);

        if (pathAvatar == null || pathAvatar.endsWith(".svg")) {
            return null; // El controller manejará esto
        }

        try {
            return avatarImageService.getResizedImg(pathAvatar, 250, 250);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
