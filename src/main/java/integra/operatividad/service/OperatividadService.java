package integra.operatividad.service;

import integra.model.Operatividad;
import integra.operatividad.repository.OperatividadEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import integra.empresa.entity.HorarioOperativoEntity;
import integra.empresa.entity.OperatividadEntity;
import integra.empresa.entity.UnidadEntity;
import integra.empresa.repository.UnidadHorarioJpaRepository;
import integra.operatividad.dto.GuardarHorariosRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OperatividadService {
    private final OperatividadEntityRepository operatividadRepository;
    private final UnidadHorarioJpaRepository unidadHorarioJpaRepository;

    public List<Operatividad> obtenerOperatividades() {
        return operatividadRepository.findAll()
                .stream()
                .map(data -> new Operatividad(data.getId(), data.getNombre()))
                .toList();
    }

    @Transactional
    public void guardarHorarios(GuardarHorariosRequest request) {
        List<HorarioOperativoEntity> entities = request.horarios().stream().map(dto -> {
            HorarioOperativoEntity entity = new HorarioOperativoEntity();
            entity.setUnidad(new UnidadEntity(request.idUnidad()));

            OperatividadEntity op = new OperatividadEntity();
            op.setId(dto.idOperatividad());
            entity.setOperatividad(op);

            entity.setApertura(dto.apertura());
            entity.setCierre(dto.cierre());
            entity.setActivo(dto.activo());
            return entity;
        }).toList();

        unidadHorarioJpaRepository.saveAll(entities);
    }
}
