package integra.empresa.dto;

import integra.empresa.entity.DepartamentoEntity;

import java.io.Serializable;

/**
 * DTO for {@link DepartamentoEntity}
 */
public record DepartamentoEntityDto(Integer id, String nombre) implements Serializable {
}