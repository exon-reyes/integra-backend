//package integra.vacacion.repository;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;
//
//import integra.vacacion.entity.PeriodoVacacionalEntity;
//
//import java.util.List;
//
///**
// * Repositorio para el dashboard de gestión de vacaciones.
// * Expone la consulta nativa que agrega empleados con periodo VIGENTE,
// * sus gestores (nivel 1 y 2) y sus solicitudes como JSON array.
// *
// * <p>Columnas devueltas por la consulta (en orden):
// * <ol>
// *   <li>[0] empleado_id   (Integer)</li>
// *   <li>[1] nombre_completo (String)</li>
// *   <li>[2] dias_restantes  (Integer)</li>
// *   <li>[3] gestor_1_nombre (String, puede ser null)</li>
// *   <li>[4] gestor_2_nombre (String, puede ser null)</li>
// *   <li>[5] solicitudes     (String JSON array)</li>
// * </ol>
// */
//@Repository
//public interface DashboardGestionRepository extends JpaRepository<PeriodoVacacionalEntity, Long> {
//
//    /**
//     * Consulta nativa optimizada para el dashboard de gestión.
//     *
//     * <p>Filtra únicamente empleados con periodo VIGENTE.
//     * Los gestores se obtienen mediante las columnas primer_jefe y segundo_jefe.
//     * Las solicitudes se agregan como {@code JSON_ARRAYAGG} y se filtran
//     * con {@code JSON_OBJECT} para evitar nulls en el array.
//     *
//     * @return lista de filas {@code Object[]} con 6 columnas (ver Javadoc de clase).
//     */
//    @Query(nativeQuery = true, value = """
//            SELECT
//                e.id                                                         AS empleado_id,
//                e.nombre_completo                                            AS nombre_completo,
//                pv.dias_restantes                                            AS dias_restantes,
//                g1.nombre_completo                                           AS gestor_1_nombre,
//                g2.nombre_completo                                           AS gestor_2_nombre,
//                COALESCE(
//                    JSON_ARRAYAGG(
//                        CASE
//                            WHEN et.id IS NOT NULL THEN
//                                JSON_OBJECT(
//                                    'id',           et.id,
//                                    'fecha',        DATE_FORMAT(et.fecha, '%Y-%m-%d'),
//                                    'tipo',         et.tipo,
//                                    'estatus',      et.estatus,
//                                    'estatus_jefe', et.estatus_jefe,
//                                    'estatus_rrhh', et.estatus_rrhh
//                                )
//                            ELSE NULL
//                        END
//                    ),
//                    JSON_ARRAY()
//                )                                                            AS solicitudes
//            FROM periodos_vacacionales pv
//            -- Empleado principal
//            INNER JOIN empleado e
//                ON e.id = pv.empleado_id
//
//            -- Gestor nivel 1
//            LEFT JOIN empleado g1
//                ON g1.id = e.primer_jefe
//
//            -- Gestor nivel 2
//            LEFT JOIN empleado g2
//                ON g2.id = e.segundo_jefe
//
//            -- Solicitudes (VACACION y DESCANSO)
//            LEFT JOIN empleado_tiempo et
//                ON et.empleado_id = e.id
//               AND et.activo      = 1
//
//            WHERE pv.estatus = 'VIGENTE'
//
//            GROUP BY
//                e.id,
//                e.nombre_completo,
//                pv.dias_restantes,
//                g1.nombre_completo,
//                g2.nombre_completo
//
//            ORDER BY e.nombre_completo ASC
//            """)
//    List<Object[]> findDashboardGestionVigente();
//}
