package integra.empleado.dto;

public record ActualizarResponsableRequest(
        Integer primerResponsableId,
        Integer segundoResponsableId
) {
}
