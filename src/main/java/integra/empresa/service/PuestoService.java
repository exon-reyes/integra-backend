package integra.empresa.service;

import integra.empresa.dto.PuestoDto;
import integra.empresa.repository.PuestoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PuestoService {
    private final PuestoRepository repository;

    public List<PuestoDto> obtenerPuestos() {
        return repository.findBy(PuestoDto.class);
    }
}