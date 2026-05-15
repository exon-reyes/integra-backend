
## **MARCO LEGAL (LFT Art.76-81)**
### **Festivos Oficiales (Art. 74)**
Acorde a la LFT en México. Al calcular periodos vacacionales, aplica la siguiente restricción obligatoria: Los días festivos oficiales estipulados en el Art. 74 de la Ley Federal del Trabajo **NO deben contabilizarse como días de vacaciones**. Si un día festivo cae dentro del rango de fechas solicitado por el empleado, el sistema debe omitirlo del conteo y mantener intacto el saldo de vacaciones del usuario para esa fecha específica.

- **Regla:** Los días feriados por ley (Art. 74 LFT) son descansos obligatorios, **no vacaciones**.

- **Criterios de Aplicación:** Si el empleado pide vacaciones en una semana que incluye el 16 de septiembre o el 25 de diciembre, el sistema debe **saltarse ese día** en el conteo. El empleado disfruta el día, pero su saldo de vacaciones permanece intacto.
### **Días de descanso obligatorios**
Al calcular el periodo de vacaciones, excluye los días de descanso del conteo de días consumidos. Si una fecha solicitada coincide con un día de descanso programado del usuario, el sistema debe marcarlo como 'No Computable' y mantener intacto el saldo de vacaciones para ese día específico.

- **Regla:** Si el descanso semanal (ej. domingo) cae dentro de las vacaciones, **no se cuenta.**

- **Criterios de Aplicación:** El sistema debe detectar la jornada del empleado al solicitar las vacaciones en períodos extendidos mayores a 7 días. Si el rango pedido es de lunes a domingo, el sistema gestor solo debe descontar **6 días**, protegiendo su día de descanso habitual.
### **Derecho mínimo por antigüedad**
Tras un año de servicio conforme a la reforma del Art. 76 de la LFT, todo trabajador adquiere al menos 12 días laborables de vacaciones pagadas. Este período aumenta en 2 días por cada año adicional hasta llegar a 20 días al cumplir 5 años. A partir del sexto año se añaden 2 días cada 5 años (por ejemplo, del año 6 al 10 corresponde 22 días; del 11 al 15, 24 días; etc.).

- **Regla:** Tras el primer año de servicios, el trabajador tendrá derecho a **12 días laborables** de vacaciones, aumentando **2 días** por cada año subsecuente hasta llegar a **20**. A partir del sexto año, el incremento será de **2 días por cada 5 años** de servicio.

- **Criterios de Aplicación:** El sistema debe ejecutar una validación de "Aniversario Laboral" cada 24 horas. Al detectar que un usuario cumple un año adicional de antigüedad, deberá:
1. Identificar el rango de años correspondiente según la tabla legal vigente.
1. Acreditar el nuevo paquete de días al saldo del usuario.

*Tabla *1*. \
Referencia para cómputo de saldos*

|**Años cumplidos**|**Días acreditados**|**Progresión**|
| - | - | - |
|1 año|12 días|Base inicial|
|2 años|14 días|+2 días|
|3 años|16 días|+2 días|
|4 años|18 días|+2 días|
|**5 años**|**20 días**|**Límite de incremento anual**|
|6 a 10 años|22 días|+2 días por cada 5 años|
|11 a 15 años|24 días|+2 días por cada 5 años|

### **Plazo de disfrute de vacaciones (6 meses)**
Para dar cumplimiento al Art. 81 de la LFT, el sistema supervisa que el periodo vacacional se otorgue y disfrute dentro del tiempo legal establecido, evitando el acumulamiento excesivo y garantizando el descanso oportuno del colaborador.

- **Regla:** Las vacaciones deben concederse y disfrutarse obligatoriamente dentro de los 6 meses siguientes al cumplimiento del aniversario laboral. El empleador tiene la responsabilidad de facilitar este periodo antes de que concluya dicho plazo.

- **Criterios de Aplicación:** El sistema establece un cronograma de vigencia para cada paquete de días generado. Al calcular la fecha límite (Aniversario + 6 meses), el gestor activa un protocolo de seguimiento que incluye:

