package integra.core.service;

import integra.core.dto.ParamsDTO;
import integra.core.entity.ParametrosAppEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParamsDataProxy {
    private final ParamsService dbProvider;

    private List<ParametrosAppEntity> getAllParametros() {
        return dbProvider.getAllParametros();
    }

    public ParamsDTO getParams() {
        List<ParametrosAppEntity> params = getAllParametros();
        return ParamsDTO.builder()
                .idPuestoNocturno(getIntValue(params, ParamKey.ID_PUESTO_NOCTURNO))
                .horaInicioNocturno(getTimeValue(params, ParamKey.HORA_INICIO_NOCTURNO))
                .defaultRolUsuarioNuevo(getStringValue(params, ParamKey.DEFAULT_ROL_USUARIO_NUEVO))
                .idPuestoSupervisor(getIntValue(params, ParamKey.ID_PUESTO_SUPERVISOR))
                .idUsuarioAdmin(getLongValue(params, ParamKey.ID_USUARIO_ADMIN))
                .idRolDefault(getLongValue(params, ParamKey.ID_ROL_DEFAULT))
                .tiempoCapturaKiosco(getIntValue(params, ParamKey.TIEMPO_CAPTURA_KIOSCO))
                .build();
    }

    public Integer getIdPuestoNocturno() {
        return getIntValue(getAllParametros(), ParamKey.ID_PUESTO_NOCTURNO);
    }

    public LocalTime getHoraInicioNocturno() {
        return getTimeValue(getAllParametros(), ParamKey.HORA_INICIO_NOCTURNO);
    }

    public String getDefaultRolUsuarioNuevo() {
        return getStringValue(getAllParametros(), ParamKey.DEFAULT_ROL_USUARIO_NUEVO);
    }

    public Integer getIdPuestoSupervisor() {
        return getIntValue(getAllParametros(), ParamKey.ID_PUESTO_SUPERVISOR);
    }

    public Long getIdUsuarioAdmin() {
        return getLongValue(getAllParametros(), ParamKey.ID_USUARIO_ADMIN);
    }

    public Long getIdRolDefault() {
        return getLongValue(getAllParametros(), ParamKey.ID_ROL_DEFAULT);
    }

    public Integer getTiempoCapturaKiosco() {
        return getIntValue(getAllParametros(), ParamKey.TIEMPO_CAPTURA_KIOSCO);
    }

    private String getStringValue(List<ParametrosAppEntity> params, ParamKey key) {
        return params.stream()
                .filter(p -> p.getId().equals(key.getId()))
                .map(ParametrosAppEntity::getValor)
                .findFirst()
                .orElse(null);
    }

    private Integer getIntValue(List<ParametrosAppEntity> params, ParamKey key) {
        String value = getStringValue(params, key);
        return value != null ? Integer.parseInt(value) : null;
    }

    private Long getLongValue(List<ParametrosAppEntity> params, ParamKey key) {
        String value = getStringValue(params, key);
        return value != null ? Long.parseLong(value) : null;
    }

    private LocalTime getTimeValue(List<ParametrosAppEntity> params, ParamKey key) {
        String value = getStringValue(params, key);
        return value != null ? LocalTime.parse(value) : null;
    }
}
