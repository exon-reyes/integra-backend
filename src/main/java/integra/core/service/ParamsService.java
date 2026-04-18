package integra.core.service;

import integra.core.entity.ParametrosAppEntity;
import integra.core.repository.ParametroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ParamsService {

    private final ParametroRepository repository;

    @Cacheable(value = "parametros", key = "'all'")
    public List<ParametrosAppEntity> getAllParametros() {

        return repository.findAll();
    }

    @Cacheable(value = "parametros", key = "#id")
    public Optional<String> getValorById(Integer id) {
        return repository.findById(id).map(ParametrosAppEntity::getValor);
    }

    @CacheEvict(value = "parametros", allEntries = true)
    public void updateValorById(Integer id, String valor) {
        repository.updateValorById(valor, id);
    }

}