package integra.empresa.repository;

import integra.empresa.query.unidad.InfoUnidad;
import java.util.List;

public interface UnidadQueryRepository {

    List<InfoUnidad> findByFiltros(
            Integer supervisorId,
            Integer zonaId,
            Boolean activo
    );
}