1. **Prioridad de Consumo:** El sistema descuenta automáticamente los días del periodo más antiguo (próximos a vencer) antes de utilizar los días del ciclo más reciente.
1. **Alertas de Cumplimiento:** Emisión de notificaciones preventivas al colaborador y a su jefe inmediato 60 y 30 días antes de que expire el plazo de los 6 meses para asegurar su programación.

*Tabla *2*.\
Estados de vigencias y plazos de notificación.*

|**Momento del Ciclo**|**Estado del Saldo**|**Acción del Sistema**|
| - | - | - |
|Mes 1|Saldo Recién Generado|Disponible para programación.|
|Mes 4|Próximo a vencer|Notificación preventiva al usuario.|
|Mes 6|Límite legal de disfrute|Alerta de cumplimiento crítico para RR.HH.|

## **PROCEDIMIENTOS: CÁLCULO DE DÍAS DE VACACIONES Y SALDOS**
### **Cálculo automático por antigüedad**
Con base en la fecha de ingreso (desde el catálogo de empleados), el sistema debe calcular la antigüedad de cada colaborador y determina los días de vacaciones que le corresponden según la tabla legal (12, 14, … hasta 30+ días). Debe implementarse la regla: 12 días al año 1, +2 anuales hasta 20 en el año 5, y luego +2 cada cinco años.

### **Distinción de días laborables**
Al calificar la solicitud, el sistema convierte las fechas indicadas en el número real de días laborales (ej. con semana de 6 días). Esto evita errores comunes de contar fines de semana o festivos como si fueran días de vacaciones.

*Se incluye calendario oficial de días feriados y de descansos precargados en el momento de la solicitud de vacaciones, introducidos por el usuario para excluirlos del conteo. Este ingreso será validado en los filtros de aceptación*

El sistema garantiza la precisión en el descuento de saldos al diferenciar automáticamente entre días de asueto y días de trabajo efectivo, asegurando que el colaborador no "gaste" sus vacaciones en fechas que por ley o contrato ya son de descanso.

- **Regla:** Al procesar una solicitud, el sistema transforma el rango de fechas calendario en el **número neto de días laborables**. Quedan estrictamente excluidos del conteo los días de descanso semanal (conforme a la jornada del empleado) y todos los días feriados oficiales estipulados por la LFT.

- **Criterios de Aplicación:** El gestor utiliza un calendario maestro inteligente que interactúa con el perfil de cada usuario:
1. **Configuración de Jornada:** El sistema identifica si el empleado tiene una semana laboral de 5 o 6 días para omitir los descansos correspondientes (ej. sábados y domingos).
1. **Calendario Oficial Precargado:** El motor de cálculo cruza las fechas solicitadas con la base de datos de días feriados nacionales, marcándolos como **"Día No Computable"**.
1. **Transparencia de Selección:** Antes de confirmar la solicitud, el sistema despliega un desglose al usuario donde se especifica: "Periodo seleccionado: 9 días naturales. Días a descontar: 5 días laborables (Excluye 2 fines de semana y 2 días festivos)".

### **Historial y gestión de saldos**
El sistema mantiene un registro auditable y en tiempo real de cada movimiento vacacional, permitiendo que tanto el colaborador como la organización tengan plena visibilidad sobre el uso de este beneficio y el balance remanente.

- **Regla:** El saldo total anual puede ser utilizado de forma fraccionada según los acuerdos internos. Cada solicitud aprobada genera un descuento automático del saldo global, impidiendo cualquier duplicidad o sobregiro de días. El sistema debe distinguir entre "Días Totales", "Días Disfrutados" y "Días Pendientes".

- **Criterios de Aplicación:** El gestor opera bajo un modelo de cuenta corriente donde cada transacción es registrada de forma cronológica:
1. **Validación de Disponibilidad:** Antes de enviar una solicitud, el sistema verifica que el usuario cuente con el saldo suficiente. Si el periodo solicitado excede los días disponibles, la solicitud no podrá ser procesada.
1. **Sincronización en Tiempo Real:** Al momento de la aprobación final, el sistema resta el periodo del saldo total y actualiza el historial visible para el empleado y el aprobador.
1. **Trazabilidad:** Se almacena un registro histórico que detalla las fechas de cada periodo tomado (ej. 6 días en verano y 6 en invierno) para facilitar futuras consultas o aclaraciones.

