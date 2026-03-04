package integra.acceso.projection;

/**
 * Projection for {@link integra.acceso.entity.Role}
 */
public interface InfoRolBasico {
    Long getId();

    String getName();

    String getDescription();

    boolean isActivo();

    Long getVersion();

    Boolean getIsDefault();
}