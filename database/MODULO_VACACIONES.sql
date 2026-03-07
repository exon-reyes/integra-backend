-- ============================================================================
-- INSTALACIÓN COMPLETA: MÓDULO DE GESTIÓN DE VACACIONES
-- ============================================================================
-- Ejecutar este script UNA SOLA VEZ en base de datos limpia
-- Incluye: Tablas + Datos Iniciales + Índices
-- ============================================================================

-- ============================================================================
-- 1. CREAR TABLAS
-- ============================================================================

CREATE TABLE IF NOT EXISTS politicas_vacaciones_escalas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion TEXT,
    fecha_vigencia_inicio DATE NOT NULL,
    fecha_vigencia_fin DATE,
    activa BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_vigencia (fecha_vigencia_inicio, fecha_vigencia_fin),
    INDEX idx_activa (activa)
);

CREATE TABLE IF NOT EXISTS escalas_vacaciones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    politica_id BIGINT NOT NULL,
    anio_antiguedad INT NOT NULL,
    dias_vacaciones INT NOT NULL,
    CONSTRAINT fk_escala_politica FOREIGN KEY (politica_id) 
        REFERENCES politicas_vacaciones_escalas(id) ON DELETE CASCADE,
    CONSTRAINT uk_politica_anio UNIQUE (politica_id, anio_antiguedad),
    INDEX idx_politica (politica_id)
);

CREATE TABLE IF NOT EXISTS periodos_vacacionales (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    empleado_id INT NOT NULL,
    anio_laboral INT NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    dias_habilitados INT NOT NULL,
    dias_tomados INT DEFAULT 0,
    dias_restantes INT NOT NULL,
    fecha_caducidad DATE NOT NULL,
    estatus VARCHAR(20) DEFAULT 'VIGENTE',
    periodo_numero INT,
    anio_gestion INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_empleado_anio UNIQUE (empleado_id, anio_laboral),
    INDEX idx_empleado (empleado_id),
    INDEX idx_estatus (estatus),
    INDEX idx_caducidad (fecha_caducidad),
    INDEX idx_periodo_vigente (empleado_id, estatus, fecha_caducidad)
);

CREATE TABLE IF NOT EXISTS solicitudes_vacaciones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    empleado_id INT NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    dias_solicitados INT NOT NULL,
    motivo TEXT,
    estatus VARCHAR(20) DEFAULT 'PENDIENTE',
    aprobador_id INT,
    fecha_aprobacion TIMESTAMP,
    comentarios_aprobador TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_empleado (empleado_id),
    INDEX idx_estatus (estatus),
    INDEX idx_fechas (fecha_inicio, fecha_fin),
    INDEX idx_solicitud_rango (empleado_id, fecha_inicio, fecha_fin, estatus)
);

CREATE TABLE IF NOT EXISTS vacaciones_auditoria (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    solicitud_id BIGINT NOT NULL,
    accion VARCHAR(50) NOT NULL,
    usuario_id INT NOT NULL,
    detalles TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_auditoria_solicitud FOREIGN KEY (solicitud_id) 
        REFERENCES solicitudes_vacaciones(id) ON DELETE CASCADE,
    INDEX idx_solicitud (solicitud_id),
    INDEX idx_fecha (created_at)
);

CREATE TABLE IF NOT EXISTS descansos_empleado (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    empleado_id INT NOT NULL,
    fecha_descanso DATE NOT NULL,
    motivo VARCHAR(255),
    activo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_empleado_fecha UNIQUE (empleado_id, fecha_descanso),
    INDEX idx_empleado (empleado_id),
    INDEX idx_fecha (fecha_descanso),
    INDEX idx_activo (activo)
);

CREATE TABLE IF NOT EXISTS festivos_oficiales (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha DATE NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    tipo VARCHAR(50) DEFAULT 'OFICIAL',
    activo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_fecha (fecha),
    INDEX idx_activo (activo)
);

CREATE TABLE IF NOT EXISTS periodos_veda (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    descripcion TEXT,
    activo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_fechas (fecha_inicio, fecha_fin),
    INDEX idx_activo (activo)
);

-- ============================================================================
-- 2. DATOS INICIALES: POLÍTICAS DE VACACIONES
-- ============================================================================

-- Política Pre-Reforma (1970-2022)
INSERT INTO politicas_vacaciones_escalas 
(nombre, descripcion, fecha_vigencia_inicio, fecha_vigencia_fin, activa) 
VALUES 
('LFT Pre-Reforma 2023', 
 'Ley Federal del Trabajo antes de la reforma de vacaciones dignas', 
 '1970-01-01', '2022-12-31', FALSE);

SET @politica_antigua = LAST_INSERT_ID();

