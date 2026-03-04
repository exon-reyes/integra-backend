package integra.acceso.util;

public final class Autoridades {

    // ================== GENERALES (A) ==================
    public static final String GENERALES_VER = "hasAuthority('A')";

    // ------ UNIDADES (AA) ------
    public static final String UNIDADES_VER = "hasAuthority('AA')";
    public static final String UNIDADES_CONSULTAR = "hasAuthority('AA1')";
    public static final String UNIDADES_EDITAR = "hasAuthority('AA2')";
    public static final String UNIDADES_ELIMINAR = "hasAuthority('AA3')";
    public static final String UNIDADES_CREAR = "hasAuthority('AA4')";
    public static final String UNIDADES_EXPORTAR_CONTACTO = "hasAuthority('AA5')";

    // ------ ZONAS (AB) ------
    public static final String ZONAS_VER = "hasAuthority('AB')";
    public static final String ZONAS_CREAR = "hasAuthority('AB1')";
    public static final String ZONAS_EDITAR = "hasAuthority('AB2')";
    public static final String ZONAS_ELIMINAR = "hasAuthority('AB3')";

    // ================== GESTIÓN RRHH (B) ==================
    public static final String RRHH_VER = "hasAuthority('B')";

    // ------ EMPLEADOS (BA) ------
    public static final String EMPLEADOS_VER = "hasAuthority('BA')";
    public static final String EMPLEADOS_CONSULTAR_ASISTENCIA = "hasAuthority('BA1')";
    public static final String EMPLEADOS_VER_INDICADORES = "hasAuthority('BA2')";
    public static final String EMPLEADOS_EXPORTAR = "hasAuthority('BA3')";
    public static final String EMPLEADOS_RESTRINGIR_FILTRO_SUPERVISOR = "hasAuthority('BA4')";

    // ================== GESTIÓN DE ASISTENCIA (C) ==================
    public static final String ASISTENCIA_VER = "hasAuthority('C')";

    // ------ RELOJ CHECADOR (CA) ------
    public static final String RELOJ_CHECADOR_ACCESO = "hasAuthority('CA')";

    // ------ ASISTENCIA MANUAL (CB) ------
    public static final String ASISTENCIA_MANUAL_ACCESO = "hasAuthority('CB')";
    public static final String ASISTENCIA_MANUAL_RESTRINGIR_FILTRO_SUPERVISOR = "hasAuthority('CB1')";

    // ------ CONSULTA DE ASISTENCIA (CC) ------
    public static final String CONSULTA_ASISTENCIA_VER = "hasAuthority('CC')";
    public static final String CONSULTA_ASISTENCIA_CONSULTAR = "hasAuthority('CC1')";
    public static final String CONSULTA_ASISTENCIA_RESTRINGIR_FILTRO_SUPERVISOR = "hasAuthority('CC2')";
    public static final String CONSULTA_ASISTENCIA_EXPORTAR = "hasAuthority('CC3')";

    // ------ CONFIGURACIÓN RELOJ CHECADOR (CD) ------
    public static final String CONFIG_RELOJ_VER = "hasAuthority('CD')";
    public static final String CONFIG_RELOJ_VER_UNIDADES = "hasAuthority('CD1')";
    public static final String CONFIG_RELOJ_ACTIVAR_CAMARA = "hasAuthority('CD2')";
    public static final String CONFIG_RELOJ_EDITAR_TIEMPOS = "hasAuthority('CD3')";
    public static final String CONFIG_RELOJ_APROBAR_PERSONALIZADA = "hasAuthority('CD4')";
    public static final String CONFIG_RELOJ_VER_INDICADORES = "hasAuthority('CD5')";

    // ------ COMPENSACIONES (CE) ------
    public static final String COMPENSACIONES_VER = "hasAuthority('CE')";
    public static final String COMPENSACIONES_VER_APLICADAS = "hasAuthority('CE1')";
    public static final String COMPENSACIONES_RESTRINGIR_FILTRO_SUPERVISOR = "hasAuthority('CE2')";
    public static final String COMPENSACIONES_EXPORTAR = "hasAuthority('CE3')";

    // ------ EXPORTACION INCIDENCIAS (CF) ------
    public static final String EXPORTACION_INCIDENCIAS_VER = "hasAuthority('CF')";
    public static final String EXPORTACION_INCIDENCIAS_RESTRINGIR_FILTRO_SUPERVISOR = "hasAuthority('CF1')";

    // ================== INFRAESTRUCTURA TI (D) ==================
    public static final String INFRAESTRUCTURA_VER = "hasAuthority('D')";

    // ------ ROLES (DA) ------
    public static final String ROLES_VER = "hasAuthority('DA')";
    public static final String ROLES_VER_DETALLE = "hasAuthority('DA1')";
    public static final String ROLES_ELIMINAR = "hasAuthority('DA2')";
    public static final String ROLES_EDITAR = "hasAuthority('DA3')";
    public static final String ROLES_CREAR = "hasAuthority('DA4')";

    // ------ USUARIOS (DB) ------
    public static final String USUARIOS_VER = "hasAuthority('DB')";
    public static final String USUARIOS_CONSULTAR = "hasAuthority('DB1')";
    public static final String USUARIOS_CREAR = "hasAuthority('DB2')";
    public static final String USUARIOS_EDITAR = "hasAuthority('DB3')";
    public static final String USUARIOS_DESACTIVAR = "hasAuthority('DB4')";

    // ------ CREDENCIALES (DC) ------
    public static final String CREDENCIALES_VER = "hasAuthority('DC')";
    public static final String CREDENCIALES_CONSULTAR = "hasAuthority('DC1')";
    public static final String CREDENCIALES_EDITAR = "hasAuthority('DC2')";
    public static final String CREDENCIALES_ELIMINAR = "hasAuthority('DC3')";
    public static final String CREDENCIALES_EXPORTAR = "hasAuthority('DC4')";
    public static final String CREDENCIALES_CREAR = "hasAuthority('DC5')";

    // ------ TIPO DE CUENTA (DD) ------
    public static final String TIPO_CUENTA_VER = "hasAuthority('DD')";
    public static final String TIPO_CUENTA_CREAR_PROVEEDOR = "hasAuthority('DD1')";
    public static final String TIPO_CUENTA_CONSULTAR_PROVEEDORES = "hasAuthority('DD2')";
    public static final String TIPO_CUENTA_EDITAR_PROVEEDOR = "hasAuthority('DD3')";
    public static final String TIPO_CUENTA_ELIMINAR_PROVEEDOR = "hasAuthority('DD4')";

    private Autoridades() {
        // Evitar instanciación
    }
}