package integra.empleado.repository;

import integra.empleado.entity.EmpleadoEntity;
import integra.empleado.query.InfoBasicaEmpleadoQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmpleadoRepository extends JpaRepository<EmpleadoEntity, Integer> {
//    <T> List<T> findBy(Class<T> type);

    <T> Optional<T> findByPin(String clave, Class<T> type);

    <T> Optional<T> findById(Integer integer, Class<T> type);

    <T> Optional<T> findByCodigoEmpleado(String codigo, Class<T> type);

    Optional<EmpleadoEntity> findByCodigoEmpleado(String codigoEmpleado);

    List<InfoBasicaEmpleadoQuery> findByEstatusNot(String estatus);

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
    List<EmpleadoEntity> findEmpleadosSinAsistenciaEnRango(
            @Param("estatus") String estatus,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin,
            @Param("unidadId") Integer unidadId,
            @Param("puestoId") Integer puestoId,
            @Param("zonaId") Integer zonaId,
            @Param("supervisorId") Integer supervisorId
    );

}