# Decisión de Arquitectura: Tabla Unificada de Eventos de Tiempo del Empleado

## Contexto

El sistema de gestión de personal requiere registrar distintos tipos de ausencias o eventos laborales asociados a una fecha específica, tales como:

* Vacaciones
* Descansos
* Permisos
* Incapacidades
* Otros eventos futuros

Inicialmente se consideró manejar tablas separadas para cada tipo de evento (por ejemplo `vacaciones`, `descansos`, etc.). Sin embargo, esto genera duplicación de lógica, mayor complejidad en consultas de calendario y validaciones entre tablas.

## Decisión

Se optó por implementar un **modelo de tabla unificada** que almacena todos los eventos de tiempo del empleado.

Cada registro representa **un evento en una fecha específica** y se diferencia mediante un campo `tipo`.

## Estructura conceptual

Tabla principal:

empleado_tiempo

Campos principales:

* id
* empleado_id
* fecha
* tipo
* estatus
* comentario
* aprobador_id
* fecha_aprobacion
* comentarios_aprobador
* periodo_id
* activo
* created_at

El campo `tipo` permite identificar la naturaleza del evento:

* VACACION
* DESCANSO
* PERMISO
* INCAPACIDAD

## Principios del modelo

### 1. Granularidad por día

Cada registro representa **una fecha específica**, no rangos de fechas.

Esto permite:

* Selección de días individuales
* Días salteados
* Integración directa con calendarios
* Validación simple de conflictos

### 2. Modelo extensible

Agregar nuevos tipos de eventos no requiere nuevas tablas ni cambios estructurales significativos.

Ejemplo:

* HOME_OFFICE
* CAPACITACION
* COMISION

### 3. Simplificación de consultas

Todas las consultas de calendario y disponibilidad se realizan sobre una sola tabla.

Ejemplo conceptual:

Obtener eventos de un empleado:

SELECT fecha, tipo, estatus
FROM empleado_tiempo
WHERE empleado_id = ?

### 4. Prevención de duplicados

Se recomienda una restricción única:

(empleado_id, fecha, tipo)

Esto evita que el mismo evento se registre múltiples veces para la misma fecha.

## Beneficios

* Reducción de duplicación de lógica
* Backend más simple
* Consultas más eficientes
* Calendario unificado
* Modelo extensible para futuros requerimientos

## Consideraciones

* El cálculo de vacaciones se realiza contando registros con tipo `VACACION`.
* Los festivos se gestionan en una tabla independiente del sistema.
* Las validaciones de conflictos se realizan sobre la misma tabla.

## Conclusión

El modelo de **tabla unificada de eventos de tiempo del empleado** proporciona mayor flexibilidad, simplifica el backend y facilita la integración con componentes de calendario en el frontend.
