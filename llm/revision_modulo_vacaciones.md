# Revisión del Módulo de Gestión de Vacaciones

## Fecha: 2026-03-05
## Estado: REQUIERE MEJORAS CRÍTICAS

---

## 1. INTEGRACIÓN FRONTEND-BACKEND

### ✅ Aspectos Positivos
- **Contratos API bien definidos**: Los DTOs coinciden con los modelos TypeScript
- **Servicios Angular correctamente implementados**: Uso de `inject()` y signals
- **Endpoints REST consistentes**: Nomenclatura clara y RESTful

### ❌ Problemas Críticos Detectados

#### 1.1 Falta de Gestión de Descansos del Empleado
**Problema**: El documento especifica que "antes de solicitar vacaciones el empleado debe ingresar sus descansos", pero NO existe:
- ❌ Endpoint para que el empleado configure sus días de descanso
- ❌ Componente Angular para capturar esta información
- ❌ Validación que obligue al empleado a tener descansos configurados

**Impacto**: El sistema usa un default (domingo) si no hay configuración, violando el requisito de negocio.

**Solución Requerida**:
```java
// Backend: Nuevo Controller
@PostMapping("/empleados/{id}/descansos")
public ResponseEntity<Void> configurarDescansos(
    @PathVariable Integer id, 
    @RequestBody ConfiguracionDescansoRequest request
)

// Frontend: Nuevo componente
<app-configuracion-descansos [empleadoId]="empleadoId" />
```

#### 1.2 Inconsistencia en Tabla de Descansos
**Problema**: La tabla `configuracion_descanso_empleado` tiene constraint `UNIQUE KEY uk_empleado_descanso (empleado_id)`, pero un empleado puede tener MÚLTIPLES días de descanso (ej: sábado Y domingo).

**SQL Actual (INCORRECTO)**:
```sql
UNIQUE KEY `uk_empleado_descanso` (`empleado_id`)
```

**SQL Correcto**:
```sql
UNIQUE KEY `uk_empleado_dia` (`empleado_id`, `dia_descanso`)
```

#### 1.3 Mapeo Incorrecto de Días de la Semana
**Problema**: El comentario SQL dice `0=Lunes...6=Domingo`, pero Java `DayOfWeek.getValue()` retorna `1=Lunes...7=Domingo`.

**Código Actual**:
```java
int diaSemana = fecha.getDayOfWeek().getValue(); // 1-7
Set<Integer> diasDescanso = Set.of(7); // Default domingo
```

**Inconsistencia**: Si la BD usa 0-6 pero Java usa 1-7, habrá errores de cálculo.

---

## 2. CUMPLIMIENTO DE ESTÁNDARES

### ❌ Violaciones a Clean Architecture

#### 2.1 Lógica de Negocio en Service en lugar de Domain
**Problema**: `VacacionCommandService` tiene lógica de descuento de días que debería estar en la entidad de dominio.

**Código Actual (INCORRECTO)**:
```java
// VacacionCommandService.java - línea 73
for (PeriodoVacacionalEntity periodo : periodoVacacionalRepository.findPeriodosDisponiblesOrdenados(...)) {
    int aDescontar = Math.min(solicitud.getDiasLaborables(), periodo.getDiasRestantes());
    periodo.descontarDias(aDescontar);
    periodoVacacionalRepository.save(periodo);
}
```

**Solución (CORRECTO)**:
```java
// Domain Service
public class GestionSaldoVacacionService {
    public void descontarDiasDePeriodos(Integer empleadoId, int diasADescontar) {
        // Lógica de negocio aquí
    }
}
```

#### 2.2 Mapeo Manual en Service
**Problema**: Método `mapToDTO()` duplicado en `VacacionCommandService` y `VacacionQueryService`.

