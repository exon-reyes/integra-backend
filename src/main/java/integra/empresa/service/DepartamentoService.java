package integra.empresa.service;

import integra.empresa.dto.DepartamentoEntityDto;
import integra.empresa.repository.DepartamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartamentoService {
    private final DepartamentoRepository repository;

    public List<DepartamentoEntityDto> obtenerDepartamentos() {
        return repository.findBy(DepartamentoEntityDto.class);
    }
}