Tabla 3. \
*Ejemplificación de Control (Estado de Cuenta Vacacional)*

|**Concepto**|**Descripción**|**Ejemplo**|
| - | - | - |
|Saldo Inicial|Días generados por antigüedad.|12 días|
|Días Programados|Solicitudes aprobadas pendientes de goce.|6 días|
|Días Disfrutados|Vacaciones que ya han concluido.|3 días|
|Saldo Disponible|Días que el usuario aún puede solicitar.|3 días|

### **Vacaciones proporcionales**
Este mecanismo asegura que el colaborador reciba la compensación justa por el tiempo efectivamente laborado, incluso si la relación laboral concluye antes de cumplir un aniversario completo o en casos de contratos por tiempo determinado.

- **Regla:** Todo trabajador tiene derecho a percibir la parte proporcional de sus vacaciones conforme al tiempo de servicios prestados durante el año en curso. Si un empleado no cumple el ciclo anual (ya sea por término de contrato o renuncia), se debe calcular el equivalente de días basándose en el mínimo legal de **12 días** (o el que corresponda según su antigüedad).

- **Criterios de Aplicación:** El sistema activa un motor de cálculo de "prorrateo" que realiza las siguientes operaciones:

1. **Cómputo de días laborados:** Contabiliza los días transcurridos desde la fecha de ingreso (o último aniversario) hasta la fecha de baja.
1. **Factor de proporcionalidad:** Divide los días de vacaciones anuales entre 365 para obtener el factor diario, multiplicándolo después por los días laborados.
1. **Resultado Preciso:** El sistema genera el saldo exacto en decimales que debe ser pagado o disfrutado, garantizando que el finiquito sea legalmente exacto.

Tabla 4.\
` `*Ejemplo de Cálculo Automático de vacaciones proporcionales*

|**Escenario**|**Tiempo Laborado**|**Días Proporcionales**|
| - | - | - |
|Contrato Temporal|6 meses (182 días)|6 días (aprox.)|
|Baja Anticipada|3 meses (91 días)|3 días (aprox.)|
|Baja Anticipada|9 meses (273 días)|9 días (aprox.)|

Aquí tienes la versión optimizada y profesional del **Flujo de solicitudes y aprobaciones (híbrido)**, diseñada para ser la base lógica de tu sistema de gestión de vacaciones.

-----
## **3. Flujo de Solicitudes y Aprobaciones**
Se define el ciclo de vida de cada petición, garantizando una comunicación fluida entre el colaborador, su jefe inmediato y Capital Humano.
### **Fase 1: Registro y Validación Automática**
La plataforma actúa como el primer filtro de cumplimiento, permitiendo una autogestión informada antes de involucrar al nivel jerárquico.

- **Regla de Acceso:** El sistema permite registrar peticiones por rangos de fechas o días específicos. Si el colaborador tiene menos de un año de antigüedad, el sistema ejecuta un **bloqueo preventivo** con el mensaje: *"No corresponde aún legalmente por falta de antigüedad"*.
- **Criterios de Aplicación:** Al iniciar la captura, el motor de validación coteja en tiempo real el saldo disponible contra los días laborables solicitados. Si la petición excede el derecho devengado, la solicitud no podrá ser enviada.
### **Fase 2: Revisión, Rechazo y Gestión Operativa**
Una vez enviada, la solicitud se turna al responsable designado (Jefe de Área o RRHH) para una toma de decisiones objetiva.

- **Panel del Aprobador:** El responsable visualiza los detalles críticos: fechas solicitadas, días disponibles del empleado y posibles cruces de vacaciones con otros miembros del equipo.
- **Protocolo de Rechazo:** En caso de no ser procedente, el sistema obliga al ingreso de una justificación razonada. Al confirmar, el empleado es notificado y sus días permanecen intactos en su saldo. El sistema facilita el rechazo de solicitudes que legalmente excedan el saldo o se encuentren fuera de plazo.
- **Alerta de Cobertura:** Para proteger la continuidad del negocio, el sistema emite una alerta si detecta que la aprobación generaría una falta de personal crítico en el mismo equipo durante esas fechas.
### **Fase 3: Aprobación, Confirmación y Fraccionamiento**
Al concretarse la autorización, el sistema formaliza el movimiento y asegura el control contable de los días.

