package integra.acceso.repository;

import integra.acceso.entity.SecurityNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecurityNodeRepository extends JpaRepository<SecurityNode, String> {

}