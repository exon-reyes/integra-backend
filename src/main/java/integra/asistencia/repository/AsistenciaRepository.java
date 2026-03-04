package integra.asistencia.repository;

import integra.asistencia.entity.AsistenciaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad AsistenciaModel.
 * Proporciona métodos para acceder y manipular datos de asistencia.
 */
public interface AsistenciaRepository
        extends JpaRepository<AsistenciaModel, Integer>, JpaSpecificationExecutor<AsistenciaModel>,
        AsistenciaRepositoryCustom {
    /**
     * Busca la primera asistencia de un empleado específico con jornada no cerrada,
     * ordenada por inicio de jornada en orden descendente.
     *
     * @param id el identificador del empleado
     * @return un Optional que contiene la asistencia encontrada o vacío si no
     * existe
     */
    Optional<AsistenciaModel> findFirstByEmpleado_IdAndJornadaCerradaFalseOrderByInicioJornadaDesc(Integer id);

    /**
     * Busca todas las asistencias de un empleado específico con jornada no cerrada.
     *
     * @param id el identificador del empleado
     * @return una lista de asistencias que coinciden con los criterios de búsqueda
     */
    List<AsistenciaModel> findByEmpleado_IdAndJornadaCerradaFalse(Integer id);

    /**
     * Busca todas las asistencias con jornada no cerrada y obtiene los datos del
     * empleado asociado.
     *
     * @return una lista de asistencias con los datos de empleado cargados
     */
    @Query("SELECT a FROM AsistenciaModel a JOIN FETCH a.empleado e WHERE a.jornadaCerrada = false")
    List<AsistenciaModel> findAllByJornadaCerradaFalseWithEmpleado();

    List<AsistenciaModel> findByEmpleado_IdInAndFechaBetween(List<Integer> empleadoIds,
                                                             java.time.LocalDate fechaInicio,
                                                             java.time.LocalDate fechaFin);

    Optional<AsistenciaModel> findByEmpleado_IdAndFecha(Integer empleadoId, LocalDate fecha);

    /**
     * Cuenta los días distintos en que el empleado tiene registro de asistencia en el rango dado.
     *
     * @param empleadoId id del empleado
     * @param inicio     primer día del rango (inclusive)
     * @param fin        último día del rango (inclusive)
     * @return cantidad de días únicos con asistencia
     */
    @Query("SELECT COUNT(DISTINCT a.fecha) FROM AsistenciaModel a WHERE a.empleado.id = :empleadoId AND a.fecha BETWEEN :inicio AND :fin")
    long countDistinctFechasByEmpleadoIdAndFechaBetween(
            @Param("empleadoId") Integer empleadoId,
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin);

    /**
     * Lista de fechas distintas en que el empleado tiene registro de asistencia en el rango dado, ordenadas.
     *
     * @param empleadoId id del empleado
     * @param inicio     primer día del rango (inclusive)
     * @param fin        último día del rango (inclusive)
     * @return lista de fechas (días laborados)
     */
    @Query("SELECT DISTINCT a.fecha FROM AsistenciaModel a WHERE a.empleado.id = :empleadoId AND a.fecha BETWEEN :inicio AND :fin ORDER BY a.fecha")
    List<LocalDate> findDistinctFechasByEmpleadoIdAndFechaBetween(
            @Param("empleadoId") Integer empleadoId,
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin);

}