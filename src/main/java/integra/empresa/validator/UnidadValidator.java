package integra.empresa.validator;

import integra.empresa.repository.UnidadRepository;
import integra.empresa.unidad.exception.UnidadException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UnidadValidator {
    private final UnidadRepository readRepository;

    public void checkExisteClaveUnidad(String clave) {
        if (clave != null && readRepository.existsByClave(clave)) {
            throw UnidadException.duplicateCode(clave);
        }
    }

    public void checkExisteTelefono(String telefono) {
        if (telefono != null && readRepository.existsByTelefono(telefono)) {
            throw UnidadException.duplicateTelefono(telefono);
        }
    }

    public void checkExisteNombre(String nombre) {
        if (nombre != null && readRepository.existsByNombre(nombre)) {
            throw UnidadException.duplicateNombre(nombre);
        }
    }

    public void checkExisteEmail(String email) {
        if (email != null && readRepository.existsByEmail(email)) {
            throw UnidadException.duplicateEmail(email);
        }
    }


}
