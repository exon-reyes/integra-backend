package integra.acceso.repository;

import integra.acceso.entity.TokenVersion;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TokenVersionRepository extends CrudRepository<TokenVersion, Integer> {
    Optional<TokenVersion> findByUsername(String username);

    @Modifying
    @Query("UPDATE TokenVersion t SET t.version = t.version + 1 WHERE t.username = :username")
    int incrementVersionByUsername(@Param("username") String username);
}