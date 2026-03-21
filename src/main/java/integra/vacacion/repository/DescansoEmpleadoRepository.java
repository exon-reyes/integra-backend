//package integra.vacacion.repository;
//
//import integra.vacacion.domain.model.EstatusSolicitud;
//import integra.vacacion.entity.DescansoEmpleadoEntity;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Optional;
//import java.util.Set;
//
//@Repository
//public interface DescansoEmpleadoRepository extends JpaRepository<DescansoEmpleadoEntity, Long> {
//
//    // Solo descansos APROBADOS (activo = true) — para cálculo al crear solicitud
//    @Query("SELECT d.fechaDescanso FROM DescansoEmpleadoEntity d WHERE d.empleadoId = :empleadoId AND d.activo = true")
//    Set<LocalDate> findFechasDescansoByEmpleado(@Param("empleadoId") Integer empleadoId);
//
//    // Todos los descansos del empleado (APROBADOS + PENDIENTES) — para generar fechas granulares en el dashboard
//    @Query("SELECT d.fechaDescanso FROM DescansoEmpleadoEntity d WHERE d.empleadoId = :empleadoId AND d.estatus IN ('APROBADA', 'PENDIENTE')")
//    Set<LocalDate> findTodasFechasDescansoByEmpleado(@Param("empleadoId") Integer empleadoId);
//
//    List<DescansoEmpleadoEntity> findByEmpleadoIdAndActivoTrue(Integer empleadoId);
//
//    @Query("SELECT d FROM DescansoEmpleadoEntity d WHERE d.empleadoId = :empleadoId " + "AND d.fechaDescanso BETWEEN :inicio AND :fin AND d.activo = true")
//    List<DescansoEmpleadoEntity> findDescansosEnRango(@Param("empleadoId") Integer empleadoId, @Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);
//
//    boolean existsByEmpleadoIdAndActivoTrue(Integer empleadoId);
//
//    // Métodos para flujo de aprobación
//    List<DescansoEmpleadoEntity> findByEmpleadoIdAndEstatus(Integer empleadoId, EstatusSolicitud estatus);
//
//    boolean existsByEmpleadoIdAndEstatus(Integer empleadoId, EstatusSolicitud estatus);
//
//    @Query("SELECT d FROM DescansoEmpleadoEntity d WHERE d.estatus = :estatus ORDER BY d.empleadoId, d.fechaDescanso")
//    List<DescansoEmpleadoEntity> findAllByEstatus(@Param("estatus") EstatusSolicitud estatus);
//
//    List<DescansoEmpleadoEntity> findByEmpleadoIdAndPeriodoId(Integer empleadoId, Long periodoId);
//
//    Optional<DescansoEmpleadoEntity> findByIdAndEmpleadoId(Long id, Integer empleadoId);
//}
