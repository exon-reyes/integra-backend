package integra.empresa.service.unidad;

import integra.empresa.query.unidad.UnidadContactoQuery;
import integra.empresa.query.unidad.UnidadHorarioQuery;
import integra.empresa.repository.UnidadHorarioJpaRepository;
import integra.empresa.repository.UnidadRepository;
import integra.empresa.unidad.exception.UnidadException;
import integra.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnidadQueryService {
    private final UnidadRepository readRepository;
    private final UnidadHorarioJpaRepository horarioRepository;

    @Cacheable(value = "unidadBuscar", key = "T(java.util.Objects).hash(#supervisorId, #zonaId, #activo)")
    @Transactional(readOnly = true)
    public List<Unidad> buscar(Integer supervisorId, Integer zonaId, Boolean activo) {
        return readRepository.findByFiltros(supervisorId, zonaId, activo)
                .stream()
                .map(data -> {
                    var unidad=new Unidad(data.id(),data.nombreCompleto());
                    unidad.setSupervisor(new Empleado(data.supervisorId(), data.supervisorNombreCompleto()));
                    unidad.setContacto(new Contacto(new Zona(data.zonaId(), data.zonaNombre())));
                    unidad.setActivo(data.activo());
                    unidad.setClave(data.clave());
                    return unidad;
                })
                .toList();
    }

    @Cacheable(value = "unidadContacto", key = "#idUnidad")
    @Transactional(readOnly = true)
    public Unidad obtenerContacto(Integer idUnidad) {
        UnidadContactoQuery data = readRepository.findById(idUnidad, UnidadContactoQuery.class)
                .orElseThrow(() -> UnidadException.notFound((long) idUnidad));
        Contacto contacto = new Contacto(data.telefono(), data.email(), new Estado(data.estadoId(), data.estadoNombre()), data.localizacion(), new Zona(data.zonaId(), data.zonaNombre()));
        contacto.setDireccion(data.direccion());

        Unidad unidad = new Unidad(data.id(),data.clave(),data.nombreCompleto());
        unidad.setContacto(contacto);
        unidad.setNombre(data.nombre());
        unidad.setActivo(data.activo());

        unidad.setSupervisor(new Empleado(null, data.supervisorNombreCompleto()));

        return unidad;
    }

    @Cacheable(value = "unidadInfo", key = "#idUnidad")
    @Transactional(readOnly = true)
    public List<HorarioOperativo> obtenerHorario(Integer idUnidad) {
        return horarioRepository.findByUnidadId(idUnidad, UnidadHorarioQuery.class)
                .stream()
                .map(x -> new HorarioOperativo(x.id(), x.operatividadNombre(), x.apertura(), x.cierre()))
                .toList();
    }
}