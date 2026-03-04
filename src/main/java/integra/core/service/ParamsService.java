package integra.core.service;

import integra.core.entity.ParametrosAppEntity;
import integra.core.repository.ParametroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ParamsService {

    private final ParametroRepository repository;

    @Cacheable(value = "parametros", key = "'all'")
    public List<ParametrosAppEntity> getAllParametros() {

        return repository.findAll();
    }

    @Cacheable(value = "parametros", key = "#id")
    public Optional<String> getValorById(Integer id) {
        return repository.findById(id).map(ParametrosAppEntity::getValor);
    }

    @CacheEvict(value = "parametros", allEntries = true)
    public void updateValorById(Integer id, String valor) {
        repository.updateValorById(valor, id);
    }


//
//
//
//
//
//
//
//
//    /**
//     * Obtiene el ID del puesto de trabajo correspondiente al turno nocturno.
//     * <p>
//     * Propiedad: {@code app.config.idPuestoNocturno}
//     * </p>
//     *
//     * @return El ID del puesto nocturno. Valor por defecto: <b>2</b>.
//     */
//    public Integer getIdPuestoNocturno() {
//        return env.getProperty("app.config.idPuestoNocturno", Integer.class, 2);
//    }
//
//    /**
//     * Obtiene la hora de inicio configurada para el turno nocturno.
//     * <p>
//     * Propiedad: {@code app.config.horaInicioNocturno}
//     * <br>
//     * Formato esperado: "HH:mm" (ej. "18:00").
//     * </p>
//     *
//     * @return La hora de inicio como {@link LocalTime}. Valor por defecto: <b>18:00</b>.
//     */
//    public LocalTime getHoraInicioNocturno() {
//        String horaStr = env.getProperty("app.config.horaInicioNocturno", "18:00");
//        return LocalTime.parse(horaStr);
//    }
//
//    /**
//     * Obtiene el nombre del rol que se asigna automáticamente a los usuarios nuevos.
//     * <p>
//     * Propiedad: {@code app.config.defaultRolUsuarioNuevo}
//     * </p>
//     *
//     * @return El nombre del rol. Valor por defecto: <b>"Vendedor"</b>.
//     */
//    public String getDefaultRolUsuarioNuevo() {
//        return env.getProperty("app.config.defaultRolUsuarioNuevo", "Vendedor");
//    }
//
//    /**
//     * Obtiene el ID del puesto de trabajo correspondiente al Supervisor.
//     * <p>
//     * Propiedad: {@code app.config.idPuestoSupervisor}
//     * </p>
//     *
//     * @return El ID del puesto de supervisor. Valor por defecto: <b>4</b>.
//     */
//    public Integer getIdPuestoSupervisor() {
//        return env.getProperty("app.config.idPuestoSupervisor", Integer.class, 4);
//    }
//
//    public Long idRolDefault() {
//        return env.getProperty("app.config.idRolDefault", Long.class, 4L);
//    }
//
//    /**
//     * Obtiene el ID del usuario administrador principal del sistema.
//     * <p>
//     * Propiedad: {@code app.config.idUsuarioAdmin}
//     * </p>
//     *
//     * @return El ID del usuario admin. Valor por defecto: <b>1L</b>.
//     */
//    public Long getIdUsuarioAdmin() {
//        return env.getProperty("app.config.idUsuarioAdmin", Long.class, 1L);
//    }
//
//    /**
//     * Tiempo en segundos que tiene el kiosco para capturar la imagen
//     *
//     * @return Tiempo en segundos
//     */
//    public Integer getTiempoCapturaKiosco() {
//        return env.getProperty("app.config.tiempoCapturaKiosco", Integer.class, 5);
//    }
}