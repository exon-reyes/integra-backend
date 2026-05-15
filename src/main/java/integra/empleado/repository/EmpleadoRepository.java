package integra.empleado.repository;

import integra.empleado.entity.EmpleadoEntity;
import integra.empleado.query.EmpleadoAniversarioInfo;
import integra.empleado.query.InfoBasicaEmpleadoQuery;
import integra.empleado.util.FiltroEmpleado;
import integra.vacacion.query.EmpleadoDescansoInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmpleadoRepository extends JpaRepository<EmpleadoEntity, Integer> {
//    <T> List<T> findBy(Class<T> type);

    <T> Optional<T> findByPin(String clave, Class<T> type);

    @Transactional
    @Modifying
    @Query("update EmpleadoEntity e set e.pathAvatar = ?1 where e.id = ?2")
    int actualizarAvatar(String pathAvatar, Integer id);

    <T> Optional<T> findById(Integer integer, Class<T> type);

    <T> Optional<T> findByCodigoEmpleado(String codigo, Class<T> type);

    @Query("SELECT e.pathAvatar FROM EmpleadoEntity e WHERE e.id = :id")
    Optional<String> findAvatarById(@Param("id") Integer id);

    Optional<EmpleadoEntity> findByCodigoEmpleado(String codigoEmpleado);

    List<InfoBasicaEmpleadoQuery> findByEstatus(String estatus);

    /**
     * Permite obtener la lista de empleados aplicando filtro de estatus y puesto
     *
     * @param estatus Estatus a filtrar
     * @param id      Puesto a filtrar
     * @return Lista de empleados filtrados por estatus y puesto
     */
    List<InfoBasicaEmpleadoQuery> findByEstatusNotAndPuesto_Id(String estatus, Integer id);

    /**
     * Encuentra empleados activos que NO tienen registros de asistencia en el rango de fechas especificado.
     * Aplica filtros opcionales por unidad, puesto, zona y supervisor.
     *
     * @param estatus      Estatus a excluir (normalmente "B" para baja)
     * @param fechaInicio  Fecha de inicio del rango
     * @param fechaFin     Fecha de fin del rango
     * @param unidadId     ID de unidad (opcional)
     * @param puestoId     ID de puesto (opcional)
     * @param zonaId       ID de zona principal (opcional)
     * @param supervisorId ID del supervisor/responsable (opcional)
     * @return Lista de empleados sin asistencia en el rango especificado
     */
    @Query("""
            SELECT DISTINCT e FROM EmpleadoEntity e
                WHERE e.estatus <> :estatus
                AND NOT EXISTS (
                    SELECT 1 FROM AsistenciaModel a
                    WHERE a.empleado.id = e.id
                    AND a.fecha BETWEEN :fechaInicio AND :fechaFin
                )
                AND (:unidadId IS NULL OR e.unidad.id = :unidadId)
                AND (:puestoId IS NULL OR e.puesto.id = :puestoId)
                AND (:zonaId IS NULL OR e.zonaPrincipal = :zonaId)
                AND (:supervisorId IS NULL OR e.jefe.id = :supervisorId)
                ORDER BY e.nombreCompleto
            """)
    List<EmpleadoEntity> findEmpleadosSinAsistenciaEnRango(@Param("estatus") String estatus, @Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin, @Param("unidadId") Integer unidadId, @Param("puestoId") Integer puestoId, @Param("zonaId") Integer zonaId, @Param("supervisorId") Integer supervisorId);


    @Query("""
            SELECT new integra.empleado.query.EmpleadoAniversarioInfo(
                e.id, e.codigoEmpleado, e.puesto.nombre, e.fechaAlta,
                e.unidad.nombreCompleto, e.fechaReingreso, e.nombreCompleto,
                e.jefe.id, e.unidad.supervisor.id
            )
            FROM EmpleadoEntity e
            LEFT JOIN e.puesto
            LEFT JOIN e.unidad
            LEFT JOIN e.jefe
            WHERE e.estatus <> :estatus
            """)
    List<EmpleadoAniversarioInfo> findEmpleadosParaAniversario(@Param("estatus") String estatus);

    <T> List<T> findByEstatusNot(String b, Class<T> type);

    List<EmpleadoEntity> findByEstatusNot(String estatus);

    @Query("""
            SELECT new integra.vacacion.query.EmpleadoDescansoInfo(
                e.id, e.fechaAlta, e.fechaBaja, e.fechaReingreso, e.nombreCompleto
            )
            FROM EmpleadoEntity e
            WHERE e.estatus <> 'B'
              AND FUNCTION('MONTH', COALESCE(e.fechaReingreso, e.fechaAlta)) = :mes
              AND FUNCTION('DAY',   COALESCE(e.fechaReingreso, e.fechaAlta)) = :dia
            """)
    List<EmpleadoDescansoInfo> findActivosConAniversarioHoy(@Param("mes") int mes, @Param("dia") int dia);

    @Query("""
            SELECT e.id AS id, e.codigoEmpleado AS codigoEmpleado, e.nombreCompleto AS nombreCompleto,
                   e.estatus AS estatus,
                   u.id AS unidadId, u.nombreCompleto AS unidadNombre,
                   j.id AS jefeId, j.nombreCompleto AS jefeNombre
            FROM EmpleadoEntity e
            LEFT JOIN e.unidad u
            LEFT JOIN e.jefe j
            WHERE e.estatus <> 'B'
              AND (:#{#f.idSupervisor} IS NULL OR j.id = :#{#f.idSupervisor})
              AND (:#{#f.idPuesto}     IS NULL OR e.puesto.id = :#{#f.idPuesto})
              AND (:#{#f.idZona}       IS NULL OR e.zonaPrincipal = :#{#f.idZona})
              AND (:#{#f.unidadId}     IS NULL OR u.id = :#{#f.unidadId})
              AND (:#{#f.estatus}      IS NULL OR e.estatus = :#{#f.estatus})
              AND (:#{#f.clave}        IS NULL OR e.codigoEmpleado LIKE %:#{#f.clave}%
                                               OR e.nombreCompleto LIKE %:#{#f.clave}%)
              AND (:#{#f.id}           IS NULL OR e.id = :#{#f.id})
            ORDER BY e.nombreCompleto
            """)
    Page<VinculacionEmpleadoProjection> findVinculaciones(@Param("f") FiltroEmpleado filtro, Pageable pageable);

    interface VinculacionEmpleadoProjection {
        Integer getId();
        String getCodigoEmpleado();
        String getNombreCompleto();
        String getEstatus();
        Integer getUnidadId();
        String getUnidadNombre();
        Integer getJefeId();
        String getJefeNombre();
    }

    interface EmpleadoAniversarioProjection {
        Integer getId();
        String getCodigoEmpleado();
        String getNombreCompleto();
        LocalDate getFechaAlta();
        LocalDate getFechaReingreso();
        String getEstatus();
        String getNombreUnidad();
        String getNombrePuesto();
    }
}