package integra.asistencia.service.kiosco;

import integra.asistencia.query.KioscoInfo;
import integra.core.service.ParamsDataProxy;
import integra.empresa.repository.UnidadRepository;
import integra.empresa.unidad.exception.UnidadException;
import integra.model.Unidad;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class KioscoQueryService {
    private final UnidadRepository repository;
    private final ParamsDataProxy paramsService;

    public List<Unidad> obtenerUnidadesKiosco() {
        return repository.findByActivoTrue(KioscoInfo.class).stream()
                .map(this::mapToUnidad)
                .toList();
    }

    public Unidad obtenerUnidadKiosco(Integer id) {
        return repository.findById(id, KioscoInfo.class)
                .map(this::mapToUnidad)
                .orElseThrow(() -> UnidadException.notFound(id.longValue()));
    }

    private Unidad mapToUnidad(KioscoInfo data) {
        Unidad unidad = new Unidad(
                data.id(),
                data.nombreCompleto(),
                data.requiereCamara(),
                data.codigoAutorizacionKiosco(),
                data.requiereCodigo(),
                data.versionKiosco(),
                data.tiempoCompensacion()
        );

        Integer tiempoEspera = data.tiempoEsperaKiosco() != null
                ? data.tiempoEsperaKiosco()
                : paramsService.getTiempoCapturaKiosco();

        unidad.setTiempoEsperaKiosco(tiempoEspera);
        return unidad;
    }

    public int obtenerTiempoCaptura() {
        return paramsService.getTiempoCapturaKiosco();
    }
}