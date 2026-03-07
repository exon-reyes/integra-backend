# Vencimiento de Vacaciones - LFT Art. 81

## Fecha: 2026-03-05

---

## ⚖️ MARCO LEGAL

**Ley Federal del Trabajo - Artículo 81**:
> "Las vacaciones deberán concederse a los trabajadores dentro de los seis meses siguientes al cumplimiento del año de servicios."

### Consecuencia

Si el empleado **NO toma** sus vacaciones dentro de los **6 meses** posteriores a su aniversario laboral, esos días se **VENCEN** y ya no pueden ser utilizados.

---

## 🔄 IMPLEMENTACIÓN

### Job Automático de Vencimiento

**Servicio**: `VencimientoPeriodoService`
**Frecuencia**: Diario a las 3 AM

```java
@Scheduled(cron = "0 0 3 * * *")
public void vencerPeriodosExpirados() {
    // 1. Busca períodos con fecha_caducidad < HOY
    // 2. Cambia estatus a VENCIDO
    // 3. Los días restantes quedan congelados
}
```

### Flujo de Vencimiento

```
Empleado ingresa: 2023-08-01
    ↓
Aniversario año 1: 2024-08-01 → 12 días
    ↓
Fecha caducidad: 2025-02-01 (+ 6 meses)
    ↓
Si NO toma vacaciones antes del 2025-02-01:
    ↓
Job ejecuta el 2025-02-02:
    - estatus: VIGENTE → VENCIDO
    - dias_restantes: 12 (congelados)
    ↓
Dashboard muestra:
    - Disponibles: 0 (no incluye vencidos)
    - Días del Año: 0 (solo vigentes)
```

---

## 📊 IMPACTO EN DASHBOARD

### Antes del Vencimiento

```
Período 1 (2024-08-01):
  - dias_habilitados: 12
  - dias_restantes: 12
  - fecha_caducidad: 2025-02-01
  - estatus: VIGENTE

Dashboard:
  - Días del Año: 12
  - Disponibles: 12
  - Disfrutados: 0
```

### Después del Vencimiento (2025-02-02)

```
Período 1 (2024-08-01):
  - dias_habilitados: 12
  - dias_restantes: 12
  - fecha_caducidad: 2025-02-01
  - estatus: VENCIDO ❌

Dashboard:
  - Días del Año: 0 (no cuenta vencidos)
  - Disponibles: 0 (no cuenta vencidos)
  - Disfrutados: 0
```

---

## 🎯 LÓGICA DE CÁLCULO

### Días Disponibles

```java
// SOLO cuenta períodos VIGENTES
int diasDisponibles = periodoRepository.sumDiasRestantesByEmpleado(empleadoId);

// Query SQL:
SELECT COALESCE(SUM(dias_restantes), 0) 
FROM periodos_vacacionales 
WHERE empleado_id = ? 
  AND estatus = 'VIGENTE'  -- ✅ Excluye VENCIDOS
```

### Días del Año Actual

```java
// SOLO períodos vigentes con caducidad futura
List<PeriodoVacacionalEntity> periodosVigentes = periodoRepository
    .findByEmpleadoIdAndEstatus(empleadoId, EstatusPeriodo.VIGENTE);

int diasAnioActual = periodosVigentes.stream()
    .filter(p -> p.getFechaCaducidad().isAfter(LocalDate.now()))
    .mapToInt(PeriodoVacacionalEntity::getDiasHabilitados)
    .sum();
```

---

## ⚠️ ALERTAS PREVENTIVAS

### Alerta 60 Días Antes

```java
if (LocalDate.now().isAfter(periodo.getFechaCaducidad().minusDays(60))) {
    // Enviar notificación al empleado
    // "Tienes 12 días que vencen el 2025-02-01"
}
```

### Alerta 30 Días Antes

```java
if (LocalDate.now().isAfter(periodo.getFechaCaducidad().minusDays(30))) {
    // Enviar notificación urgente
    // "¡URGENTE! Tienes 12 días que vencen en 30 días"
}
```

### Dashboard - Días Próximos a Vencer

```java
if (LocalDate.now().isAfter(proximoAniversario.minusMonths(1))) {
    diasProximosVencer = diasDisponibles;
    fechaProximoVencer = limiteLegal;
}
```

---

## 📋 EJEMPLO REAL

### Empleado: Juan Pérez

**Fecha ingreso**: 2023-08-01

| Aniversario | Días | Caducidad | Estado Hoy (2026-03-05) |
|-------------|------|-----------|-------------------------|
| 2024-08-01 | 12 | 2025-02-01 | ❌ VENCIDO (no tomó) |
| 2025-08-01 | 14 | 2026-02-01 | ❌ VENCIDO (no tomó) |
| 2026-08-01 | 16 | 2027-02-01 | ⏳ Pendiente (futuro) |

**Dashboard actual**:
- Días del Año: 0
- Disponibles: 0
- Disfrutados: 0
- **Días perdidos**: 26 (12 + 14)

---

## 🔧 RECUPERACIÓN DE DÍAS VENCIDOS

### ¿Se pueden recuperar?

**NO**. Según la LFT, una vez vencido el plazo de 6 meses, el derecho se pierde.

### Excepción

Solo si existe **acuerdo por escrito** entre empleado y patrón para diferir las vacaciones, pero esto debe documentarse ANTES del vencimiento.

---

## 🎓 CONCLUSIÓN

**Regla**: Vacaciones NO tomadas en 6 meses = **VENCIDAS**

**Implementación**:
- ✅ Job diario vence períodos automáticamente
- ✅ Dashboard solo muestra días VIGENTES
- ✅ Alertas preventivas 60 y 30 días antes
- ✅ Auditoría completa de días perdidos

**Beneficio**: Cumplimiento legal y transparencia total.
