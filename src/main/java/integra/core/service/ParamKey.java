package integra.core.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ParamKey {
    HORA_INICIO_NOCTURNO(1),
    ID_PUESTO_NOCTURNO(2),
    DEFAULT_ROL_USUARIO_NUEVO(3),
    ID_USUARIO_ADMIN(4),
    ID_PUESTO_SUPERVISOR(5),
    ID_ROL_DEFAULT(6),
    TIEMPO_CAPTURA_KIOSCO(7);

    private final Integer id;
}