- **Confirmación y Descuento:** La plataforma reserva las fechas en el calendario laboral, notifica al colaborador y realiza la deducción automática del saldo.
- **Distribución Flexible:** El sistema soporta que el empleado fraccione su periodo anual en múltiples solicitudes a lo largo del año. Se llevará un **historial de tramos utilizados**, sumando cada uno al consumo total anual para evitar duplicidades.
### **Fase 4: Plazos y Recordatorios Inteligentes**
El gestor actúa como un monitor activo para garantizar que las vacaciones se tomen en tiempo y forma.

- **Avisos de Cumplimiento Legal:** Al cumplirse **5 meses** tras el aniversario laboral sin que el empleado haya planificado su descanso, el sistema notificará al colaborador y a RRHH para programar el periodo antes del límite legal de **6 meses**.
- **Alertas de Gestión:** El sistema alertará al aprobador si una solicitud lleva tiempo considerable pendiente de respuesta, evitando retrasos en la planeación del personal.



Tabla 5.\
` `*Estados del flujo de solicitudes de vacaciones*

|**Estado**|**Significado para el Negocio**|**Acción del Sistema**|
| - | - | - |
|Bloqueado|Falta de antigüedad (< 1 año).|Impide el envío de la solicitud.|
|Pendiente|En revisión por el Jefe/RRHH.|Envía recordatorios si no hay respuesta.|
|Rechazado|Solicitud no procedente.|Exige motivo y libera los días al saldo.|
|Aprobado|Vacaciones confirmadas.|Deduce días y actualiza el calendario.|

## **Notificaciones, reportes y constancias**
### **Notificaciones automáticas**
El sistema enviará correos o alertas internas en eventos clave: solicitud recibida, solicitud aprobada/rechazada, recordatorio de uso de vacaciones. Por ejemplo, cuando el plazo de 6 meses se aproxima sin que el empleado haya agendado sus vacaciones, puede alertarse al área de interés vía correo electrónico.
### **Reportes e informes**
Se generarán reportes periódicos para RRHH, tales como: lista de solicitudes pendientes, días de vacaciones usados vs. disponibles por área o por empleado, etc. para facilita la planificación operativa y el cumplimiento normativo.
### **Constancia de vacaciones**
Cada año, el sistema deberá poder emitir la constancia anual exigida por la ley[11]. Este documento incluirá el nombre del trabajador, fecha de ingreso, antigüedad (en años), número de días de vacaciones asignados para el año y fechas acordadas de disfrute. Puede ser una generación automática de PDF firmable por RRHH.
### **Auditoría y trazabilidad**
Para fines de inspección o reclamo, debe mantenerse un registro de todas las operaciones: quién solicitó, quién aprobó, fechas y motivos de rechazo. Estos logs(registrados en tablas SQL) permiten verificar que se cumple con los plazos legales (6 meses, 12 días mínimos, etc.) y dar respuestas en caso de auditorías laborales internas de áreas de interés.
## **Consideraciones operativas adicionales**
### **Interfaz de usuario clara**
Debe ser accesible y sencilla. Empleados y jefes deben visualizar fácilmente su saldo de vacaciones, histórico de solicitudes y calendario de aprobaciones. Un **calendario gráfico** que muestre ausencias planificadas ayuda a prever cargas de trabajo.
### **Automatización parcial (híbrida)**
Según la petición, el modelo es híbrido: el sistema realiza los cálculos automáticos (saldos, disponibilidad, plazos) y envía notificaciones, pero las decisiones críticas (aprobación final, resolución de solapamientos) quedan a cargo de personas (gerentes/RRHH). Esto garantiza precisión legal con control humano.
### **Políticas internas**
Aunque no se otorgan beneficios extra por convenio, el sistema puede soportar configuraciones internas, por ejemplo: días máximos consecutivos autorizables o reglas de solicitud con cierta anticipación. Estas políticas deben reflejarse como parámetros configurables.
