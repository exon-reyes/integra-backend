package integra.asistencia.service.kiosco;

import integra.asistencia.dto.CruceKioscoDTO;
import integra.asistencia.dto.CruceKioscoFiltroDTO;
import integra.asistencia.entity.CruceEmpleadoKiosco;
import integra.asistencia.repository.CruceEmpleadoKioscoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CruceKioscoService {
    private final CruceEmpleadoKioscoRepository repository;

    @Transactional(readOnly = true)
    public List<CruceKioscoDTO> buscarPorFiltros(CruceKioscoFiltroDTO filtro) {
        Specification<CruceEmpleadoKiosco> spec = (root, query, cb) -> cb.conjunction();

        if (filtro.empleadoId() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("empleado").get("id"), filtro.empleadoId()));
        }
        if (filtro.asistenciaId() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("asistenciaId"), filtro.asistenciaId()));
        }
        if (filtro.fechaInicio() != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("fecha"), filtro.fechaInicio()));
        }
        if (filtro.fechaFin() != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("fecha"), filtro.fechaFin()));
        }
        if (filtro.accion() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("accion"), filtro.accion()));
        }
        if (filtro.unidadRegistroId() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("unidadRegistro").get("id"), filtro.unidadRegistroId()));
        }
        if (filtro.unidadEsperadaId() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("unidadEsperada").get("id"), filtro.unidadEsperadaId()));
        }

        return repository.findAll(spec).stream()
                .map(this::toDTO)
                .toList();
    }

    private CruceKioscoDTO toDTO(CruceEmpleadoKiosco entity) {
        return new CruceKioscoDTO(
                entity.getId(),
                entity.getFecha(),
                entity.getEmpleado() != null ? entity.getEmpleado().getId() : null,
                entity.getAsistenciaId(),
                entity.getPathImg(),
                entity.getUnidadRegistro() != null ? entity.getUnidadRegistro().getId() : null,
                entity.getUnidadEsperada() != null ? entity.getUnidadEsperada().getId() : null,
                entity.getAccion()
        );
    }
}
