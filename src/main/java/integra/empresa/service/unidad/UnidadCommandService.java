package integra.empresa.service.unidad;

import integra.empresa.entity.UnidadEntity;
import integra.empresa.repository.UnidadRepository;
import integra.empresa.request.ActualizarUnidad;
import integra.empresa.request.NuevaUnidad;
import integra.empresa.unidad.exception.UnidadException;
import integra.empresa.validator.UnidadValidator;

import integra.model.Unidad;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UnidadCommandService {

    private final UnidadRepository writeRepository;
    private final UnidadValidator validator;

    // --- REGISTRAR ---
    @Transactional
    @CacheEvict(value = {"unidadBuscar", "unidadContacto", "unidadInfo"}, allEntries = true)
    public Unidad registrarUnidad(NuevaUnidad params) {
        validator.checkExisteClaveUnidad(params.getClave());
        validator.checkExisteTelefono(params.getTelefono());
        validator.checkExisteNombre(params.getNombre());
        validator.checkExisteEmail(params.getEmail());

        UnidadEntity unidad = new UnidadEntity(params);

        try {
            var result=writeRepository.saveAndFlush(unidad);
            return new Unidad(result.getId(), result.getClave(), result.getNombreCompleto());
        } catch (DataIntegrityViolationException ex) {
            log.error("Violación de integridad al registrar unidad: {}", ex.getMessage(), ex);

            String message = ex.getMostSpecificCause().getMessage();
            if (message == null) throw ex;

            if (message.contains("FK_unidad_zona")) {
                throw UnidadException.zonaNotFound(params.getIdZona());
            }

            if (message.contains("FK_unidad_estado")) {
                throw UnidadException.estadoNotFound(params.getIdEstado());
            }

            verificarDuplicado(ex, params);
            throw ex;
        }
    }

    // --- ACTUALIZAR ---
    @Transactional
    @CacheEvict(value = {"unidadBuscar", "unidadBuscar", "unidadContacto", "unidadInfo"}, allEntries = true)
    public void actualizarUnidad(ActualizarUnidad params) {
        UnidadEntity unidadToUpdate = new UnidadEntity(params);

        try {
            writeRepository.saveAndFlush(unidadToUpdate);
        } catch (DataIntegrityViolationException | PersistenceException ex) {
            log.error("Error al actualizar unidad ({}): {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
            verificarDuplicado(ex, params);
            throw ex;
        } catch (Exception ex) {
            log.error("Excepción general al actualizar unidad: {} - {}", ex.getClass()
                    .getSimpleName(), ex.getMessage(), ex);
            Throwable cause = ex;
            while (cause != null) {
                if (verificarDuplicado(cause, params)) break;
                cause = cause.getCause();
            }
            throw ex;
        }
    }

    // --- CAMBIO DE ESTATUS ---
    @Transactional
    @CacheEvict(value = {"unidadBuscar", "unidadContacto", "unidadInfo"}, allEntries = true)
    public void actualizarEstatusUnidad(Integer id, Boolean estatus) {
        writeRepository.updateActivoById(estatus, id);
        log.info("Unidad {}: {}", estatus ? "habilitada" : "deshabilitada", id);
    }

    // --- ELIMINAR ---
    @Transactional
    @CacheEvict(value = {"unidadBuscar", "unidadContacto", "unidadInfo"}, allEntries = true)
    public void eliminarUnidad(int id) {
        try {
            writeRepository.deleteById(id);
            log.info("Unidad eliminada correctamente: {}", id);
        } catch (DataIntegrityViolationException e) {
            log.error("Error al eliminar unidad {}: {}", id, e.getMessage());
            throw UnidadException.hasEmployees((long) id);
        }
    }

    // --- DETECTOR DE DUPLICADOS ---
    private boolean verificarDuplicado(Throwable ex, NuevaUnidad data) {
        String message = ex.getMessage();
        if (message == null) return false;

        if (message.contains("idx_clave_unidad") && message.contains("Duplicate entry")) {
            throw UnidadException.duplicateCode(data.getClave());
        }
        if (message.contains("idx_unidad_email") && message.contains("Duplicate entry")) {
            throw UnidadException.duplicateEmail(data.getEmail());
        }
        if (message.contains("idx_nombre_unidad") && message.contains("Duplicate entry")) {
            throw UnidadException.duplicateNombre(data.getNombre());
        }
        if (message.contains("idx_telefono") && message.contains("Duplicate entry")) {
            throw UnidadException.duplicateTelefono(data.getTelefono());
        }
        if (message.contains("idx_codigo_autorizacion_kiosco") && message.contains("Duplicate entry")) {
            throw UnidadException.duplicateCodigoAutorizacion("El código de autorización ya está en uso");
        }
        if (message.contains("email") && message.contains("Duplicate entry")) {
            throw UnidadException.duplicateEmail(data.getEmail());
        }
        return false;
    }
}