**Violación**: DRY (Don't Repeat Yourself)

**Solución**:
```java
// Crear Mapper dedicado
@Component
public class SolicitudVacacionMapper {
    public SolicitudVacacionDTO toDTO(SolicitudVacacionEntity entity) { ... }
}
```

### ❌ Violaciones a Principios SOLID

#### 2.3 Violación de Single Responsibility
**Problema**: `VacacionCommandService` hace demasiado:
- Crea solicitudes
- Aprueba/rechaza
- Cancela
- Pre-calcula
- Registra auditoría
- Mapea a DTO

**Solución**: Separar en servicios especializados:
```java
- SolicitudCreacionService
- SolicitudAprobacionService  
- SolicitudCancelacionService
- AuditoriaVacacionService
```

---

## 3. FLUJO Y OPTIMIZACIÓN DE PROCESOS

### ❌ Problemas de Flujo de Negocio

#### 3.1 Falta Validación de Descansos Configurados
**Problema**: `ValidacionSolicitudService` NO valida que el empleado tenga descansos configurados antes de solicitar.

**Código Faltante**:
```java
private void validarDescansoConfigurado(Integer empleadoId) {
    Set<Integer> descansos = descansoRepository.findDiasDescansoByEmpleado(empleadoId);
    if (descansos.isEmpty()) {
        throw VacacionException.descansosNoConfigurados();
    }
}
```

#### 3.2 Cálculo de Días Ineficiente
**Problema**: `CalculoDiasLaboralesService.calcular()` itera día por día, ineficiente para rangos largos.

**Código Actual (O(n))**:
```java
LocalDate fecha = inicio;
while (!fecha.isAfter(fin)) {
    // Procesa cada día individualmente
    fecha = fecha.plusDays(1);
}
```

**Optimización Sugerida (O(1))**:
```java
// Calcular días totales
long diasTotales = ChronoUnit.DAYS.between(inicio, fin) + 1;

// Calcular fines de semana matemáticamente
int semanas = (int) diasTotales / 7;
int diasRestantes = (int) diasTotales % 7;
int finesSemana = semanas * diasDescanso.size();

// Solo iterar días restantes
```

#### 3.3 Falta Notificaciones
**Problema**: El documento especifica notificaciones automáticas, pero NO están implementadas:
- ❌ Notificación al crear solicitud
- ❌ Notificación al aprobar/rechazar
- ❌ Alertas de vencimiento (60 y 30 días)

**Solución Requerida**:
```java
@Service
public class NotificacionVacacionService {
    public void notificarSolicitudCreada(SolicitudVacacionEntity solicitud);
    public void notificarAprobacion(SolicitudVacacionEntity solicitud);
    public void alertarProximoVencimiento(PeriodoVacacionalEntity periodo);
}
```

#### 3.4 Falta Job Programado para Generación de Períodos
**Problema**: El documento especifica "validación de Aniversario Laboral cada 24 horas", pero NO existe job programado.

**Solución Requerida**:
```java
@Scheduled(cron = "0 0 2 * * *") // 2 AM diario
public void generarPeriodosVacacionales() {
    List<EmpleadoEntity> empleadosConAniversario = 
        empleadoRepository.findEmpleadosConAniversarioHoy();
    
    for (EmpleadoEntity empleado : empleadosConAniversario) {
        periodoService.generarNuevoPeriodo(empleado);
    }
}
```

---

## 4. PROBLEMAS DE SEGURIDAD

### ❌ Falta Validación de Autorización

#### 4.1 Endpoints Sin Validación de Permisos
**Problema**: Los controllers NO validan que el usuario autenticado tenga permiso para la acción.

**Código Actual (INSEGURO)**:
```java
@GetMapping("/dashboard")
public ResponseEntity<DashboardVacacionDTO> getDashboard(@RequestParam Integer empleadoId) {
    // ❌ Cualquier usuario puede ver el dashboard de cualquier empleado
    return ResponseEntity.ok(queryService.obtenerDashboard(empleadoId));
}
```

**Solución (SEGURO)**:
```java
@GetMapping("/dashboard")
public ResponseEntity<DashboardVacacionDTO> getDashboard(
    @RequestParam Integer empleadoId,
    @AuthenticationPrincipal UserDetails user
) {
    if (!authService.puedeVerDashboard(user, empleadoId)) {
        throw new AccessDeniedException("Sin permisos");
    }
    return ResponseEntity.ok(queryService.obtenerDashboard(empleadoId));
}
```

---

## 5. PROBLEMAS EN FRONTEND

### ❌ Violaciones a Estándares Angular 21

#### 5.1 Uso de `@Input()` en lugar de `input()`
**Problema**: `SolicitudVacacionesComponent` usa decorador antiguo.

**Código Actual (INCORRECTO)**:
```typescript
@Input() empleadoId: number = 1;
```

**Código Correcto (Angular 21)**:
```typescript
empleadoId = input.required<number>();
```

#### 5.2 Template Inline Muy Grande
**Problema**: El componente tiene template inline de 150+ líneas, viola principio de componentes pequeños.

**Solución**: Separar en componentes:
```typescript
- SolicitudFormComponent
- CalculoDiasDisplayComponent  
- InformacionLegalComponent
```

#### 5.3 Falta Manejo de Estados de Carga
**Problema**: No hay indicador visual mientras se calcula días.

**Solución**:
```typescript
@if (loading()) {
  <p-progressSpinner />
}
```

---

## 6. OPTIMIZACIONES REQUERIDAS

### 6.1 Caché de Festivos
**Problema**: Se consulta BD en cada cálculo de días.

**Solución**:
```java
@Cacheable("festivos")
public List<FestivoEntity> findFestivosBetween(LocalDate inicio, LocalDate fin)
```

### 6.2 Consultas N+1
**Problema**: `VacacionQueryService.obtenerCalendarioDepartamento()` hace consulta por cada empleado.

**Solución**:
```java
@Query("SELECT s FROM SolicitudVacacionEntity s " +
       "JOIN FETCH s.empleado e " +
       "WHERE e.departamento.id = :deptId")
List<SolicitudVacacionEntity> findByDepartamentoIdWithEmpleado(@Param("deptId") Integer deptId);
```

### 6.3 Índices Faltantes
**Problema**: Falta índice compuesto para consultas frecuentes.

**SQL Requerido**:
```sql
CREATE INDEX idx_solicitud_empleado_fecha 
ON solicitud_vacacion(empleado_id, fecha_inicio, fecha_fin);
```

---

## 7. RESUMEN DE ACCIONES REQUERIDAS

### 🔴 CRÍTICO (Bloquea funcionalidad)
1. ✅ Crear endpoint y UI para configuración de descansos del empleado
2. ✅ Corregir constraint UNIQUE en tabla `configuracion_descanso_empleado`
3. ✅ Estandarizar mapeo de días de semana (0-6 vs 1-7)
4. ✅ Agregar validación de descansos configurados antes de solicitar

### 🟡 IMPORTANTE (Mejora calidad)
5. ✅ Extraer mapeo a clase Mapper dedicada
6. ✅ Separar `VacacionCommandService` en servicios especializados
7. ✅ Implementar sistema de notificaciones
8. ✅ Crear job programado para generación de períodos
9. ✅ Agregar validación de autorización en endpoints
10. ✅ Optimizar cálculo de días laborables

### 🟢 DESEABLE (Optimización)
11. ✅ Implementar caché de festivos
12. ✅ Resolver consultas N+1
13. ✅ Agregar índices de BD
14. ✅ Refactorizar componentes Angular grandes
15. ✅ Usar `input()` signal en lugar de `@Input()`

---

## 8. CONCLUSIÓN

El módulo tiene una **base sólida** pero requiere **mejoras críticas** antes de producción:

- ✅ **Integración API**: Bien estructurada
- ❌ **Flujo de negocio**: Incompleto (falta configuración de descansos)
- ❌ **Estándares**: Violaciones a Clean Architecture y DRY
- ❌ **Seguridad**: Falta validación de autorización
- ⚠️ **Performance**: Optimizable (caché, índices, consultas)

**Recomendación**: Implementar acciones CRÍTICAS antes de despliegue.
