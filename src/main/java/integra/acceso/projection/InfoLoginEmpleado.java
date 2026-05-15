package integra.acceso.projection;

public interface InfoLoginEmpleado {
    Long getUser_idd();

    Integer getEmpleado_id();

    String getCodigo_empleado();

    Integer getEstatus_empleado();
    String getAvatar();

    String getNombre_completo();

    Integer getPuesto_id();

    String getPuesto();

    String getDepartamento();

    Integer getDepartamento_id();

    String getRoles_json();    // Recibe el JSON como String

    String getPermisos_json(); // Recibe el JSON como String
}