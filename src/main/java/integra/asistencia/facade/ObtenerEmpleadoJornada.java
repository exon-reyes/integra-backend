package integra.asistencia.facade;

import integra.asistencia.actions.EmpleadoJornada;
import integra.asistencia.entity.PausaModel;
import integra.asistencia.exception.AsistenciaDomainException;
import integra.asistencia.query.EmpleadoModelInfo;
import integra.empleado.service.ConsultarEmpleadoService;
import integra.global.exception.code.ErrorCode;
import integra.asistencia.repository.AsistenciaRepository;
import integra.asistencia.repository.PausaModelRepository;
import integra.asistencia.service.EmpleadoPuestoValidatorService;
import integra.asistencia.util.HandlerExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para obtener información consolidada de un empleado y su estado de jornada
 * basado en su Número de Identificación Personal (NIP).
 * <p>
 * Este servicio es utilizado principalmente en los kioscos de asistencia para mostrar
 * información relevante al empleado cuando inicia sesión, incluyendo:
 * - Información básica del empleado
 * - Estado de la jornada laboral (iniciada o no)
 * - Tipo de puesto (nocturno o diurno)
 * - Estado de pausas activas
 * - Unidad asignada
 *
 * @see EmpleadoJornada
 * @see HandlerExecutor
 */
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ObtenerEmpleadoJornada {
    private final ConsultarEmpleadoService consultarEmpleadoService;
    private final AsistenciaRepository asistenciaRepository;
    private final PausaModelRepository pausaRepository;
    private final EmpleadoPuestoValidatorService empleadoPuestoRepositoryImpl;

    /**
     * Ejecuta la obtención de información de empleado y estado de jornada.
     *
     * @param pin Número de Identificación Personal del empleado
     * @return Objeto EmpleadoJornada con la información consolidada
     * @throws AsistenciaDomainException Si el empleado no existe o no está activo
     * @see EmpleadoJornada
     * @see AsistenciaDomainException
     */
    public EmpleadoJornada execute(String pin) {
        EmpleadoModelInfo data = consultarEmpleadoService.execute(pin, EmpleadoModelInfo.class)
                .orElseThrow(AsistenciaDomainException::invalidPin);
        return procesarEmpleado(data);

    }

    private EmpleadoJornada procesarEmpleado(EmpleadoModelInfo data) {
        if (data.estatus().equals("B")) {
            throw new AsistenciaDomainException(ErrorCode.EMP_NOT_ACTIVE, "El empleado no se encuentra activo");
        }
        // 2. Determinar si la jornada está iniciada
        boolean esNocturno = empleadoPuestoRepositoryImpl.tienePuestoNocturno(data.puestoId());
        boolean jornadaIniciada = esNocturno ? empleadoPuestoRepositoryImpl.tieneJornadaActivaNocturna(data.id()) : asistenciaRepository.findFirstByEmpleado_IdAndJornadaCerradaFalseOrderByInicioJornadaDesc(data.id())
                .isPresent();

        // 3. Buscar la pausa activa
        String tipoPausa = pausaRepository.findFirstByAsistencia_Empleado_IdAndFinNullOrderByInicioDesc(data.id())
                .map(PausaModel::getTipo) // Extrae el tipo de la pausa (ej: "COMIDA")
                .orElse(null); // Si no hay pausa, el valor es null
        // 4. Construir el nombre completo
        String nombreCompleto = data.nombre() + " " + data.apellidoPaterno() + " " + data.apellidoMaterno();
        // 5. Devolver el objeto EmpleadoJornada con la nueva información
        return new EmpleadoJornada(data.id(), data.codigoEmpleado(), nombreCompleto, jornadaIniciada, esNocturno, tipoPausa, data.unidadId(), data.puestoId(), data.puestoNombre());
    }

    public EmpleadoJornada obtenerPorId(Integer id) {
        EmpleadoModelInfo data = consultarEmpleadoService.obtenerPorId(id, EmpleadoModelInfo.class)
                .orElseThrow(() -> AsistenciaDomainException.notFound(id.longValue()));
        return procesarEmpleado(data);
    }
}