INSERT INTO escalas_vacaciones (politica_id, anio_antiguedad, dias_vacaciones) VALUES
(@politica_antigua, 1, 6), (@politica_antigua, 2, 8), (@politica_antigua, 3, 10),
(@politica_antigua, 4, 12), (@politica_antigua, 5, 14),
-- A partir del año 6: bloques de 5 años con mismo valor
(@politica_antigua, 6, 16), (@politica_antigua, 7, 16), (@politica_antigua, 8, 16),
(@politica_antigua, 9, 16), (@politica_antigua, 10, 16),
(@politica_antigua, 11, 18), (@politica_antigua, 12, 18), (@politica_antigua, 13, 18),
(@politica_antigua, 14, 18), (@politica_antigua, 15, 18),
(@politica_antigua, 16, 20), (@politica_antigua, 17, 20), (@politica_antigua, 18, 20),
(@politica_antigua, 19, 20), (@politica_antigua, 20, 20),
(@politica_antigua, 21, 22), (@politica_antigua, 22, 22), (@politica_antigua, 23, 22),
(@politica_antigua, 24, 22), (@politica_antigua, 25, 22),
(@politica_antigua, 26, 24), (@politica_antigua, 27, 24), (@politica_antigua, 28, 24),
(@politica_antigua, 29, 24), (@politica_antigua, 30, 24),
(@politica_antigua, 31, 26), (@politica_antigua, 32, 26), (@politica_antigua, 33, 26),
(@politica_antigua, 34, 26), (@politica_antigua, 35, 26);

-- Política Reforma Vacaciones Dignas (2023+)
INSERT INTO politicas_vacaciones_escalas 
(nombre, descripcion, fecha_vigencia_inicio, fecha_vigencia_fin, activa) 
VALUES 
('Reforma Vacaciones Dignas 2023', 
 'Reforma que establece mínimo de 12 días desde el primer año (Art. 76 y 78 LFT)', 
 '2023-01-01', NULL, TRUE);

SET @politica_actual = LAST_INSERT_ID();

INSERT INTO escalas_vacaciones (politica_id, anio_antiguedad, dias_vacaciones) VALUES
(@politica_actual, 1, 12), (@politica_actual, 2, 14), (@politica_actual, 3, 16),
(@politica_actual, 4, 18), (@politica_actual, 5, 20),
-- A partir del año 6: bloques de 5 años con mismo valor
(@politica_actual, 6, 22), (@politica_actual, 7, 22), (@politica_actual, 8, 22),
(@politica_actual, 9, 22), (@politica_actual, 10, 22),
(@politica_actual, 11, 24), (@politica_actual, 12, 24), (@politica_actual, 13, 24),
(@politica_actual, 14, 24), (@politica_actual, 15, 24),
(@politica_actual, 16, 26), (@politica_actual, 17, 26), (@politica_actual, 18, 26),
(@politica_actual, 19, 26), (@politica_actual, 20, 26),
(@politica_actual, 21, 28), (@politica_actual, 22, 28), (@politica_actual, 23, 28),
(@politica_actual, 24, 28), (@politica_actual, 25, 28),
(@politica_actual, 26, 30), (@politica_actual, 27, 30), (@politica_actual, 28, 30),
(@politica_actual, 29, 30), (@politica_actual, 30, 30),
(@politica_actual, 31, 32), (@politica_actual, 32, 32), (@politica_actual, 33, 32),
(@politica_actual, 34, 32), (@politica_actual, 35, 32);

-- ============================================================================
-- 3. DATOS INICIALES: FESTIVOS OFICIALES 2025
-- ============================================================================

INSERT INTO festivos_oficiales (fecha, nombre, tipo, activo) VALUES
('2025-01-01', 'Año Nuevo', 'OFICIAL', TRUE),
('2025-02-03', 'Día de la Constitución', 'OFICIAL', TRUE),
('2025-03-17', 'Natalicio de Benito Juárez', 'OFICIAL', TRUE),
('2025-05-01', 'Día del Trabajo', 'OFICIAL', TRUE),
('2025-09-16', 'Día de la Independencia', 'OFICIAL', TRUE),
('2025-11-17', 'Revolución Mexicana', 'OFICIAL', TRUE),
('2025-12-25', 'Navidad', 'OFICIAL', TRUE);

-- ============================================================================
-- 4. VERIFICACIÓN DE INSTALACIÓN
-- ============================================================================

SELECT 'Políticas instaladas:' AS verificacion;
SELECT nombre, fecha_vigencia_inicio, fecha_vigencia_fin, activa 
FROM politicas_vacaciones_escalas;

SELECT 'Escalas Pre-Reforma (primeros 5 años):' AS verificacion;
SELECT e.anio_antiguedad, e.dias_vacaciones 
FROM escalas_vacaciones e
JOIN politicas_vacaciones_escalas p ON e.politica_id = p.id
WHERE p.nombre = 'LFT Pre-Reforma 2023' AND e.anio_antiguedad <= 5;

SELECT 'Escalas Reforma 2023 (primeros 5 años):' AS verificacion;
SELECT e.anio_antiguedad, e.dias_vacaciones 
FROM escalas_vacaciones e
JOIN politicas_vacaciones_escalas p ON e.politica_id = p.id
WHERE p.nombre = 'Reforma Vacaciones Dignas 2023' AND e.anio_antiguedad <= 5;

SELECT 'Festivos 2025:' AS verificacion;
SELECT fecha, nombre FROM festivos_oficiales WHERE YEAR(fecha) = 2025 ORDER BY fecha;

-- ============================================================================
-- RESUMEN DE INSTALACIÓN
-- ============================================================================
-- ✅ 8 tablas creadas
-- ✅ 2 políticas instaladas (Pre-Reforma + Reforma 2023)
-- ✅ 70 escalas de días (35 por política)
-- ✅ 7 festivos oficiales 2025
-- ✅ Sistema listo para generar períodos vacacionales
-- 
-- SIGUIENTE PASO:
-- Ejecutar sincronización inicial: POST /api/v1/vacaciones/admin/sincronizar-periodos
-- ============================================================================
