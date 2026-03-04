package integra.credenciales.service;

import integra.credenciales.entity.CuentaEntity;
import integra.credenciales.entity.TipoCuentaEntity;
import integra.credenciales.repository.CuentaEntityRepository;
import integra.credenciales.repository.TipoCuentaRepository;
import integra.credenciales.request.NuevaCuenta;
import integra.credenciales.request.TipoCuenta;
import integra.empresa.entity.DepartamentoEntity;
import integra.empresa.entity.UnidadEntity;
import integra.credenciales.exception.CredencialesException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CuentaCommandService {
    private final CuentaEntityRepository repository;
    private final TipoCuentaRepository tipoCuentaReposistory;

    @Transactional
    public void registrar(NuevaCuenta data) {
        CuentaEntity entity = new CuentaEntity();
        entity.setUsuario(data.getUsuario());
        entity.setClave(data.getClave());
        entity.setUnidad(new UnidadEntity(data.getIdUnidad()));
        entity.setTipo(new TipoCuentaEntity(data.getIdTipoCuenta()));
        entity.setDepartamento(new DepartamentoEntity(data.getIdDepartamento()));
        entity.setNota(data.getNota());
        try {
            repository.save(entity);
        } catch (DataIntegrityViolationException e) {
            log.info("No se pudo registrar al usuario.", e);
            throw CredencialesException.duplicateUser(data.getUsuario());
        }
    }

    @Transactional
    public void eliminar(Integer id) {
        repository.deleteById(id);
    }

    @Transactional
    public void actualizar(Integer id, NuevaCuenta data) {
        CuentaEntity entiy = new CuentaEntity(id);
        entiy.setUsuario(data.getUsuario());
        entiy.setClave(data.getClave());
        entiy.setUnidad(new UnidadEntity(data.getIdUnidad()));
        entiy.setTipo(new TipoCuentaEntity(data.getIdTipoCuenta()));
        entiy.setDepartamento(new DepartamentoEntity(data.getIdDepartamento()));
        entiy.setNota(data.getNota());
        try {
            repository.save(entiy);
        } catch (DataIntegrityViolationException e) {
            throw CredencialesException.duplicateUser(data.getUsuario());
        }
    }

    public void registrarTipoCuenta(@Valid TipoCuenta data) {
        TipoCuentaEntity entity = new TipoCuentaEntity();
        entity.setNombre(data.nombre());
        tipoCuentaReposistory.save(entity);
    }

    public void actualizarTipoCuenta(Integer id, @Valid TipoCuenta data) {
        TipoCuentaEntity entity = new TipoCuentaEntity(id);
        entity.setNombre(data.nombre());
        tipoCuentaReposistory.save(entity);
    }

    public void eliminarTipoCuenta(Integer id) {
        tipoCuentaReposistory.deleteById(id);
    }
}
