# Fuente de Verdad - Gestión de Vacaciones

## Fecha: 2026-03-05

---

## ✅ FUENTE DE VERDAD ÚNICA

### Datos Maestros (Source of Truth)

**Tabla**: `empleado`
- `fecha_alta` → Fecha de ingreso original
- `fecha_reingreso` → Fecha de reingreso (si aplica, sobrescribe fecha_alta)

**Regla**: La antigüedad SIEMPRE se calcula desde `EmpleadoEntity`:
```java
LocalDate fechaBase = empleado.getFechaReingreso() != null 
    ? empleado.getFechaReingreso() 
    : empleado.getFechaAlta();

long antiguedad = ChronoUnit.YEARS.between(fechaBase, LocalDate.now());
```

---

## 📊 DATOS DERIVADOS (Calculados)

### Tabla: `periodos_vacacionales`

**NO duplica** `fecha_alta`, sino que almacena **períodos generados** basándose en ella:

```sql
CREATE TABLE periodos_vacacionales (
  id BIGINT PRIMARY KEY,
  empleado_id INT,              -- FK a empleado
  anio_laboral INT,             -- Año cumplido (1, 2, 3...)
  fecha_inicio DATE,            -- Inicio del período
  fecha_fin DATE,               -- Fin del período
  dias_habilitados INT,         -- Días otorgados
  dias_tomados INT,             -- Días consumidos
  dias_restantes INT,           -- Saldo actual
  fecha_caducidad DATE,         -- Límite legal (aniversario + 6 meses)
  estatus ENUM('VIGENTE','VENCIDO','CONSUMIDO')
);
```

**Propósito**: Mantener el **historial de saldos** por cada aniversario laboral.

---

## 🔄 SINCRONIZACIÓN AUTOMÁTICA

### Job Programado

**Servicio**: `PeriodoVacacionalSyncService`
**Frecuencia**: Diario a las 2 AM

```java
@Scheduled(cron = "0 0 2 * * *")
public void generarPeriodosAutomaticos() {
    // 1. Obtiene política activa
    // 2. Busca empleados activos
    // 3. Calcula antigüedad desde EmpleadoEntity.fechaAlta
    // 4. Genera período si cumple aniversario HOY
    // 5. Calcula días según tabla legal
}
```

### Flujo de Sincronización

```
EmpleadoEntity.fechaAlta (2023-03-05)
         ↓
   Hoy: 2026-03-05
         ↓
   Antigüedad: 3 años
         ↓
   Política LFT: 16 días (12 + 2 + 2)
         ↓
   Genera PeriodoVacacionalEntity:
   - anio_laboral: 3
   - dias_habilitados: 16
   - fecha_caducidad: 2026-09-05 (+ 6 meses)
```

---

## 🎯 VENTAJAS DE ESTE DISEÑO

### 1. Single Source of Truth
- ✅ `fecha_alta` solo existe en `empleado`
- ✅ No hay duplicación de datos
- ✅ Cambios en empleado se reflejan automáticamente

### 2. Historial Auditable
- ✅ Cada período es un registro independiente
- ✅ Se puede rastrear cuántos días tuvo cada año
- ✅ Cumple con requisitos legales de auditoría

### 3. Manejo de Casos Especiales
- ✅ Reingreso: usa `fecha_reingreso` en lugar de `fecha_alta`
- ✅ Baja/Alta: genera nuevos períodos desde reingreso
- ✅ Cambio de política: solo afecta períodos futuros

---

## 🔧 SINCRONIZACIÓN MANUAL

### Endpoint Admin

```bash
# Sincronizar todos los empleados
POST /api/v1/vacaciones/admin/sincronizar-periodos

# Sincronizar un empleado específico
POST /api/v1/vacaciones/admin/sincronizar-empleado/123
```

### Casos de Uso

1. **Migración inicial**: Generar períodos para empleados existentes
2. **Corrección de datos**: Regenerar período si hubo error
3. **Cambio de fecha_alta**: Sincronizar después de corrección

---

## 📋 EJEMPLO PRÁCTICO

### Empleado: Juan Pérez

**Datos Maestros** (`empleado`):
```
id: 123
nombre: Juan Pérez
fecha_alta: 2020-01-15
fecha_reingreso: NULL
estatus: A
```

**Períodos Generados** (`periodos_vacacionales`):

| Año | Fecha Generación | Días | Caducidad | Estatus |
|-----|------------------|------|-----------|---------|
| 1 | 2021-01-15 | 12 | 2021-07-15 | VENCIDO |
| 2 | 2022-01-15 | 14 | 2022-07-15 | CONSUMIDO |
| 3 | 2023-01-15 | 16 | 2023-07-15 | VIGENTE |
| 4 | 2024-01-15 | 18 | 2024-07-15 | VIGENTE |
| 5 | 2025-01-15 | 20 | 2025-07-15 | VIGENTE |

**Saldo Actual**: 16 + 18 + 20 = 54 días disponibles

---

## ⚠️ IMPORTANTE

### NO Duplicar Datos

❌ **INCORRECTO**:
```java
// Guardar fecha_alta en periodos_vacacionales
periodo.setFechaAlta(empleado.getFechaAlta()); // NO HACER
```

✅ **CORRECTO**:
```java
// Calcular antigüedad en tiempo real
LocalDate fechaBase = empleado.getFechaReingreso() != null 
    ? empleado.getFechaReingreso() 
    : empleado.getFechaAlta();
long antiguedad = ChronoUnit.YEARS.between(fechaBase, LocalDate.now());
```

### Siempre Consultar Empleado

Cuando necesites antigüedad:
```java
EmpleadoEntity empleado = empleadoRepository.findById(empleadoId).get();
// Calcular desde empleado, NO desde período
```

---

## 🎓 CONCLUSIÓN

**Fuente de Verdad**: `EmpleadoEntity.fechaAlta` + `fechaReingreso`

**Datos Derivados**: `PeriodoVacacionalEntity` (generados automáticamente)

**Sincronización**: Job diario + endpoints manuales

**Beneficio**: Consistencia, auditoría y cumplimiento legal garantizados.
