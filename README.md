# Integra - Sistema de Gestión de Asistencia

## Introducción

**Integra** es una plataforma integral para la administración y control de asistencia del personal en organizaciones con múltiples unidades de trabajo. El sistema permite registrar las entradas y salidas de los empleados, gestionar sus permisos, generar reportes para nómina y mantener un control detallado de la productividad organizacional.

Esta documentación está diseñada para que cualquier usuario pueda entender qué hace el sistema y cómo usarlo, sin necesidad de conocimientos técnicos.

---

## Módulos del Sistema

### 1. Registro de Asistencia (Checador)

El módulo principal del sistema donde los empleados registran su entrada y salida del trabajo.

**Qué permite hacer:**
- **Registro de entrada y salida**: El empleado registra cuando inicia y termina su jornada laboral
- **Registro de descansos**: Los empleados pueden registrar sus tiempos de comida o descansos durante el día
- **Modo kiosco**: Una interfaz especial para dispositivos de registro automático (como una computadora en la entrada de la empresa)
- **Captura de foto**: El sistema puede tomar una foto del empleado al momento del registro
- **Registro manual**: Los administradores pueden registrar asistencia de forma manual cuando sea necesario
- **Horas extras**: Control del tiempo adicional trabajado por los empleados
- **Incidencias**: Manejo de retardos, ausencias y otras situaciones especiales

**Para quién es útil:**
- Empleados que necesitan registrar su asistencia diaria
- Administradores que supervisan la asistencia del personal

---

### 2. Gestión de Empleados

Administración del catálogo de empleados de la organización.

**Qué permite hacer:**
- **Catálogo de empleados**: Ver y buscar todos los empleados registrados
- **Datos del empleado**: Información personal, puesto, departamento, supervisor asignado
- **Asignación a unidades**: Determinar en qué área o unidad trabaja cada empleado

**Para quién es útil:**
- Recursos humanos
- Administradores del sistema

---

### 3. Estructura Organizacional (Empresa)

Configuración de la estructura física y organizacional de la empresa.

**Qué permite hacer:**
- **Departamentos**: Crear y administrar las áreas de la empresa (ej. Recursos Humanos, Ventas, Producción)
- **Puestos**: Definir los puestos de trabajo disponibles
- **Unidades de trabajo**: Agrupar empleados en unidades específicas con su propio supervisor
- **Zonas**: Organizar geográficamente las diferentes ubicaciones de la empresa
- **Horarios de operación**: Definir los horarios de trabajo para cada unidad

**Para quién es útil:**
- Administradores que configuran la estructura de la empresa

---

### 4. Credenciales y Badges

Gestión de identificaciones físicas o digitales para empleados y visitantes.

**Qué permite hacer:**
- **Tipos de credencial**: Definir diferentes tipos (empleado, visitante, contratista)
- **Asignación de credenciales**: Asignar identificaciones a cada persona
- **Vigencia**: Controlar cuándo expira cada credencial
- **Cuentas asociadas**: Vincular credenciales con cuentas de acceso al sistema

**Para quién es útil:**
- Seguridad
- Recursos humanos

---

### 5. Control de Acceso y Usuarios

Sistema de seguridad que determina quién puede acceder a qué partes del sistema.

**Qué permite hacer:**
- **Usuarios**: Crear y administrar cuentas de usuario en el sistema
- **Roles**: Definir tipos de usuario (Administrador, Supervisor, Empleado)
- **Permisos**: Controlar qué puede hacer cada tipo de usuario
- **Autenticación segura**: Inicio de sesión con contraseña segura
- **Recuperación de contraseña**: Sistema para restablecer acceso si se olvida la contraseña

**Para quién es útil:**
- Administradores del sistema
- Todos los usuarios del sistema

---

### 6. Reportes

Generación de información consolidada sobre la asistencia del personal.

**Qué permite hacer:**
- **Reportes de asistencia**: Ver quién asistió y quién no en un período determinado
- **Reportes en Excel**: Descargar información en formato Excel para nómina
- **Detalle de descansos**: Ver exactamente cuándo tomó descansos cada empleado
- **Resumen mensual**: Cantidad de días trabajados vs días no trabajados
- **Empleados sin registro**: Identificar a empleados que no han registrado asistencia
- **Inconsistencias**: Detectar registros de asistencia que no cuadran (entradas sin salidas, etc.)

**Para quién es útil:**
- Recursos humanos
- Supervisores
- Administradores de nómina

---

