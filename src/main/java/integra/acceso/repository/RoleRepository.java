package integra.acceso.repository;

import integra.acceso.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    <T> List<T> findBy(Class<T> type);

    @Transactional
    @Modifying
    @Query("update Role r set r.name = ?1, r.description = ?2 where r.id = ?3")
    void updateNameAndDescriptionById(String name, String description, Long id);
}