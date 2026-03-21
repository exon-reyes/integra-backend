CREATE TABLE integra.empleado_tiempo_historial (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    empleado_tiempo_id BIGINT NOT NULL,
    tipo_evento VARCHAR(50) NOT NULL,
    fecha_evento DATETIME NOT NULL,
    usuario_id INT NOT NULL,
    comentario TEXT,
    CONSTRAINT fk_empleado_tiempo_historial_tiempo FOREIGN KEY (empleado_tiempo_id) REFERENCES integra.empleado_tiempo(id) ON DELETE CASCADE
);
