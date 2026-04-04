package integra.acceso.repository;

import integra.acceso.entity.User;
import integra.acceso.projection.InfoLoginEmpleado;
import integra.acceso.projection.PermissionProjection;
import integra.acceso.projection.UsuarioBasicoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    <T> Optional<T> findByUsername(String username, Class<T> type);

    boolean existsByUsername(String username);

    boolean existsByIdNotAndUsername(Long id, String username);


    @Query("select (count(u) > 0) from User u where u.empleadoId = ?1")
    boolean existsByEmpleadoId(Integer empleadoId);

    // Query nativa para obtener solo los nombres de usuario vinculados a un Rol
    @Query(value = "SELECT u.username FROM users u " + "JOIN user_roles ur ON u.id = ur.user_id " + "WHERE ur.role_id = :roleId", nativeQuery = true)
    List<String> findUsernamesByRoleId(@Param("roleId") Long roleId);

    @Query(value = """
            SELECT u.id AS user_idd,
                e.id AS empleado_id,
                e.codigo_empleado AS codigo_empleado,
                e.estatus AS estatus_empleado,
                e.path_avatar as avatar,
                e.nombre_completo AS nombre_completo,
                p.id AS puesto_id,
                p.nombre AS puesto,
                d.nombre AS departamento,
                d.id AS departamento_id,
                (SELECT JSON_ARRAYAGG(r.id) 
                 FROM user_roles ur 
                 JOIN roles r ON ur.role_id = r.id 
                 WHERE ur.user_id = u.id) AS roles_json,
                (SELECT JSON_ARRAYAGG(up.permission_id)
                 FROM user_permissions up
                 WHERE up.user_id = u.id) AS permisos_json
            FROM users u
            LEFT JOIN empleado e ON u.empleado_id = e.id
            LEFT JOIN puesto p ON e.puesto_id = p.id
            LEFT JOIN departamento d ON e.departamento_id = d.id
            WHERE u.username = :username AND u.activo = 1
            """, nativeQuery = true)
    Optional<InfoLoginEmpleado> findInfoLoginByUsername(@Param("username") String username);

    @Transactional
    @Modifying
    @Query("update User u set u.activo = ?1 where u.id = ?2")
    void updateActivoById(Boolean activo, Long id);

    @Query(value = """
            SELECT
                u.id ,
                u.username,
                u.empleado_id AS empleadoId,
                e.email,
                e.nombre_completo AS nombreCompleto,
                u.activo,
                GROUP_CONCAT(DISTINCT r.name ORDER BY r.name SEPARATOR ', ') AS roles,
                d.nombre AS departamento,
                p.nombre AS puesto
            FROM users u
            LEFT JOIN user_roles ur ON ur.user_id = u.id
            LEFT JOIN roles r ON r.id = ur.role_id
            LEFT JOIN empleado e ON e.id = u.empleado_id
            LEFT JOIN departamento d ON d.id = e.departamento_id
            LEFT JOIN puesto p ON p.id = e.puesto_id
            GROUP BY
                u.id, u.username, u.empleado_id,
                e.email, e.nombre_completo,
                u.activo, d.nombre, p.nombre
            """, countQuery = """
            SELECT COUNT(DISTINCT u.id)
            FROM users u
            """, nativeQuery = true)
    Page<UsuarioBasicoDTO> obtenerUsuariosRaw(Pageable pageable);

    @Query(value = """
            SELECT rp.permission_id AS permissionId, 'ROL' AS origen
            FROM user_roles ur
            INNER JOIN role_permissions rp ON ur.role_id = rp.role_id
            WHERE ur.user_id = :idUsuario
            UNION
            SELECT up.permission_id AS permissionId, 'ESPECIAL' AS origen
            FROM user_permissions up
            WHERE up.user_id = :idUsuario
            """, nativeQuery = true)
    List<PermissionProjection> findAllPermissionsRaw(@Param("idUsuario") Long idUsuario);

    @Query(value = """
            SELECT COUNT(*) > 0
            FROM users u
            WHERE u.empleado_id = :empleadoId AND u.activo = 1
              AND (
                  EXISTS (SELECT 1 FROM user_roles ur
                          INNER JOIN role_permissions rp ON ur.role_id = rp.role_id
                          WHERE ur.user_id = u.id AND rp.permission_id = :permisoId)
                  OR
                  EXISTS (SELECT 1 FROM user_permissions up
                          WHERE up.user_id = u.id AND up.permission_id = :permisoId)
              )
            """, nativeQuery = true)
    int tienePermiso(@Param("empleadoId") Integer empleadoId, @Param("permisoId") String permisoId);

}