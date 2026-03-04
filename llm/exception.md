# Estrategia de Manejo de Excepciones - Proyecto Integra

## 📋 Resumen Ejecutivo

Este documento describe la estrategia completa de manejo de excepciones implementada en el proyecto Integra, una API REST con Spring Boot utilizando arquitectura por paquetes orientada a dominio.

## 🏗️ Arquitectura de Excepciones

### Jerarquía de Excepciones

```
IntegraException (abstracta - base para todas)
├── BusinessException (excepciones de negocio/dominio)
│   ├── AccesoException
│   ├── AsistenciaDomainException
│   ├── CredencialesException
│   ├── EmpleadoException
│   ├── EmpresaException
│   ├── ObservacionException
│   ├── UnidadException
│   └── ReporteException
└── TechnicalException (excepciones técnicas/infraestructura)
```

### Ubicación de Archivos

```
src/main/java/integra/
├── global/exception/
│   ├── IntegraException.java
│   ├── BusinessException.java
│   ├── TechnicalException.java
│   ├── code/ErrorCode.java
│   ├── handler/GlobalExceptionHandler.java
│   └── response/ApiError.java
├── acceso/exception/AccesoException.java
├── asistencia/exception/AsistenciaDomainException.java
├── credenciales/exception/CredencialesException.java
├── empleado/exception/EmpleadoException.java
├── empresa/exception/EmpresaException.java
├── empresa/unidad/exception/UnidadException.java
├── observacion/exception/ObservacionException.java
└── reportes/exception/ReporteException.java
```

## 📑 ErrorCode Centralizado

El enum `ErrorCode` define códigos estandarizados con formato: `XXX-NNN`

### Prefijos por Módulo

| Prefijo | Módulo | Ejemplo |
|---------|--------|---------|
| GEN | Errores generales | GEN-001: Error interno |
| VAL | Validación | VAL-001: Solicitud inválida |
| AUT | Autenticación | AUT-001: Credenciales inválidas |
| BUS | Reglas de negocio | BUS-001: Operación no permitida |
| DAT | Datos | DAT-001: Recurso no encontrado |
| TEC | Técnicos | TEC-001: Error de conexión DB |
| EMP | Empleados | EMP-001: Empleado no encontrado |
| EMR | Empresa | EMR-001: Empresa no encontrada |
| UND | Unidades | UND-001: Unidad no encontrada |
| ACC | Acceso | ACC-001: Usuario no encontrado |
| REP | Reportes | REP-001: Reporte no encontrado |
| ASI | Asistencia | ASI-001: Registro no encontrado |
| OBS | Observaciones | OBS-001: Observación no encontrada |

## 🔧 Excepciones por Dominio

### 1. AccesoException
**Ubicación:** `integra.acceso.exception`

**Factory Methods:**
- `userNotFound(String username)` - Usuario no encontrado
- `duplicateUser(String username)` - Usuario duplicado
- `duplicateUserByMessage(String message)` - Usuario duplicado con mensaje personalizado
- `roleNotFound(Long roleId)` - Rol no encontrado
- `duplicateRole(String roleName)` - Rol duplicado
- `roleHasUsers(Long roleId)` - Rol con usuarios asignados
- `passwordMismatch()` - Contraseñas no coinciden
- `weakPassword(String details)` - Contraseña débil
- `invalidResetToken()` - Token de restablecimiento inválido
- `passwordResetBlocked(String reason)` - Restablecimiento bloqueado
- `sessionExpired()` - Sesión expirada
- `accessDenied(String reason)` - Acceso denegado

### 2. AsistenciaDomainException
**Ubicación:** `integra.asistencia.exception`

**Factory Methods:**
- `notFound(Long registroId)` - Registro no encontrado
- `duplicateEntry(Long empleadoId, String fecha)` - Registro duplicado
- `invalidTime(String hora)` - Hora inválida
- `outsideSchedule(String horarioPermitido)` - Fuera de horario
- `kioscoNotConfigured(Long kioscoId)` - Kiosco no configurado
- `invalidPin()` - PIN inválido
- `kioscoBlocked(Long kioscoId)` - Kiosco bloqueado
- `invalidDateRange(String fechaInicio, String fechaFin)` - Rango de fechas inválido
- `pausaActivaExistente(Integer empleadoId)` - Pausa activa existente
- `jornadaNotFound(Integer jornadaId)` - Jornada no encontrada
- `invalidActionType(String tipoAccion)` - Tipo de acción inválido

### 3. CredencialesException
**Ubicación:** `integra.credenciales.exception`

**Factory Methods:**
- `cuentaNotFound(Integer cuentaId)` - Cuenta no encontrada
- `duplicateUser(String usuario)` - Usuario duplicado
- `tipoCuentaNotFound(Integer tipoId)` - Tipo de cuenta no encontrado
- `duplicateTipoCuenta(String nombre)` - Tipo de cuenta duplicado
- `unidadNotFound(Long unidadId)` - Unidad no encontrada (referencia)
- `departamentoNotFound(Integer departamentoId)` - Departamento no encontrado

