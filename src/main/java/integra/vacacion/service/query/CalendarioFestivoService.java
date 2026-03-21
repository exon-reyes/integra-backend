package integra.vacacion.service.query;

import integra.vacacion.dto.response.Festivo;
import integra.vacacion.repository.FestivoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalendarioFestivoService {
    private final FestivoRepository festivoRepository;

    public List<Festivo> obtenerFestivos(Integer anioInicio, Integer anioFin) {
        return festivoRepository.findFestivosBetween(LocalDate.of(anioInicio, 1, 1), LocalDate.of(anioFin, 12, 31))
                .stream()
                .map(f -> new Festivo(f.getId(), f.getFecha(), f.getNombre(), f.getActivo()))
                .toList();
    }
}