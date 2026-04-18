/*
 Navicat Premium Dump SQL

 Source Server         : integra
 Source Server Type    : MariaDB
 Source Server Version : 110803 (11.8.3-MariaDB)
 Source Host           : sci.ddns.me:3306
 Source Schema         : comialex

 Target Server Type    : MariaDB
 Target Server Version : 110803 (11.8.3-MariaDB)
 File Encoding         : 65001

 Date: 15/04/2026 22:15:38
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for account_registration_tokens
-- ----------------------------
DROP TABLE IF EXISTS `account_registration_tokens`;
CREATE TABLE `account_registration_tokens`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `empleado_id` int(11) NOT NULL,
  `token_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL,
  `expires_at` datetime NOT NULL,
  `created_at` datetime NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_empleado_token`(`empleado_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 81 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for asignaciones_temporales
-- ----------------------------
DROP TABLE IF EXISTS `asignaciones_temporales`;
CREATE TABLE `asignaciones_temporales`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `empleado_id` int(11) NOT NULL,
  `zona_id` int(11) NOT NULL,
  `asignado_por` int(11) NOT NULL,
  `fecha_inicio` date NOT NULL,
  `fecha_fin` date NOT NULL,
  `justificacion` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL,
  `activa` tinyint(1) NOT NULL DEFAULT 1,
  `creado` datetime NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_asignacion_temporal_empleado_activa`(`empleado_id` ASC, `activa` ASC) USING BTREE,
  INDEX `idx_asignacion_temporal_zona_vigencia`(`zona_id` ASC, `fecha_inicio` ASC, `fecha_fin` ASC) USING BTREE,
  INDEX `idx_asignacion_temporal_vigencia`(`fecha_inicio` ASC, `fecha_fin` ASC, `activa` ASC) USING BTREE,
  INDEX `fk_asignacion_temporal_asignado_por`(`asignado_por` ASC) USING BTREE,
  CONSTRAINT `fk_asignacion_temporal_asignado_por` FOREIGN KEY (`asignado_por`) REFERENCES `empleado` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_asignacion_temporal_empleado` FOREIGN KEY (`empleado_id`) REFERENCES `empleado` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_asignacion_temporal_zona` FOREIGN KEY (`zona_id`) REFERENCES `zona` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci COMMENT = 'Asignaciones temporales de zona para auditores' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for asistencia
-- ----------------------------
DROP TABLE IF EXISTS `asistencia`;
CREATE TABLE `asistencia`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `public_id` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_uca1400_ai_ci NULL DEFAULT NULL,
  `id_empleado` int(11) NOT NULL,
  `fecha` date NOT NULL,
  `inicio_jornada` datetime NULL DEFAULT NULL,
  `fin_jornada` datetime NULL DEFAULT NULL,
  `jornada_cerrada` tinyint(1) NULL DEFAULT 0,
  `path_foto_inicio` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_uca1400_ai_ci NULL DEFAULT NULL,
  `path_foto_fin` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_uca1400_ai_ci NULL DEFAULT NULL,
  `comentario` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_uca1400_ai_ci NULL DEFAULT NULL,
  `cerrado_automatico` tinyint(1) NULL DEFAULT 0,
  `creado` datetime NULL DEFAULT current_timestamp(),
  `actualizado` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `inconsistencia` tinyint(1) NULL DEFAULT 0,
  `tiempo_compensado` time NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uq_asistencia_public_id`(`public_id` ASC) USING BTREE,
  INDEX `idx_fecha_jornada`(`fecha` ASC) USING BTREE,
  INDEX `fk_empleado_asistencia`(`id_empleado` ASC) USING BTREE,
  INDEX `idx_inconistencia_kiosco`(`inconsistencia` ASC) USING BTREE,
  INDEX `idx_empleado_fecha`(`id_empleado` ASC, `fecha` ASC) USING BTREE,
  CONSTRAINT `fk_empleado_asistencia` FOREIGN KEY (`id_empleado`) REFERENCES `empleado` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 5593 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_uca1400_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for auditoria
-- ----------------------------
DROP TABLE IF EXISTS `auditoria`;
CREATE TABLE `auditoria`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `folio` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL,
  `unidad_id` int(10) NOT NULL,
  `auditor_id` int(10) NOT NULL,
  `tipo_auditoria_id` int(11) NOT NULL DEFAULT 0,
  `fecha_inicio` datetime NOT NULL,
  `fecha_fin` datetime NULL DEFAULT NULL,
  `duracion_minutos` int(11) NULL DEFAULT NULL,
  `estatus` enum('PROGRAMADA','EN_PROCESO','COMPLETADA','CANCELADA') CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL DEFAULT 'PROGRAMADA',
  `observaciones_generales` text CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  `observaciones_encontradas` int(11) NULL DEFAULT 0 COMMENT 'Counter denormalizado',
  `calificacion_general` decimal(3, 2) NULL DEFAULT NULL COMMENT 'Del 1 al 10',
  `requiere_seguimiento` tinyint(1) NOT NULL DEFAULT 0,
  `created_at` datetime NOT NULL DEFAULT current_timestamp(),
  `updated_at` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_folio_auditoria`(`folio` ASC) USING BTREE,
  INDEX `idx_auditoria_unidad_fecha`(`unidad_id` ASC, `fecha_inicio` ASC) USING BTREE,
  INDEX `idx_auditoria_auditor_fecha`(`auditor_id` ASC, `fecha_inicio` ASC) USING BTREE,
  INDEX `idx_auditoria_estatus_fecha`(`estatus` ASC, `fecha_inicio` ASC) USING BTREE,
  INDEX `idx_auditoria_tipo_fecha`(`tipo_auditoria_id` ASC, `fecha_inicio` ASC) USING BTREE,
  INDEX `idx_auditoria_seguimiento`(`requiere_seguimiento` ASC) USING BTREE,
  INDEX `idx_auditoria_observaciones`(`observaciones_encontradas` ASC) USING BTREE,
  INDEX `id`(`id` ASC) USING BTREE,
  CONSTRAINT `FK_auditoria_auditoria` FOREIGN KEY (`auditor_id`) REFERENCES `auditoria` (`id`) ON DELETE RESTRICT ON UPDATE NO ACTION,
  CONSTRAINT `FK_auditoria_tipo_auditoria` FOREIGN KEY (`tipo_auditoria_id`) REFERENCES `tipo_auditoria` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_auditoria_unidad` FOREIGN KEY (`unidad_id`) REFERENCES `unidad` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci COMMENT = 'Auditorías realizadas - particionada por año' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for categoria_observacion
-- ----------------------------
DROP TABLE IF EXISTS `categoria_observacion`;
CREATE TABLE `categoria_observacion`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci COMMENT = 'Categorías de tipos de observación' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for compensacion_salida_deposito
-- ----------------------------
DROP TABLE IF EXISTS `compensacion_salida_deposito`;
CREATE TABLE `compensacion_salida_deposito`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `asistencia_id` int(11) NOT NULL,
  `empleado_id` int(11) NOT NULL,
  `unidad_id` int(11) NOT NULL,
  `fecha` date NOT NULL,
  `hora_salida` time NOT NULL,
  `horas_trabajadas` time NOT NULL,
  `horas_faltantes` time NOT NULL,
  `tiempo_compensado` time NOT NULL,
  `creado` datetime NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_asistencia_unica`(`asistencia_id` ASC) USING BTREE,
  INDEX `idx_empleado_deposito`(`empleado_id` ASC) USING BTREE,
  INDEX `idx_unidad_deposito`(`unidad_id` ASC) USING BTREE,
  INDEX `idx_fecha_deposito`(`fecha` ASC) USING BTREE,
  INDEX `idx_empleado_unidad_fecha`(`empleado_id` ASC, `unidad_id` ASC, `fecha` ASC) USING BTREE,
  CONSTRAINT `fk_asistencia_compensacion` FOREIGN KEY (`asistencia_id`) REFERENCES `asistencia` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_empleado_deposito` FOREIGN KEY (`empleado_id`) REFERENCES `empleado` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_unidad_deposito` FOREIGN KEY (`unidad_id`) REFERENCES `unidad` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 92 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for configuracion_tareas
-- ----------------------------
DROP TABLE IF EXISTS `configuracion_tareas`;
CREATE TABLE `configuracion_tareas`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL COMMENT 'Nombre lógico de la tarea.',
  `cron_expression` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL COMMENT 'Expresión CRON de ejecución.',
  `activo` tinyint(1) NULL DEFAULT 1 COMMENT '1 = activo, 0 = inactivo.',
  `creado_en` timestamp NULL DEFAULT current_timestamp() COMMENT 'Fecha de creación.',
  `actualizado_en` timestamp NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP COMMENT 'Última actualización.',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci COMMENT = 'Configura tareas programadas dinámicas (scheduler).' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for cruce_empleado_kiosco
-- ----------------------------
DROP TABLE IF EXISTS `cruce_empleado_kiosco`;
CREATE TABLE `cruce_empleado_kiosco`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `asistencia_id` int(11) NOT NULL,
  `accion` enum('entrada','salida') CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  `path_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  `fecha` datetime NULL DEFAULT NULL,
  `empleado_id` int(11) NULL DEFAULT NULL,
  `unidad_registro_id` int(11) NULL DEFAULT NULL,
  `unidad_esperada_id` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_accion_fecha`(`accion` ASC, `fecha` ASC) USING BTREE,
  INDEX `idx_empleado_cruce`(`empleado_id` ASC) USING BTREE,
  INDEX `idx_fecha_cruce_asistencia`(`fecha` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for cuenta
-- ----------------------------
DROP TABLE IF EXISTS `cuenta`;
CREATE TABLE `cuenta`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `unidad_id` int(11) NOT NULL,
  `departamento_id` int(11) NOT NULL,
  `tipo_id` int(11) NOT NULL,
  `usuario` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL,
  `clave` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL,
  `creado` datetime NULL DEFAULT current_timestamp(),
  `nota` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  `actualizado` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_tipo_cuenta`(`tipo_id` ASC, `usuario` ASC) USING BTREE,
  INDEX `FK_cuenta_departamento`(`departamento_id` ASC) USING BTREE,
  INDEX `FK_cuenta_unidad`(`unidad_id` ASC) USING BTREE,
  CONSTRAINT `FK_cuenta_departamento` FOREIGN KEY (`departamento_id`) REFERENCES `departamento` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_cuenta_tipo_cuenta` FOREIGN KEY (`tipo_id`) REFERENCES `tipo_cuenta` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `FK_cuenta_unidad` FOREIGN KEY (`unidad_id`) REFERENCES `unidad` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 765 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for departamento
-- ----------------------------
DROP TABLE IF EXISTS `departamento`;
CREATE TABLE `departamento`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL,
  `responsable_id` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK_departamento_empleado`(`responsable_id` ASC) USING BTREE,
  CONSTRAINT `FK_departamento_empleado` FOREIGN KEY (`responsable_id`) REFERENCES `empleado` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dias_solicitud_descanso
-- ----------------------------
DROP TABLE IF EXISTS `dias_solicitud_descanso`;
CREATE TABLE `dias_solicitud_descanso`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `folio_id` bigint(20) NULL DEFAULT NULL,
  `fecha` date NULL DEFAULT NULL,
  `estatus_nivel1` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  `estatus_nivel2` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  `fecha_accion_nivel1` date NULL DEFAULT NULL,
  `fecha_accion_nivel2` date NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_folio_solicitud`(`folio_id` ASC) USING BTREE,
  CONSTRAINT `idx_folio_solicitud` FOREIGN KEY (`folio_id`) REFERENCES `solicitud_descanso` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for empleado
-- ----------------------------
DROP TABLE IF EXISTS `empleado`;
CREATE TABLE `empleado`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `codigo_empleado` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL,
  `pin` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  `nombre` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL,
  `apellido_paterno` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL,
  `apellido_materno` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  `telefono` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  `departamento_id` int(11) NULL DEFAULT NULL,
  `puesto_id` int(11) NULL DEFAULT NULL,
  `unidad_id` int(11) NULL DEFAULT NULL,
  `zona_principal_id` int(11) NULL DEFAULT NULL,
  `estatus` enum('A','B','R','S','V') CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL DEFAULT 'A',
  `fecha_alta` date NOT NULL,
  `fecha_baja` date NULL DEFAULT NULL,
  `sexo` enum('M','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  `nombre_completo` varchar(152) GENERATED ALWAYS AS (concat(`nombre`,' ',`apellido_paterno`,ifnull(concat(' ',`apellido_materno`),''))) PERSISTENT,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP,
  `fecha_reingreso` date NULL DEFAULT NULL,
  `jefe_id` int(11) NULL DEFAULT NULL,
  `path_avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  `segundo_jefe_id` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK_empleado_unidad`(`unidad_id` ASC) USING BTREE,
  INDEX `FK_empleado_departamento`(`departamento_id` ASC) USING BTREE,
  INDEX `FK_empleado_puesto`(`puesto_id` ASC) USING BTREE,
  INDEX `FK_empleado_zona`(`zona_principal_id` ASC) USING BTREE,
  INDEX `idx_empleado_codigo`(`codigo_empleado` ASC) USING BTREE,
  INDEX `idx_empleado_estatus`(`estatus` ASC) USING BTREE,
  INDEX `idx_jefe_empleado`(`jefe_id` ASC) USING BTREE,
  INDEX `fk_segundo_jefe`(`segundo_jefe_id` ASC) USING BTREE,
  CONSTRAINT `FK_empleado_departamento` FOREIGN KEY (`departamento_id`) REFERENCES `departamento` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_empleado_puesto` FOREIGN KEY (`puesto_id`) REFERENCES `puesto` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_empleado_unidad` FOREIGN KEY (`unidad_id`) REFERENCES `unidad` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_empleado_zona` FOREIGN KEY (`zona_principal_id`) REFERENCES `zona` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_jefe_empleado` FOREIGN KEY (`jefe_id`) REFERENCES `empleado` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_segundo_jefe` FOREIGN KEY (`segundo_jefe_id`) REFERENCES `empleado` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 8407 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for escalas_vacaciones
-- ----------------------------
DROP TABLE IF EXISTS `escalas_vacaciones`;
CREATE TABLE `escalas_vacaciones`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Identificador único de la escala de vacaciones',
  `politica_id` bigint(20) NOT NULL COMMENT 'Referencia a la política de vacaciones a la que pertenece la escala',
  `anio_antiguedad` int(11) NOT NULL COMMENT 'Número de años de antigüedad del empleado',
  `dias_vacaciones` int(11) NOT NULL COMMENT 'Cantidad de días de vacaciones asignados para ese nivel de antigüedad',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_politica_anio`(`politica_id` ASC, `anio_antiguedad` ASC) USING BTREE,
  INDEX `idx_politica`(`politica_id` ASC) USING BTREE,
  CONSTRAINT `fk_escala_politica` FOREIGN KEY (`politica_id`) REFERENCES `politicas_vacaciones_escalas` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 71 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci COMMENT = 'Tabla que define los días de vacaciones según la antigüedad del empleado' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for estado
-- ----------------------------
DROP TABLE IF EXISTS `estado`;
CREATE TABLE `estado`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `codigo` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL,
  `nombre` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `IDX_codigo_estado`(`codigo` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 33 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for estatus
-- ----------------------------
DROP TABLE IF EXISTS `estatus`;
CREATE TABLE `estatus`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL,
  `descripcion` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  `color_bg` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  `nombre_icono` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  `es_inicial` tinyint(1) NULL DEFAULT 1,
  `es_final` tinyint(1) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci COMMENT = 'Estatus posibles de las observaciones' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for festivos_oficiales
-- ----------------------------
DROP TABLE IF EXISTS `festivos_oficiales`;
CREATE TABLE `festivos_oficiales`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Identificador único del día festivo',
  `fecha` date NOT NULL COMMENT 'Fecha del día festivo',
  `nombre` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL COMMENT 'Nombre del día festivo',
  `descripcion` text CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL COMMENT 'Descripción adicional del festivo',
  `tipo` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT 'OFICIAL' COMMENT 'Tipo de festivo (OFICIAL, EMPRESA, REGIONAL)',
  `activo` tinyint(1) NULL DEFAULT 1 COMMENT 'Indica si el festivo está activo',
  `created_at` timestamp NULL DEFAULT current_timestamp() COMMENT 'Fecha de creación del registro',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `fecha`(`fecha` ASC) USING BTREE,
  INDEX `idx_fecha`(`fecha` ASC) USING BTREE,
  INDEX `idx_activo`(`activo` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 39 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci COMMENT = 'Catálogo de días festivos oficiales o definidos por la empresa' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for historial_jornada
-- ----------------------------
DROP TABLE IF EXISTS `historial_jornada`;
CREATE TABLE `historial_jornada`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_unidad` int(11) NOT NULL,
  `id_jornada` int(11) NOT NULL,
  `tipo` enum('ENTRADA','PAUSA','COMIDA','SALIDA','REGRESO_PAUSA','REGRESO_COMIDA') CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL,
  `timestamp` datetime NOT NULL,
  `foto` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_jornada_registro`(`id_jornada` ASC) USING BTREE,
  INDEX `fk_unidad_registro_jornada`(`id_unidad` ASC) USING BTREE,
  CONSTRAINT `fk_unidad_registro_jornada` FOREIGN KEY (`id_unidad`) REFERENCES `unidad` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `historial_jornada_ibfk_1` FOREIGN KEY (`id_jornada`) REFERENCES `jornada` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for historial_solicitud_descanso
-- ----------------------------
DROP TABLE IF EXISTS `historial_solicitud_descanso`;
CREATE TABLE `historial_solicitud_descanso`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `solicitud_id` bigint(20) NULL DEFAULT NULL,
  `fecha` datetime NULL DEFAULT current_timestamp(),
  `descripcion` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_solicitud_id`(`solicitud_id` ASC) USING BTREE,
  CONSTRAINT `fk_solicitud_id` FOREIGN KEY (`solicitud_id`) REFERENCES `solicitud_descanso` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for horario_operativo
-- ----------------------------
DROP TABLE IF EXISTS `horario_operativo`;
CREATE TABLE `horario_operativo`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `unidad_id` int(11) NOT NULL,
  `operatividad_id` int(11) NOT NULL,
  `apertura` time NOT NULL,
  `cierre` time NOT NULL,
  `activo` tinyint(1) NULL DEFAULT 1,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK_horario_operativo_unidad`(`unidad_id` ASC) USING BTREE,
  INDEX `FK_horario_operativo_operatividad`(`operatividad_id` ASC) USING BTREE,
  CONSTRAINT `FK_horario_operativo_operatividad` FOREIGN KEY (`operatividad_id`) REFERENCES `operatividad` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_horario_operativo_unidad` FOREIGN KEY (`unidad_id`) REFERENCES `unidad` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for impresora_documentos
-- ----------------------------
DROP TABLE IF EXISTS `impresora_documentos`;
CREATE TABLE `impresora_documentos`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `modelo` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL,
  `tipo` enum('INYECCION','LASER') CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  `modelo_toner` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for incidencia_asistencia
-- ----------------------------
DROP TABLE IF EXISTS `incidencia_asistencia`;
CREATE TABLE `incidencia_asistencia`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `fecha` date NULL DEFAULT NULL,
  `total_ventas` decimal(10, 2) NULL DEFAULT NULL,
  `incidencia` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  `total_horas_extras` int(11) NULL DEFAULT NULL,
  `nota_horas_extras` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  `nota` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  `autorizada` tinyint(1) NULL DEFAULT NULL,
  `empleado_id` int(11) NULL DEFAULT NULL,
  `unidad_id` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_empleado_incidencia_asistencia`(`empleado_id` ASC) USING BTREE,
  INDEX `fk_unidad_incidencia_empleado`(`unidad_id` ASC) USING BTREE,
  INDEX `idx_fecha_asistencia`(`fecha` ASC) USING BTREE,
  INDEX `idx_fecha_unidad_asistencia`(`fecha` ASC, `unidad_id` ASC) USING BTREE,
  CONSTRAINT `fk_empleado_incidencia_asistencia` FOREIGN KEY (`empleado_id`) REFERENCES `empleado` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_unidad_incidencia_empleado` FOREIGN KEY (`unidad_id`) REFERENCES `unidad` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for incidencia_kiosco
-- ----------------------------
DROP TABLE IF EXISTS `incidencia_kiosco`;
CREATE TABLE `incidencia_kiosco`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Identificador único de la incidencia',
  `entidad_id` int(11) NOT NULL COMMENT 'ID de la entidad asociada (asistencia.id o pausa.id)',
  `tipo_incidencia` enum('unidad_incorrecta','fuera_horario') CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL COMMENT 'Tipo de inconsistencia detectada',
  `mensaje` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL COMMENT 'Descripción legible para humanos sobre la incidencia',
  `id_esperado` int(11) NULL DEFAULT NULL COMMENT 'Valor correcto esperado según la validación',
  `id_registrado` int(11) NULL DEFAULT NULL COMMENT 'Valor registrado que generó la inconsistencia',
  `empleado_id` int(11) NULL DEFAULT NULL COMMENT 'ID del empleado involucrado en la incidencia',
  `fecha` datetime NULL DEFAULT current_timestamp() COMMENT 'Fecha y hora de registro de la incidencia',
  `path_imagen` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL COMMENT 'Ruta de la imagen asociada como evidencia (opcional)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_entidad`(`entidad_id` ASC) USING BTREE COMMENT 'Índice compuesto para búsquedas rápidas por tipo de entidad e ID',
  INDEX `idx_tipo`(`tipo_incidencia` ASC) USING BTREE COMMENT 'Índice para filtrar por tipo de inconsistencia',
  INDEX `idx_empleado`(`empleado_id` ASC) USING BTREE COMMENT 'Índice para filtrar/buscar incidencias por empleado'
) ENGINE = InnoDB AUTO_INCREMENT = 1548 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci COMMENT = 'Tabla que almacena todas las incidencias detectadas en registros de asistencia y pausas, permitiendo la auditoría y análisis de inconsistencias.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for jornada
-- ----------------------------
DROP TABLE IF EXISTS `jornada`;
CREATE TABLE `jornada`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_empleado` int(11) NOT NULL,
  `fecha_inicio` datetime NOT NULL,
  `fecha_fin` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_empleado_jornada`(`id_empleado` ASC) USING BTREE,
  CONSTRAINT `fk_empleado_jornada` FOREIGN KEY (`id_empleado`) REFERENCES `empleado` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for observacion
-- ----------------------------
DROP TABLE IF EXISTS `observacion`;
CREATE TABLE `observacion`  (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `folio_observacion` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL,
  `auditoria_id` int(11) NULL DEFAULT NULL COMMENT 'Auditoría origen (opcional)',
  `unidad_id` int(11) NOT NULL,
  `tipo_observacion_id` int(11) NOT NULL,
  `estatus_id` int(11) NOT NULL DEFAULT 0,
  `creado_por_id` int(10) NOT NULL,
  `departamento_responsable_id` int(11) NOT NULL,
  `departamento_origen_id` int(11) NULL DEFAULT NULL COMMENT 'Departamento que levanta la observación',
  `titulo` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL,
  `descripcion` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL,
  `prioridad` enum('BAJA','MEDIA','ALTA','CRITICA') CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL DEFAULT 'MEDIA',
  `es_privada` tinyint(1) NOT NULL DEFAULT 0,
  `requiere_seguimiento` tinyint(1) NOT NULL DEFAULT 1,
  `fecha_resolucion` datetime NULL DEFAULT NULL,
  `tiempo_resolucion_horas` int(10) UNSIGNED NULL DEFAULT NULL,
  `total_comentarios` int(5) UNSIGNED NULL DEFAULT 0,
  `total_archivos` int(5) UNSIGNED NULL DEFAULT 0,
  `total_subtareas` int(5) UNSIGNED NULL DEFAULT 0,
  `subtareas_completadas` int(5) UNSIGNED NULL DEFAULT 0,
  `creado` datetime NOT NULL DEFAULT current_timestamp(),
  `modificado` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_observacion_folio`(`folio_observacion` ASC) USING BTREE,
  INDEX `idx_observacion_unidad_estado`(`unidad_id` ASC, `estatus_id` ASC) USING BTREE,
  INDEX `idx_observacion_responsable_estado`(`departamento_responsable_id` ASC, `estatus_id` ASC) USING BTREE,
  INDEX `idx_observacion_auditoria`(`auditoria_id` ASC) USING BTREE,
  INDEX `idx_observacion_prioridad_estado`(`prioridad` ASC, `estatus_id` ASC) USING BTREE,
  INDEX `idx_observacion_privada`(`es_privada` ASC) USING BTREE,
  INDEX `idx_observacion_seguimiento`(`requiere_seguimiento` ASC, `estatus_id` ASC) USING BTREE,
  INDEX `idx_observacion_dashboard_unidad`(`unidad_id` ASC, `estatus_id` ASC, `prioridad` ASC) USING BTREE,
  INDEX `idx_observacion_dashboard_depto`(`departamento_responsable_id` ASC, `estatus_id` ASC, `prioridad` ASC) USING BTREE,
  INDEX `FK_observaciones_tipo_observacion`(`tipo_observacion_id` ASC) USING BTREE,
  INDEX `FK_observaciones_estatus`(`estatus_id` ASC) USING BTREE,
  INDEX `FK_observaciones_empleado`(`creado_por_id` ASC) USING BTREE,
  INDEX `FK_observaciones_departamento_origen`(`departamento_origen_id` ASC) USING BTREE,
  INDEX `idx_observacion_estatus_lookup`(`id` ASC, `estatus_id` ASC) USING BTREE,
  CONSTRAINT `FK_observaciones_auditoria` FOREIGN KEY (`auditoria_id`) REFERENCES `auditoria` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_observaciones_departamento_origen` FOREIGN KEY (`departamento_origen_id`) REFERENCES `departamento` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_observaciones_departamento_responsable` FOREIGN KEY (`departamento_responsable_id`) REFERENCES `departamento` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_observaciones_empleado` FOREIGN KEY (`creado_por_id`) REFERENCES `empleado` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_observaciones_estatus` FOREIGN KEY (`estatus_id`) REFERENCES `estatus` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_observaciones_tipo_observacion` FOREIGN KEY (`tipo_observacion_id`) REFERENCES `tipo_observacion` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_observaciones_unidad` FOREIGN KEY (`unidad_id`) REFERENCES `unidad` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for observacion_archivos
-- ----------------------------
DROP TABLE IF EXISTS `observacion_archivos`;
CREATE TABLE `observacion_archivos`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `observacion_id` int(11) NULL DEFAULT NULL,
  `subtarea_id` int(11) NULL DEFAULT NULL,
  `comentario_id` int(11) NULL DEFAULT NULL,
  `subido_por` int(11) NULL DEFAULT NULL,
  `nombre_archivo` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  `ruta_archivo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  `tipo_mime` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  `es_imagen` tinyint(1) NULL DEFAULT NULL,
  `es_documento` tinyint(1) NULL DEFAULT NULL,
  `checksum_md5` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL COMMENT 'Para integridad',
  `creado` datetime NULL DEFAULT NULL,
  `url_externos` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK_observacion_archivos_observacion`(`observacion_id` ASC) USING BTREE,
  INDEX `FK_observacion_archivos_observacion_subtarea`(`subtarea_id` ASC) USING BTREE,
  INDEX `FK_observacion_archivos_observacion_comentario`(`comentario_id` ASC) USING BTREE,
  INDEX `FK_observacion_archivos_empleado`(`subido_por` ASC) USING BTREE,
  CONSTRAINT `FK_observacion_archivos_empleado` FOREIGN KEY (`subido_por`) REFERENCES `empleado` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_observacion_archivos_observacion` FOREIGN KEY (`observacion_id`) REFERENCES `observacion` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_observacion_archivos_observacion_comentario` FOREIGN KEY (`comentario_id`) REFERENCES `observacion_comentario` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_observacion_archivos_observacion_subtarea` FOREIGN KEY (`subtarea_id`) REFERENCES `observacion_subtarea` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci COMMENT = 'Archivos adjuntos a observaciones, subtareas y comentarios' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for observacion_colaboraciones
-- ----------------------------
DROP TABLE IF EXISTS `observacion_colaboraciones`;
CREATE TABLE `observacion_colaboraciones`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `observacion_id` int(11) NOT NULL DEFAULT 0,
  `departamento_id` int(11) NOT NULL,
  `asignado_por_id` int(11) NOT NULL,
  `fecha_asignacion` timestamp NULL DEFAULT current_timestamp(),
  `activa` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_obs_colaboracion`(`observacion_id` ASC, `departamento_id` ASC) USING BTREE,
  INDEX `idx_colaboracion_departamento_activa`(`departamento_id` ASC, `activa` ASC) USING BTREE,
  INDEX `idx_colaboracion_observacion`(`observacion_id` ASC) USING BTREE,
  INDEX `fk_colaboracion_asignado_por`(`asignado_por_id` ASC) USING BTREE,
  CONSTRAINT `FK_observacion_colaboraciones_observacion` FOREIGN KEY (`observacion_id`) REFERENCES `observacion` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_colaboracion_asignado_por` FOREIGN KEY (`asignado_por_id`) REFERENCES `empleado` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_colaboracion_departamento` FOREIGN KEY (`departamento_id`) REFERENCES `departamento` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci COMMENT = 'Departamentos que colaboran en observaciones' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for observacion_comentario
-- ----------------------------
DROP TABLE IF EXISTS `observacion_comentario`;
CREATE TABLE `observacion_comentario`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `observacion_id` int(11) NOT NULL,
  `subtarea_id` int(11) NULL DEFAULT NULL,
  `comentario` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_uca1400_ai_ci NULL DEFAULT NULL,
  `es_interno` tinyint(1) NULL DEFAULT 0,
  `autor_id` int(11) NOT NULL,
  `creado` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK_observacion_comentario_observacion`(`observacion_id` ASC) USING BTREE,
  INDEX `FK_observacion_comentario_observacion_subtarea`(`subtarea_id` ASC) USING BTREE,
  INDEX `FK_observacion_comentario_empleado`(`autor_id` ASC) USING BTREE,
  CONSTRAINT `FK_observacion_comentario_empleado` FOREIGN KEY (`autor_id`) REFERENCES `empleado` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_observacion_comentario_observacion` FOREIGN KEY (`observacion_id`) REFERENCES `observacion` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_observacion_comentario_observacion_subtarea` FOREIGN KEY (`subtarea_id`) REFERENCES `observacion_subtarea` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_uca1400_ai_ci COMMENT = 'Comentarios en observaciones y subtareas' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for observacion_historial
-- ----------------------------
DROP TABLE IF EXISTS `observacion_historial`;
CREATE TABLE `observacion_historial`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `observacion_id` int(11) NOT NULL DEFAULT 0,
  `accion` enum('CREADA','MODIFICADA','ESTADO_CAMBIADO','COMENTARIO_AGREGADO','ARCHIVO_ADJUNTADO','SUBTAREA_CREADA','SUBTAREA_COMPLETADA','REASIGNADA','CERRADA') CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL,
  `detalle_cambio` text CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  `creado` datetime NOT NULL DEFAULT current_timestamp(),
  `usuario` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_historial_observacion_fecha`(`observacion_id` ASC, `creado` ASC) USING BTREE,
  INDEX `idx_historial_accion_fecha`(`accion` ASC, `creado` ASC) USING BTREE,
  INDEX `idx_observacion_historial`(`observacion_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci COMMENT = 'Historial completo de cambios en observaciones' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for observacion_subtarea
-- ----------------------------
DROP TABLE IF EXISTS `observacion_subtarea`;
CREATE TABLE `observacion_subtarea`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `observacion_id` int(11) NULL DEFAULT NULL,
  `creado_por` int(11) NULL DEFAULT NULL,
  `titulo` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  `departamento_responsable` int(11) NULL DEFAULT NULL,
  `creado` datetime NULL DEFAULT NULL,
  `estatus_id` int(11) NULL DEFAULT NULL,
  `prioridad` enum('BAJA','MEDIA','ALTA','CRITICA') CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  `fecha_completada` datetime NULL DEFAULT NULL,
  `orden` tinyint(2) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK_observacion_subtarea_observacion`(`observacion_id` ASC) USING BTREE,
  INDEX `FK_observacion_subtarea_departamento`(`departamento_responsable` ASC) USING BTREE,
  INDEX `FK_observacion_subtarea_empleado`(`creado_por` ASC) USING BTREE,
  INDEX `FK_observacion_subtarea_estatus`(`estatus_id` ASC) USING BTREE,
  CONSTRAINT `FK_observacion_subtarea_departamento` FOREIGN KEY (`departamento_responsable`) REFERENCES `departamento` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_observacion_subtarea_empleado` FOREIGN KEY (`creado_por`) REFERENCES `empleado` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_observacion_subtarea_estatus` FOREIGN KEY (`estatus_id`) REFERENCES `estatus` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_observacion_subtarea_observacion` FOREIGN KEY (`observacion_id`) REFERENCES `observacion` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci COMMENT = 'Subtareas de las observaciones' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for observaciones_acceso_privado
-- ----------------------------
DROP TABLE IF EXISTS `observaciones_acceso_privado`;
CREATE TABLE `observaciones_acceso_privado`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `observacion_id` int(11) NULL DEFAULT NULL,
  `empleado_id` int(11) NULL DEFAULT NULL,
  `departamento_id` int(11) NULL DEFAULT NULL,
  `puesto_id` int(11) NULL DEFAULT NULL,
  `otorgado_por_id` int(11) NULL DEFAULT NULL,
  `fecha_otorgamiento` datetime NULL DEFAULT current_timestamp(),
  `fecha_expiracion` datetime NULL DEFAULT NULL,
  `activo` tinyint(1) NULL DEFAULT 1,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_acceso_privado_empleado`(`empleado_id` ASC, `activo` ASC) USING BTREE,
  INDEX `idx_acceso_privado_observacion_id`(`observacion_id` ASC) USING BTREE,
  INDEX `idx_acceso_privado_puesto`(`puesto_id` ASC, `activo` ASC) USING BTREE,
  INDEX `FK_observaciones_acceso_privado_departamento`(`departamento_id` ASC) USING BTREE,
  INDEX `FK_observaciones_acceso_privado_empleado_otorga`(`otorgado_por_id` ASC) USING BTREE,
  CONSTRAINT `FK_observaciones_acceso_privado_departamento` FOREIGN KEY (`departamento_id`) REFERENCES `departamento` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_observaciones_acceso_privado_empleado` FOREIGN KEY (`empleado_id`) REFERENCES `empleado` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_observaciones_acceso_privado_empleado_otorga` FOREIGN KEY (`otorgado_por_id`) REFERENCES `empleado` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_observaciones_acceso_privado_observacion` FOREIGN KEY (`observacion_id`) REFERENCES `observacion` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_observaciones_acceso_privado_puesto` FOREIGN KEY (`puesto_id`) REFERENCES `puesto` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for operatividad
-- ----------------------------
DROP TABLE IF EXISTS `operatividad`;
CREATE TABLE `operatividad`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for parametros_app
-- ----------------------------
DROP TABLE IF EXISTS `parametros_app`;
CREATE TABLE `parametros_app`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL,
  `valor` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_parametro_app`(`nombre` ASC, `valor` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for password_reset_tokens
-- ----------------------------
DROP TABLE IF EXISTS `password_reset_tokens`;
CREATE TABLE `password_reset_tokens`  (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(11) NOT NULL,
  `token_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL,
  `expires_at` datetime NOT NULL,
  `used` tinyint(1) NULL DEFAULT 0,
  `created_at` datetime NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_token`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 132 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for pausa
-- ----------------------------
DROP TABLE IF EXISTS `pausa`;
CREATE TABLE `pausa`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_asistencia` int(11) NOT NULL,
  `tipo` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_uca1400_ai_ci NOT NULL,
  `inicio` datetime NOT NULL,
  `fin` datetime NULL DEFAULT NULL,
  `path_foto_inicio` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_uca1400_ai_ci NULL DEFAULT NULL,
  `path_foto_fin` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_uca1400_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_pausa_asistencia`(`id_asistencia` ASC) USING BTREE,
  CONSTRAINT `fk_pausa_asistencia` FOREIGN KEY (`id_asistencia`) REFERENCES `asistencia` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1130 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_uca1400_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for periodos_vacacionales
-- ----------------------------
DROP TABLE IF EXISTS `periodos_vacacionales`;
CREATE TABLE `periodos_vacacionales`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Identificador único del periodo vacacional del empleado',
  `empleado_id` int(11) NOT NULL COMMENT 'Identificador del empleado al que pertenece el periodo',
  `anio_laboral` int(11) NOT NULL COMMENT 'Año laboral del periodo de vacaciones',
  `fecha_inicio` date NOT NULL COMMENT 'Fecha en que inicia el periodo de acumulación de vacaciones',
  `fecha_fin` date NOT NULL COMMENT 'Fecha en que termina el periodo de acumulación de vacaciones',
  `dias_habilitados` int(11) NOT NULL COMMENT 'Total de días de vacaciones otorgados en el periodo',
  `dias_tomados` int(11) NULL DEFAULT 0 COMMENT 'Días de vacaciones que el empleado ya utilizó',
  `dias_restantes` int(11) NOT NULL COMMENT 'Días de vacaciones disponibles para el empleado',
  `fecha_caducidad` date NOT NULL COMMENT 'Fecha límite para usar los días de vacaciones antes de expirar',
  `estatus` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT 'VIGENTE' COMMENT 'Estado del periodo (VIGENTE, CADUCADO, CERRADO)',
  `periodo_numero` int(11) NULL DEFAULT NULL COMMENT 'Número de periodo dentro del historial del empleado',
  `anio_gestion` int(11) NULL DEFAULT NULL COMMENT 'Año administrativo en que se gestiona el periodo',
  `created_at` timestamp NULL DEFAULT current_timestamp() COMMENT 'Fecha de creación del registro',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_empleado_anio`(`empleado_id` ASC, `anio_laboral` ASC) USING BTREE,
  INDEX `idx_empleado`(`empleado_id` ASC) USING BTREE,
  INDEX `idx_estatus`(`estatus` ASC) USING BTREE,
  INDEX `idx_caducidad`(`fecha_caducidad` ASC) USING BTREE,
  INDEX `idx_periodo_vigente`(`empleado_id` ASC, `estatus` ASC, `fecha_caducidad` ASC) USING BTREE,
  INDEX `idx_periodo`(`estatus` ASC) USING BTREE,
  CONSTRAINT `fk_empleado_periodo` FOREIGN KEY (`empleado_id`) REFERENCES `empleado` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 310 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci COMMENT = 'Periodos vacacionales acumulados por cada empleado según su antigüedad' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for periodos_veda
-- ----------------------------
DROP TABLE IF EXISTS `periodos_veda`;
CREATE TABLE `periodos_veda`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Identificador del periodo de restricción de vacaciones',
  `nombre` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL COMMENT 'Nombre del periodo de veda',
  `fecha_inicio` date NOT NULL COMMENT 'Fecha de inicio del periodo en el que no se permiten vacaciones',
  `fecha_fin` date NOT NULL COMMENT 'Fecha de finalización del periodo de veda',
  `descripcion` text CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL COMMENT 'Descripción o motivo de la restricción',
  `activo` tinyint(1) NULL DEFAULT 1 COMMENT 'Indica si el periodo de veda está activo',
  `created_at` timestamp NULL DEFAULT current_timestamp() COMMENT 'Fecha de creación del registro',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_fechas`(`fecha_inicio` ASC, `fecha_fin` ASC) USING BTREE,
  INDEX `idx_activo`(`activo` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci COMMENT = 'Periodos en los que la empresa restringe la solicitud de vacaciones' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for politicas_vacaciones_escalas
-- ----------------------------
DROP TABLE IF EXISTS `politicas_vacaciones_escalas`;
CREATE TABLE `politicas_vacaciones_escalas`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Identificador de la política de vacaciones',
  `nombre` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL COMMENT 'Nombre de la política de vacaciones',
  `descripcion` text CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL COMMENT 'Descripción detallada de la política',
  `fecha_vigencia_inicio` date NOT NULL COMMENT 'Fecha en que inicia la vigencia de la política',
  `fecha_vigencia_fin` date NULL DEFAULT NULL COMMENT 'Fecha en que termina la vigencia de la política',
  `activa` tinyint(1) NULL DEFAULT 1 COMMENT 'Indica si la política está actualmente activa',
  `created_at` timestamp NULL DEFAULT current_timestamp() COMMENT 'Fecha de creación del registro',
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP COMMENT 'Fecha de última actualización',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `nombre`(`nombre` ASC) USING BTREE,
  INDEX `idx_vigencia`(`fecha_vigencia_inicio` ASC, `fecha_vigencia_fin` ASC) USING BTREE,
  INDEX `idx_activa`(`activa` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci COMMENT = 'Políticas de vacaciones configurables utilizadas para calcular días por antigüedad' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for puesto
-- ----------------------------
DROP TABLE IF EXISTS `puesto`;
CREATE TABLE `puesto`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL,
  `activo` tinyint(1) NULL DEFAULT 1,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `nombre`(`nombre` ASC) USING BTREE,
  INDEX `idx_puesto_id_nombre`(`id` ASC, `nombre` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 60 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for role_permissions
-- ----------------------------
DROP TABLE IF EXISTS `role_permissions`;
CREATE TABLE `role_permissions`  (
  `role_id` bigint(20) NOT NULL,
  `permission_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL,
  PRIMARY KEY (`role_id`, `permission_id`) USING BTREE,
  INDEX `idx_rp_perm`(`permission_id` ASC) USING BTREE,
  CONSTRAINT `fk_rp_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for roles
-- ----------------------------
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT 1,
  `version` int(11) NULL DEFAULT 1,
  `is_default` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_roles_activo`(`activo` ASC) USING BTREE,
  INDEX `idx_roles_id_name`(`id` ASC, `name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for security_node
-- ----------------------------
DROP TABLE IF EXISTS `security_node`;
CREATE TABLE `security_node`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL,
  `name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL,
  `type` enum('UNIVERSO','MODULO','PERMISO') CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL,
  `parent_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  `nivel` int(11) NOT NULL DEFAULT 0,
  `orden` int(11) NOT NULL DEFAULT 0,
  `activo` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_node_type_activo`(`type` ASC, `activo` ASC) USING BTREE,
  INDEX `idx_node_parent`(`parent_id` ASC) USING BTREE,
  INDEX `idx_node_hierarchy`(`nivel` ASC, `orden` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for solicitud_descanso
-- ----------------------------
DROP TABLE IF EXISTS `solicitud_descanso`;
CREATE TABLE `solicitud_descanso`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `empleado_id` int(11) NULL DEFAULT NULL,
  `folio_solicitud` bigint(20) NULL DEFAULT NULL,
  `tipo_solicitud` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  `fecha_creacion` date NULL DEFAULT NULL,
  `dias_solicitados` int(11) NULL DEFAULT NULL,
  `estatus` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  `periodo_id` bigint(20) NULL DEFAULT NULL,
  `estatus_nivel1` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  `estatus_nivel2` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  `fecha_accion_nivel1` date NULL DEFAULT NULL,
  `fecha_accion_nivel2` date NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_empleado_solicitud`(`empleado_id` ASC) USING BTREE,
  INDEX `fk_periodo_vacaciones`(`periodo_id` ASC) USING BTREE,
  CONSTRAINT `fk_periodo_vacaciones` FOREIGN KEY (`periodo_id`) REFERENCES `periodos_vacacionales` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `idx_empleado_solicitud` FOREIGN KEY (`empleado_id`) REFERENCES `empleado` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for stock_toner_unidad
-- ----------------------------
DROP TABLE IF EXISTS `stock_toner_unidad`;
CREATE TABLE `stock_toner_unidad`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `zona_id` int(11) NULL DEFAULT NULL,
  `unidad_resguardo_id` int(11) NULL DEFAULT NULL,
  `impresora_id` int(11) NULL DEFAULT NULL,
  `supervisor_id` int(11) NULL DEFAULT NULL,
  `stock` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_stock_zona`(`zona_id` ASC) USING BTREE,
  UNIQUE INDEX `idx_zona_impresora`(`zona_id` ASC, `impresora_id` ASC) USING BTREE,
  INDEX `FK_stock_toner_unidad_impresora_documentos`(`impresora_id` ASC) USING BTREE,
  INDEX `FK_stock_toner_unidad_empleado`(`supervisor_id` ASC) USING BTREE,
  INDEX `FK_stock_toner_unidad_unidad`(`unidad_resguardo_id` ASC) USING BTREE,
  CONSTRAINT `FK_stock_toner_unidad_empleado` FOREIGN KEY (`supervisor_id`) REFERENCES `empleado` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_stock_toner_unidad_impresora_documentos` FOREIGN KEY (`impresora_id`) REFERENCES `impresora_documentos` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_stock_toner_unidad_unidad` FOREIGN KEY (`unidad_resguardo_id`) REFERENCES `unidad` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_stock_toner_unidad_zona` FOREIGN KEY (`zona_id`) REFERENCES `zona` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tipo_auditoria
-- ----------------------------
DROP TABLE IF EXISTS `tipo_auditoria`;
CREATE TABLE `tipo_auditoria`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `codigo` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL,
  `nombre` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_tipo_auditoria`(`nombre` ASC) USING BTREE,
  UNIQUE INDEX `idx_codigo_auditoria`(`codigo` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tipo_cuenta
-- ----------------------------
DROP TABLE IF EXISTS `tipo_cuenta`;
CREATE TABLE `tipo_cuenta`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_tipo_cuenta`(`nombre` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 43 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tipo_observacion
-- ----------------------------
DROP TABLE IF EXISTS `tipo_observacion`;
CREATE TABLE `tipo_observacion`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL,
  `descripcion` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  `departamento_responsable_id` int(11) NULL DEFAULT NULL,
  `categoria_id` int(11) NULL DEFAULT NULL,
  `prioridad_default` enum('BAJA','MEDIA','ALTA','CRITICA') CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT 'BAJA',
  `sla_horas` smallint(5) UNSIGNED NULL DEFAULT 72,
  `activo` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_tipo_observacion_departamento`(`departamento_responsable_id` ASC) USING BTREE,
  INDEX `fk_tipo_observacion_categoria`(`categoria_id` ASC) USING BTREE,
  CONSTRAINT `fk_tipo_observacion_categoria` FOREIGN KEY (`categoria_id`) REFERENCES `categoria_observacion` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_tipo_observacion_departamento` FOREIGN KEY (`departamento_responsable_id`) REFERENCES `departamento` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 145 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci COMMENT = 'Tipos de observación disponibles' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tipo_proceso
-- ----------------------------
DROP TABLE IF EXISTS `tipo_proceso`;
CREATE TABLE `tipo_proceso`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL COMMENT 'Código técnico estable: VACACIONES, MARCAJES, AMBOS',
  `descripcion` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL COMMENT 'Descripción funcional del proceso',
  `activo` tinyint(1) NOT NULL DEFAULT 1,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `codigo`(`nombre` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci COMMENT = 'Catálogo de tipos de proceso' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for token_version
-- ----------------------------
DROP TABLE IF EXISTS `token_version`;
CREATE TABLE `token_version`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL,
  `version` int(11) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for unidad
-- ----------------------------
DROP TABLE IF EXISTS `unidad`;
CREATE TABLE `unidad`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `clave` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  `nombre` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL,
  `localizacion` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  `telefono` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  `activo` tinyint(1) NULL DEFAULT 1,
  `zona_id` int(11) NULL DEFAULT NULL,
  `supervisor_id` int(11) NULL DEFAULT NULL,
  `estado_id` int(11) NULL DEFAULT NULL,
  `nombre_completo` varchar(255) GENERATED ALWAYS AS (concat(`clave`,' ',`nombre`)) PERSISTENT,
  `direccion` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  `ultima_auditoria` datetime NULL DEFAULT NULL,
  `proxima_auditoria` datetime NULL DEFAULT NULL,
  `observaciones_pendientes` int(11) NULL DEFAULT 0,
  `creado` date NULL DEFAULT NULL,
  `actualizado` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `requiere_camara` tinyint(1) NULL DEFAULT 1,
  `codigo_autorizacion_kiosco` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  `requiere_codigo` tinyint(1) NULL DEFAULT NULL,
  `version_kiosco` int(11) NULL DEFAULT 1,
  `tiempo_compensacion` time NULL DEFAULT NULL,
  `tiempo_espera_kiosco` int(1) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_nombre_unidad`(`nombre` ASC) USING BTREE,
  UNIQUE INDEX `idx_telefono`(`telefono` ASC) USING BTREE,
  UNIQUE INDEX `idx_clave_unidad`(`clave` ASC) USING BTREE,
  UNIQUE INDEX `idx_codigo_autorizacion_kiosco`(`codigo_autorizacion_kiosco` ASC) USING BTREE,
  UNIQUE INDEX `idx_unidad_email`(`email` ASC) USING BTREE,
  INDEX `FK_unidad_zona`(`zona_id` ASC) USING BTREE,
  INDEX `FK_unidad_empleado`(`supervisor_id` ASC) USING BTREE,
  INDEX `FK_unidad_estado`(`estado_id` ASC) USING BTREE,
  INDEX `idx_unidad_observaciones_pendientes`(`observaciones_pendientes` ASC) USING BTREE,
  INDEX `idx_unidad_activo`(`activo` ASC) USING BTREE,
  CONSTRAINT `FK_unidad_empleado` FOREIGN KEY (`supervisor_id`) REFERENCES `empleado` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_unidad_estado` FOREIGN KEY (`estado_id`) REFERENCES `estado` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_unidad_zona` FOREIGN KEY (`zona_id`) REFERENCES `zona` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 126 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for unidad_impresora_documentos
-- ----------------------------
DROP TABLE IF EXISTS `unidad_impresora_documentos`;
CREATE TABLE `unidad_impresora_documentos`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `unidad_id` int(11) NOT NULL,
  `impresora_id` int(11) NOT NULL,
  `fecha_asignacion` date NULL DEFAULT NULL,
  `cambio_toner` datetime NULL DEFAULT NULL,
  `stock` int(4) NULL DEFAULT NULL,
  `nota` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unidad_id`(`unidad_id` ASC, `impresora_id` ASC) USING BTREE,
  INDEX `idx_impresora_id`(`impresora_id` ASC) USING BTREE,
  INDEX `idx_unidad_impresora`(`unidad_id` ASC) USING BTREE,
  CONSTRAINT `FK_unidad_impresora_documentos_impresora_documentos` FOREIGN KEY (`impresora_id`) REFERENCES `impresora_documentos` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_unidad_impresora_documentos_unidad` FOREIGN KEY (`unidad_id`) REFERENCES `unidad` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for unidad_wifi
-- ----------------------------
DROP TABLE IF EXISTS `unidad_wifi`;
CREATE TABLE `unidad_wifi`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `unidad_id` int(11) NOT NULL,
  `ssid` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  `clave` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  `nota` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK_unidad_wifi_unidad`(`unidad_id` ASC) USING BTREE,
  CONSTRAINT `FK_unidad_wifi_unidad` FOREIGN KEY (`unidad_id`) REFERENCES `unidad` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_permissions
-- ----------------------------
DROP TABLE IF EXISTS `user_permissions`;
CREATE TABLE `user_permissions`  (
  `user_id` bigint(20) NOT NULL,
  `permission_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL,
  PRIMARY KEY (`user_id`, `permission_id`) USING BTREE,
  INDEX `idx_up_perm`(`permission_id` ASC) USING BTREE,
  CONSTRAINT `fk_up_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_roles
-- ----------------------------
DROP TABLE IF EXISTS `user_roles`;
CREATE TABLE `user_roles`  (
  `user_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL,
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE,
  INDEX `idx_ur_role`(`role_id` ASC) USING BTREE,
  INDEX `idx_user_roles_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_ur_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_ur_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT 1,
  `created_at` datetime NULL DEFAULT current_timestamp(),
  `empleado_id` int(11) NULL DEFAULT NULL,
  `requier_cambio_password` tinyint(1) NULL DEFAULT NULL,
  `update_at` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_users_username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `idx_users_empleado`(`empleado_id` ASC) USING BTREE,
  INDEX `idx_users_username_activo`(`username` ASC, `activo` ASC) USING BTREE,
  INDEX `idx_users_email`(`email` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 541 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for zona
-- ----------------------------
DROP TABLE IF EXISTS `zona`;
CREATE TABLE `zona`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci NOT NULL,
  `activo` tinyint(1) NULL DEFAULT 1,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `IDX_nombre_zona`(`nombre` ASC) USING BTREE,
  INDEX `idx_zona_activa`(`activo` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 814 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Event structure for ev_refrescar_vm_observaciones_por_departamento
-- ----------------------------
DROP EVENT IF EXISTS `ev_refrescar_vm_observaciones_por_departamento`;
delimiter ;;
CREATE EVENT `ev_refrescar_vm_observaciones_por_departamento`
ON SCHEDULE
EVERY '1' HOUR STARTS '2025-09-18 21:05:29'
DO CALL sp_refrescar_vm_observaciones_por_departamento_incremental()
;;
delimiter ;

-- ----------------------------
-- Event structure for ev_refrescar_vm_observaciones_por_unidad
-- ----------------------------
DROP EVENT IF EXISTS `ev_refrescar_vm_observaciones_por_unidad`;
delimiter ;;
CREATE EVENT `ev_refrescar_vm_observaciones_por_unidad`
ON SCHEDULE
EVERY '1' HOUR STARTS '2025-09-18 21:00:29'
DO CALL sp_refrescar_vm_observaciones_por_unidad_incremental()
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table role_permissions
-- ----------------------------
DROP TRIGGER IF EXISTS `trg_role_permissions_val_ins`;
delimiter ;;
CREATE TRIGGER `trg_role_permissions_val_ins` BEFORE INSERT ON `role_permissions` FOR EACH ROW BEGIN
    IF (SELECT type FROM security_node WHERE id = NEW.permission_id) <> 'PERMISO' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Error: Solo se permiten nodos tipo PERMISO.';
    END IF;
END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table role_permissions
-- ----------------------------
DROP TRIGGER IF EXISTS `trg_role_permissions_val_upd`;
delimiter ;;
CREATE TRIGGER `trg_role_permissions_val_upd` BEFORE UPDATE ON `role_permissions` FOR EACH ROW BEGIN
    IF (SELECT type FROM security_node WHERE id = NEW.permission_id) <> 'PERMISO' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Error: Solo se permiten nodos tipo PERMISO en la actualización.';
    END IF;
END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table security_node
-- ----------------------------
DROP TRIGGER IF EXISTS `trg_security_node_before_insert`;
delimiter ;;
CREATE TRIGGER `trg_security_node_before_insert` BEFORE INSERT ON `security_node` FOR EACH ROW BEGIN
    DECLARE p_nivel INT DEFAULT -1;
    DECLARE p_type ENUM('UNIVERSO','MODULO','PERMISO');

    IF NEW.parent_id IS NOT NULL THEN
        SELECT nivel, type INTO p_nivel, p_type FROM security_node WHERE id = NEW.parent_id;
        SET NEW.nivel = p_nivel + 1;
        
        -- Impide que un permiso tenga hijos
        IF p_type = 'PERMISO' THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Un PERMISO no puede ser nodo padre.';
        END IF;
    ELSE
        SET NEW.nivel = 0;
    END IF;
END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table user_permissions
-- ----------------------------
DROP TRIGGER IF EXISTS `trg_user_permissions_val_ins`;
delimiter ;;
CREATE TRIGGER `trg_user_permissions_val_ins` BEFORE INSERT ON `user_permissions` FOR EACH ROW BEGIN
    IF (SELECT type FROM security_node WHERE id = NEW.permission_id) <> 'PERMISO' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Error: Solo se pueden asignar nodos de tipo PERMISO directamente al usuario.';
    END IF;
END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table user_permissions
-- ----------------------------
DROP TRIGGER IF EXISTS `trg_user_permissions_val_upd`;
delimiter ;;
CREATE TRIGGER `trg_user_permissions_val_upd` BEFORE UPDATE ON `user_permissions` FOR EACH ROW BEGIN
    IF (SELECT type FROM security_node WHERE id = NEW.permission_id) <> 'PERMISO' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Error: Solo se pueden actualizar a nodos de tipo PERMISO.';
    END IF;
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