### 4. EmpleadoException
**Ubicación:** `integra.empleado.exception`

**Factory Methods:**
- `notFound(Long empleadoId)` - Empleado no encontrado
- `duplicateNip(String nip)` - NIP duplicado
- `duplicateEmail(String email)` - Email duplicado
- `notActive(Long empleadoId)` - Empleado inactivo
- `alreadyAssigned(Long empleadoId)` - Empleado ya asignado
- `invalidNip(String nip)` - NIP inválido

### 5. EmpresaException
**Ubicación:** `integra.empresa.exception`

**Factory Methods:**
- `notFound(Long empresaId)` - Empresa no encontrada
- `duplicateRfc(String rfc)` - RFC duplicado
- `hasDepartments(Long empresaId)` - Empresa con departamentos
- `invalidRfc(String rfc)` - RFC inválido

### 6. UnidadException
**Ubicación:** `integra.empresa.unidad.exception`

**Factory Methods:**
- `notFound(Long unidadId)` - Unidad no encontrada
- `duplicateCode(String codigo)` - Código duplicado
- `hasEmployees(Long unidadId)` - Unidad con empleados
- `parentNotFound(Long parentId)` - Unidad padre no encontrada
- `circularReference(Long unidadId, Long parentId)` - Referencia circular
- `invalidCode(String codigo)` - Código inválido
- `duplicateEmail(String email)` - Email duplicado
- `duplicateTelefono(String telefono)` - Teléfono duplicado
- `duplicateNombre(String nombre)` - Nombre duplicado
- `duplicateCodigoAutorizacion(String codigo)` - Código autorización duplicado
- `zonaNotFound(Integer zonaId)` - Zona no encontrada
- `estadoNotFound(Integer estadoId)` - Estado no encontrado

### 7. ObservacionException
**Ubicación:** `integra.observacion.exception`

**Factory Methods:**
- `notFound(Integer observacionId)` - Observación no encontrada
- `estatusFinalNoModificable()` - Estatus final no modificable
- `categoriaInvalida(String categoria)` - Categoría inválida
- `estatusInvalido(Integer estatusId)` - Estatus inválido
- `transicionEstatusNoPermitida(String actual, String nuevo)` - Transición no permitida
- `duplicate(String identificador)` - Observación duplicada

### 8. ReporteException
**Ubicación:** `integra.reportes.exception`

**Factory Methods:**
- `notFound(Long reporteId)` - Reporte no encontrado
- `generationFailed(String message, Throwable cause)` - Error al generar
- `invalidDateRange(String fechaInicio, String fechaFin)` - Rango inválido
- `emptyResult(Long reporteId)` - Reporte sin datos
- `exportFailed(String format, Throwable cause)` - Error al exportar

## 🎯 Buenas Prácticas Implementadas

### Uso Correcto de Excepciones

```java
// ✅ CORRECTO: Usar excepción específica del dominio
throw EmpleadoException.notFound(empleadoId);

// ✅ CORRECTO: Usar factory method con contexto
throw UnidadException.duplicateEmail(email);

// ❌ INCORRECTO: Usar BusinessException directamente
throw new BusinessException(ErrorCode.EMP_NOT_FOUND, "...");

// ❌ INCORRECTO: Usar excepciones genéricas
throw new IllegalArgumentException("Empleado no encontrado");
throw new RuntimeException("Error al procesar");
```

### Manejo en Servicios

```java
@Service
@RequiredArgsConstructor
public class EmpleadoService {
    
    public Empleado obtenerEmpleado(Long id) {
        return empleadoRepository.findById(id)
            .orElseThrow(() -> EmpleadoException.notFound(id));
    }
    
    public void registrarEmpleado(EmpleadoRequest request) {
        if (empleadoRepository.existsByNip(request.getNip())) {
            throw EmpleadoException.duplicateNip(request.getNip());
        }
        // ...
    }
}
```

### Manejo de Errores de Base de Datos

```java
try {
    repository.save(entity);
} catch (DataIntegrityViolationException ex) {
    String message = ex.getMostSpecificCause().getMessage();
    if (message.contains("foreign key")) {
        throw UnidadException.zonaNotFound(zonaId);
    }
    if (message.contains("Duplicate entry")) {
        throw EmpleadoException.duplicateNip(nip);
    }
    throw ex;
}
```

## 🌍 GlobalExceptionHandler

El `GlobalExceptionHandler` maneja todas las excepciones y devuelve respuestas estandarizadas siguiendo RFC 7807 (Problem Details).

### Respuesta de Error Estándar

```json
{
  "type": "about:blank",
  "title": "Empleado no encontrado",
  "status": 404,
  "detail": "No existe empleado con ID: 12345",
  "errorCode": "EMP-001",
  "timestamp": "2024-01-15T10:30:00Z",
  "path": "/api/empleados/12345"
}
```

