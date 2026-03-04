package integra.acceso.service.account;

import integra.acceso.entity.TokenVersion;
import integra.acceso.repository.TokenVersionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TokenVersionService {

    private final TokenVersionRepository repository;

    @Cacheable(value = "tokenVersions", key = "#username")
    public int getVersion(String username) {
        return repository.findByUsername(username)
                .map(TokenVersion::getVersion)
                .orElseGet(() -> createInitialVersion(username));
    }

    @Transactional
    public int createInitialVersion(String username) {
        log.info("Inicializando versión de token en DB para: {}", username);
        // El save devuelve la entidad con la versión 1 por defecto
        return repository.save(new TokenVersion(null, username, 1)).getVersion();
    }

    @Transactional
    @CacheEvict(value = "tokenVersions", key = "#username")
    public void incrementVersion(String username) {
        int updatedRows = repository.incrementVersionByUsername(username);
        if (updatedRows == 0) {
            createInitialVersion(username);
        }
        log.info("Versión de token incrementada para el usuario: {}", username);
    }

    public void obtenerTokensCache() {
        log.info("Precargando versiones de tokens en Caffeine...");
        repository.findAll().forEach(tv -> getVersion(tv.getUsername()));
    }
}