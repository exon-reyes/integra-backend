# Documentación de Usuario Final: Obtener Empleado Jornada por NIP

## Descripción General

El servicio `ObtenerEmpleadoJornadaPorNip` es un componente clave del sistema de asistencia que permite obtener
información detallada sobre el estado actual de un empleado basado en su Número de Identificación Personal (NIP). Este
servicio es utilizado principalmente en los kioscos de asistencia para mostrar información relevante al empleado cuando
inicia sesión.

## Funcionalidad Principal

Este servicio recupera y consolida la siguiente información para un empleado:

1. **Información básica del empleado** (nombre, apellidos, código de empleado)
2. **Estado de la jornada laboral** (si ha iniciado o no su jornada)
3. **Tipo de puesto** (si es nocturno o diurno)
4. **Estado de pausas** (si tiene una pausa activa y qué tipo de pausa es)
5. **Unidad asignada** (identificador de la unidad donde trabaja)

## Casos de Uso

### Caso de Uso Principal: Visualización en Kiosco de Asistencia

**Actor:** Empleado
**Objetivo:** Visualizar información personal y estado de jornada al acceder al kiosco

**Flujo:**

1. El empleado ingresa su NIP en el kiosco de asistencia
2. El sistema llama al servicio `ObtenerEmpleadoJornadaPorNip` con el NIP proporcionado
3. El servicio devuelve la información consolidada del empleado
4. El kiosco muestra la información al empleado

### Caso de Uso Alternativo: Validación de Estado

**Actor:** Sistema de Asistencia
**Objetivo:** Validar si un empleado puede realizar ciertas acciones (iniciar jornada, tomar pausa, etc.)

**Flujo:**

1. Antes de permitir una acción, el sistema consulta el estado actual del empleado
2. Basado en la información devuelta (jornada iniciada, tipo de pausa, etc.), el sistema determina qué acciones están
   disponibles
3. El sistema muestra u oculta opciones según el estado actual

## Información Devuelta

El servicio devuelve un objeto `EmpleadoJornada` con la siguiente estructura:

| Campo              | Tipo    | Descripción                                                                 | Ejemplo                    |
|--------------------|---------|-----------------------------------------------------------------------------|----------------------------|
| `id`               | Integer | Identificador único del empleado en el sistema                              | 12345                      |
| `clave`            | String  | Código o clave de empleado                                                  | "EMP-001"                  |
| `nombre`           | String  | Nombre completo del empleado (nombre + apellido paterno + apellido materno) | "Juan Pérez García"        |
| `jornadaIniciada`  | Boolean | Indica si el empleado ha iniciado su jornada laboral                        | true/false                 |
| `esNocturno`       | Boolean | Indica si el empleado tiene un puesto nocturno                              | true/false                 |
| `tipoPausa`        | String  | Tipo de pausa activa (si existe). Puede ser null si no hay pausa activa     | "COMIDA", "DESCANSO", null |
| `unidadAsignadaId` | Integer | Identificador de la unidad donde está asignado el empleado                  | 789                        |

## Reglas de Negocio

1. **Empleado debe estar activo**: Si el empleado tiene estatus "B" (baja), el servicio devolverá un error indicando que
   el empleado no se encuentra activo.

2. **Determinación de jornada iniciada**:
    - Para empleados nocturnos: Se verifica si tienen una jornada activa nocturna
    - Para empleados diurnos: Se verifica si tienen una jornada no cerrada

3. **Pausas activas**: Solo se considera la pausa más reciente que no tenga hora de finalización registrada.

## Mensajes de Error

El servicio puede devolver los siguientes mensajes de error:

1. **"No se encontró al empleado especificado por el NIP [NIP]"**: Cuando no existe un empleado con el NIP proporcionado
2. **"El empleado no se encuentra activo"**: Cuando el empleado existe pero tiene estatus de baja

## Ejemplos de Uso

### Ejemplo 1: Empleado Diurno con Jornada Iniciada

**Entrada:** NIP = "12345"

**Salida:**

```json
{
  "id": 12345,
  "clave": "EMP-001",
  "nombre": "Juan Pérez García",
  "jornadaIniciada": true,
  "esNocturno": false,
  "tipoPausa": "COMIDA",
  "unidadAsignadaId": 789
}
```

**Interpretación:** El empleado Juan Pérez García ha iniciado su jornada diurna y actualmente está en una pausa de
comida.

### Ejemplo 2: Empleado Nocturno sin Jornada Iniciada

**Entrada:** NIP = "67890"

**Salida:**

```json
{
  "id": 67890,
  "clave": "EMP-002",
  "nombre": "María López Martínez",
  "jornadaIniciada": false,
  "esNocturno": true,
  "tipoPausa": null,
  "unidadAsignadaId": 456
}
```

**Interpretación:** La empleada María López Martínez tiene un puesto nocturno pero aún no ha iniciado su jornada, y no
tiene pausas activas.


## Consideraciones de Rendimiento

- La información básica del empleado está cacheada para mejorar el rendimiento
- Las consultas a la base de datos están optimizadas para recuperar solo la información necesaria
- El servicio está marcado como `@Transactional(readOnly = true)` para evitar bloqueos innecesarios

## Seguridad

- El servicio solo devuelve información básica del empleado y su estado de jornada
- No expone información sensible o confidencial
- El acceso al servicio debe estar protegido por los mecanismos de autenticación del kiosco