### 7. Operatividad

Definición y gestión de los horarios de trabajo de cada unidad.

**Qué permite hacer:**
- **Horarios laborales**: Establecer cuándo inicia y termina la jornada de trabajo
- **Por unidad**: Definir horarios específicos para cada área o departamento
- **Días de descanso**: Configurar los días de descanso de cada unidad

**Para quién es útil:**
- Administradores
- Supervisores de unidad

---

### 8. Observaciones y Auditorías

Sistema para registrar hallazgos durante visitas de inspección o auditoría.

**Qué permite hacer:**
- **Registro de observaciones**: Documentar hallazgos durante visitas a las instalaciones
- **Seguimiento**: Dar seguimiento a observaciones que requieren corrección
- **Evaluaciones**: Calificar el estado general de las áreas visitadas
- **Estados**: Mantener control del estado de cada observación (abierta, en proceso, resuelta)

**Para quién es útil:**
- Supervisores
- Personal de auditoría
- Gerencia

---

### 9. Colaboración

Espacio de trabajo colaborativo para los empleados.

**Qué permite hacer:**
- **Acceso al espacio de trabajo**: Los empleados pueden acceder a herramientas colaborativas
- **Gestión de empleados**: Administración del acceso de empleados al sistema

**Para quién es útil:**
- Todos los empleados

---

### 10. Perfil de Usuario

Gestión de la información personal de cada usuario.

**Qué permite hacer:**
- **Ver perfil**: Consultar la información personal registrada
- **Actualizar datos**: Modificar información personal (correo, teléfono, etc.)

**Para quién es útil:**
- Todos los usuarios del sistema

---

## Flujos de Trabajo Principales

### Flujo de Asistencia Diaria

1. El empleado llega a la instalación
2. Se identifica en el sistema (kiosco o aplicación web)
3. Registra su **entrada** - el sistema guarda la hora, foto y ubicación
4. Durante el día, registra sus **descansos** cuando sea necesario
5. Al terminar su jornada, registra su **salida**
6. El sistema calcula automáticamente las horas trabajadas

### Flujo de Administración

1. El administrador configura la estructura de la empresa (departamentos, puestos, unidades)
2. Crea los empleados y los asigna a sus unidades correspondientes
3. Define los horarios de operación de cada unidad
4. Los empleados comienzan a registrar su asistencia
5. El administrador genera reportes para verificar la asistencia del personal
6. Se identifican y resuelven incidencias (retardos, ausencias)

---

## Glosario de Términos

| Término | Significado |
|---------|-------------|
| **Asistencia** | Registro de la presencia del empleado en su lugar de trabajo |
| **Entrada** | Momento en que el empleado inicia su jornada laboral |
| **Salida** | Momento en que el empleado termina su jornada laboral |
| **Descanso** | Período de tiempo durante la jornada donde el empleado no trabaja (comida, etc.) |
| **Incidencia** | Situación especial como retardo, ausencia o permiso |
| **Unidad** | Grupo de trabajo dentro de la organización (departamento, área) |
| **Zona** | Ubicación geográfica de las instalaciones |
| **Horario de operación** | Horas de trabajo establecidas para una unidad |
| **Credencial** | Identificación física o digital del empleado |
| **Kiosco** | Dispositivo dedicado para el registro automático de asistencia |
| **Horas extras** | Tiempo adicional trabajado fuera del horario regular |

---

## Preguntas Frecuentes

### ¿Cómo registro mi asistencia?

Puede hacerlo de dos formas:
- **Presencial**: Utilizando el kiosco de registro en la entrada de su unidad
- **Web**: Iniciando sesión en el sistema y registrando su asistencia desde la aplicación

### ¿Qué hago si olvidé registrar mi salida?

Contacte a su supervisor para que registre la salida de forma manual a través del módulo de administración.

### ¿Cómo puedo ver mi historial de asistencia?

Inicie sesión en el sistema y acceda a la sección "Mi Registro" o "Mis Asistencias".

### ¿Cómo puedo cambiar mi contraseña?

En su perfil de usuario, encontrará la opción para cambiar la contraseña.

### ¿Quién puede ver mis registros de asistencia?

Su supervisor directo y los administradores del sistema tienen acceso a esta información.

---

## Contacto y Soporte

Para dudas o problemas técnicos, contacte al administrador del sistema o al área de tecnología de su organización.

---

*Documentación generada para Integra - Sistema de Control de Asistencia*
*Versión 1.0*
