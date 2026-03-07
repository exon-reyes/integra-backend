# Project Coding Rules

## Tech Stack
- Java 25
- Spring Boot 4+
- Maven
- MariaDB
- Angular 21
- PrimeNG 21
- Caddy como proxy inverso

## Architecture
- Clean Architecture
- SOLID
- Controllers -> Services -> Repositories
- DTOs obligatorios
- No lógica de negocio en controllers

## Design Principles

### DRY (Don't Repeat Yourself)
- Evitar duplicación de lógica, validaciones y reglas de negocio.
- Extraer comportamiento común a métodos, servicios o componentes reutilizables.
- No abstraer prematuramente: solo cuando exista duplicación real.

### KISS (Keep It Simple)
- Preferir soluciones simples y explícitas sobre abstracciones complejas.
- Priorizar legibilidad sobre cleverness.
- Evitar patrones innecesarios si no aportan valor claro.

### General Guidelines
- Métodos pequeños y con una sola responsabilidad.
- Clases enfocadas en un solo propósito.
- La claridad es más importante que la optimización prematura.
- Código optmizado para rapido procesamiento
- Utiliza la estadarización de excepciones implementada en el proyecto(ref: llm/exception.md)
- Entidades de dominio como clases principales con logica de negocio

## Coding Style
- Records para DTOs.
- Constructor injection obligatorio.
- Prohibido field injection.
- Streams solo si mejoran legibilidad.
- No usar Optional como parámetro.

---

## Security
- Validar DTOs con Validation (@Valid) en el Controller.
- No exponer entidades directamente.

## Spring Data JPA Guidelines

### Repositories
- Extender JpaRepository o CrudRepository según necesidad.
- No incluir lógica de negocio en repositorios.
- Usar métodos derivados cuando sea posible.
- Usar @Query solo si el método derivado no es suficiente.
- Preferir JPQL antes que SQL nativo.

### Entities
- Entidades de base de datos con @Entity son una representación de mi tabla.
- Relaciones LAZY por defecto.
- No exponer entidades en controllers.
- En Entidades de base de datos agrega un costructor vacío
- Constructor adicional que reciba ID cuando aplique.
- Evitar setters públicos innecesarios.

### Transactions
- @Transactional solo en capa Service.
- Métodos de lectura con readOnly = true.

### Fetching
- Evitar N+1 usando:
    - fetch join
    - @EntityGraph

### DTOs
- Usar records para salida.
- Mapear en Service o Mapper dedicado.
- Proyecciones cuando aplique preferentemente.

## Angular Guidelines
- Evitar componentes monolíticos extensos, y no mezcles responsabilidades de vista con reglas de negocio.
- Dividir vistas grandes en componentes pequeños reutilizables.
- Preferir creación de componentes por widgets o secciones, que sean representadores.
- Cada componente debe tener una responsabilidad clara.
- Separar componentes de presentación y componentes de lógica cuando aplique.
- Usar servicios para acceso a datos.
- No consumir APIs directamente desde el template.
- Usar interfaces o types para modelos.
- Usar ChangeDetectionStrategy.OnPush cuando sea posible.
- Usar angular 21 para la llamada a APIS(CRUD)
- Optimizar el uso de memoria, latencia y procesamiento, así como rendimiento UI/UX con la actualización de interfaces.

