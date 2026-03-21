package integra.vacacion.repository;

import integra.vacacion.domain.model.EstatusSolicitud;
import integra.vacacion.domain.model.TipoSolicitud;
import integra.vacacion.entity.EmpleadoTiempoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EmpleadoTiempoEntityRepository extends JpaRepository<EmpleadoTiempoEntity, Long> {
    List<EmpleadoTiempoEntity> findByEmpleadoIdAndTipoAndPeriodoId(Integer empleadoId, TipoSolicitud tipo, Long periodoId);

    List<EmpleadoTiempoEntity> findByEmpleadoIdAndTipoAndEstatusAndPeriodoId(Integer empleadoId, TipoSolicitud tipo, EstatusSolicitud estatus, Long periodoId);

    List<EmpleadoTiempoEntity> findByEmpleadoIdAndPeriodoId(Integer empleadoId, Long periodoId);

    List<EmpleadoTiempoEntity> findByEstatusAndFecha(EstatusSolicitud estatus, LocalDate fecha);


}