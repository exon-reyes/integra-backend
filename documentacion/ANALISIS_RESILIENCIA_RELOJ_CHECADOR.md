# Análisis de Resiliencia: Reloj Checador (Problema de Idempotencia por Timeouts)

## Descripción del Problema
Cuando un usuario registra su entrada o salida en el Kiosco (Reloj Checador), la petición viaja hacia el backend. En ocasiones, la red es intermitente o el servidor tarda más de lo esperado en procesar la foto y guardar los datos. 

El frontend tiene un límite de espera (actualmente de 20 segundos para peticiones con imagen). Si el backend no responde en ese tiempo, el frontend aborta la conexión (genera un error de Timeout) y muestra el botón de **"Reintentar"**.

El problema es que el backend **sí procesó la solicitud exitosamente** (logró registrar la salida o la entrada minutos o segundos después), pero el frontend nunca se enteró.

Cuando el usuario da clic en "Reintentar":
1. El frontend envía una nueva petición de salida.
2. El backend busca una jornada activa para cerrarla.
3. Al haberla cerrado en la petición anterior (la que dio timeout), ya no encuentra ninguna jornada activa.
4. El backend responde con un error (`notFound` "No se encontró registro para el empleado...").
5. El usuario se confunde, porque el sistema le dice que hay un error, cuando en realidad su salida ya estaba registrada.

## ¿Es "incrementar el tiempo de espera" la solución?
**No. Incrementar el timeout es solo una mitigación parcial.**
Aunque aumentar el tiempo a 30 o 40 segundos reducirá la cantidad de veces que ocurre esto, no soluciona el problema de raíz. Si ocurre un microcorte de red (donde el teléfono pierde los datos de retorno de la API pero la API sí recibió la solicitud de entrada), el usuario inevitablemente le dará a "Reintentar" y el problema se volverá a presentar.

A esto se le conoce en ingeniería de software como un problema de **Idempotencia**.

## Soluciones Propuestas

### Solución 1: Llave de Idempotencia (Idempotency Key) - *[Más Robusta]*
Esta es la solución estándar de la industria (utilizada en pasarelas de pago y sistemas críticos).
1. El Frontend genera un UUID (Identificador Único) en el momento en que el usuario le da "Guardar/Finalizar".
2. Este UUID se envía en los Headers de la petición HTTP (ej. `X-Idempotency-Key: 123e4567-e89b...`).
3. El frontend de Angular manda el **mismo** UUID si el usuario le da al botón de "Reintentar" de esa misma captura.
4. El backend recibe la llave. Antes de procesar, verifica si esa llave ya fue procesada recientemente (guardada en una tabla o caché como Redis).
5. Si ya se procesó, el backend **no hace nada** pero responde con un código de éxito de inmediato (simulando que se acaba de guardar).

### Solución 2: Tolerancia de Tiempo en el negocio (Business Logic Grace Period) - *[Más Fácil de Implementar]*
Dado que estamos hablando específicamente de asistencias (Entrada/Salida), nadie checa su salida 2 veces en 2 minutos.

**Al registrar Salida (`FinalizarJornada.java`):**
En lugar de fallar inmediatamente si no hay jornada activa, el backend verifica:
1. Buscar jornada activa. Si no existe...
2. Buscar si la última jornada que tiene "Jornada Cerrada = true" fue cerrada hace menos de **2 a 5 minutos**.
3. Si es así, significa que es un reintento por problemas de red. Devolvemos HTTP 200 OK con mensaje `"Jornada finalizada exitosamente"` (absorbemos el error silenciosamente).

**Al registrar Entrada (`IniciarJornada.java`):**
1. Al validar si hay jornada activa (`validarNoExisteJornadaActiva`), si ya hay una jornada creada hace menos de **2 a 5 minutos**, asumimos que es un reintento del inicio.
2. En lugar de lanzar `duplicateEntry`, retornamos un HTTP 200 OK informando que se inició con éxito.

### Solución 3: Peticiones de validación previas (Polling de estado)
1. Antes de que el Frontend mande el reintento, hace una llamada rápida a una API como `/asistencia/estado-actual` para ver si el estatus en backend ya cambió a "Salida".
2. Si ya cambió, el Frontend lo da por bueno y quita la pantalla de error automáticamente.
*Desventaja*: Carga la red con peticiones extra y hace el frontend más complejo.

---

## Recomendación Final
No toques los timeouts en el frontend (20 segundos es bastante tiempo de espera para una UI; es un límite sano). 

En su lugar, debemos aplicar la **Solución 2**. Es la más económica computacionalmente para este proyecto y provee una experiencia de usuario sin fricción en el kiosco, requiriendo modificaciones mínimas solo en las clases `BaseAsistenciaService`, `IniciarJornada`, y `FinalizarJornada`.