### Manejadores Implementados

- `IntegraException` - Excepciones base de dominio
- `MethodArgumentNotValidException` - Validación de request bodies
- `ConstraintViolationException` - Violaciones de restricciones
- `MethodArgumentTypeMismatchException` - Errores de tipo
- `BadCredentialsException` - Credenciales inválidas
- `AuthenticationException` - Errores de autenticación
- `AccessDeniedException` - Acceso denegado
- `DataIntegrityViolationException` - Violaciones de integridad
- `NoResourceFoundException` - Recursos no encontrados
- `Exception` - Manejador de último recurso

## 📝 Convenciones de Nomenclatura

### Factory Methods
- Buscar/Consultar: `notFound()`, `userNotFound()`
- Duplicados: `duplicateXxx()`, `duplicateNip()`, `duplicateEmail()`
- Estado: `notActive()`, `alreadyAssigned()`
- Validación: `invalidXxx()`, `invalidNip()`, `invalidCode()`
- Relaciones: `hasXxx()`, `hasDepartments()`, `hasEmployees()`

### Mensajes de Error
- Ser descriptivos pero concisos
- Incluir el identificador cuando aplique
- No exponer información sensible en errores técnicos

## 🔄 Migración desde Excepciones Genéricas

### Antes
```java
// ❌ RuntimeException en capa de negocio
throw new RuntimeException("Error al guardar empleado");

// ❌ IllegalArgumentException para validaciones
throw new IllegalArgumentException("Fecha inválida");

// ❌ IllegalStateException para estado
throw new IllegalStateException("Ya existe una pausa activa");
```

### Después
```java
// ✅ Excepción de dominio específica
throw TechnicalException.dbError("Error al guardar empleado", ex);

// ✅ Excepción de dominio con contexto
throw AsistenciaDomainException.invalidDateRange(fechaInicio, fechaFin);

// ✅ Excepción de negocio con factory method
throw AsistenciaDomainException.pausaActivaExistente(empleadoId);
```

## 🏗️ Constructores Requeridos

Toda excepción de dominio debe extender `BusinessException` e implementar los siguientes constructores:

```java
public class DominioException extends BusinessException {
    
    // Constructor básico
    public DominioException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
    
    // Constructor con causa (para errores técnicos)
    public DominioException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
    
    // Constructor con campo y valor rechazado (para validaciones)
    public DominioException(ErrorCode errorCode, String message, String field, Object rejectedValue) {
        super(errorCode, message, field, rejectedValue);
    }
}
```

### Cuándo usar cada constructor

| Constructor | Uso |
|-------------|-----|
| `(ErrorCode, String)` | Errores generales de negocio |
| `(ErrorCode, String, Throwable)` | Errores con causa técnica (wrapping) |
| `(ErrorCode, String, String, Object)` | Validaciones con campo específico |

## 📋 Checklist para Nuevas Excepciones

Al crear una nueva excepción de dominio:

- [ ] Extender `BusinessException`
- [ ] Ubicar en paquete `exception` del dominio correspondiente
- [ ] Definir los 3 constructores que propaguen `ErrorCode`
- [ ] Crear factory methods estáticos para casos comunes
- [ ] Usar códigos de `ErrorCode` existentes o agregar nuevos
- [ ] Documentar con JavaDoc
- [ ] Incluir ejemplos de uso en la documentación
- [ ] Actualizar este documento

## 🔍 Ejemplos de Uso por Dominio

### Módulo de Acceso
```java
// Autenticación
if (!passwordEncoder.matches(password, user.getPassword())) {
    throw AccesoException.passwordMismatch();
}

// Autorización
if (!tienePermiso(usuario, permiso)) {
    throw AccesoException.accessDenied("No tiene permiso para eliminar usuarios");
}
```

### Módulo de Asistencia
```java
// Validación de registro
if (pausaRepository.existsPausaActiva(empleadoId)) {
    throw AsistenciaDomainException.pausaActivaExistente(empleadoId);
}

// Validación de horario
if (hora.isAfter(horarioPermitido)) {
    throw AsistenciaDomainException.outsideSchedule("08:00-18:00");
}
```

### Módulo de Empleados
```java
// Validación de existencia
Empleado empleado = empleadoRepository.findById(id)
    .orElseThrow(() -> EmpleadoException.notFound(id));

// Validación de duplicados
if (empleadoRepository.existsByNip(nip)) {
    throw EmpleadoException.duplicateNip(nip);
}
```

## 📚 Referencias

- [RFC 7807 - Problem Details for HTTP APIs](https://tools.ietf.org/html/rfc7807)
- [Spring Boot Error Handling](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.developing-web-applications.spring-mvc.error-handling)
- [Clean Architecture - Error Handling](https://blog.cleancoder.com/uncle-bob/2020/04/06/ALittleArchitecture.html)

---

**Última actualización:** Marzo 2026  
**Versión:** 1.0  
**Autor:** Integra Development Team
