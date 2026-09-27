-- ============================================================
-- Proyecto: Sistema de Gestión para Taller Mecánico
-- Entrega 2: Esquema de Base de Datos (MySQL)
-- ============================================================

CREATE DATABASE IF NOT EXISTS taller_mecanico_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE taller_mecanico_db;

-- ------------------------------------------------------------
-- 1. Tabla Usuario (Administrador y Mecánico)
-- ------------------------------------------------------------
CREATE TABLE usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    nombre_completo VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    rol ENUM('ADMIN', 'MECANICO') NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ------------------------------------------------------------
-- 2. Tabla Cliente
-- ------------------------------------------------------------
CREATE TABLE cliente (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    dni_cuit VARCHAR(20) UNIQUE,
    telefono VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    direccion VARCHAR(200),
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ------------------------------------------------------------
-- 3. Tabla Vehiculo
-- ------------------------------------------------------------
CREATE TABLE vehiculo (
    id_vehiculo INT AUTO_INCREMENT PRIMARY KEY,
    patente VARCHAR(10) NOT NULL UNIQUE,
    marca VARCHAR(50) NOT NULL,
    modelo VARCHAR(50) NOT NULL,
    anio INT,
    kilometraje_actual INT,
    id_cliente INT NOT NULL,
    CONSTRAINT fk_vehiculo_cliente
        FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente)
        ON DELETE RESTRICT ON UPDATE CASCADE
);

-- ------------------------------------------------------------
-- 4. Tabla OrdenTrabajo (módulo central)
-- ------------------------------------------------------------
CREATE TABLE orden_trabajo (
    id_orden INT AUTO_INCREMENT PRIMARY KEY,
    id_vehiculo INT NOT NULL,
    id_mecanico INT,
    fecha_ingreso DATETIME NOT NULL,
    fecha_egreso DATETIME,
    kilometraje_ingreso INT NOT NULL,
    problema_informado TEXT NOT NULL,
    diagnostico TEXT,
    estado ENUM(
        'RECIBIDO',
        'EN_DIAGNOSTICO',
        'EN_REPARACION',
        'ESPERANDO_REPUESTO',
        'CONTROL_FINAL',
        'FINALIZADO',
        'ENTREGADO'
    ) NOT NULL DEFAULT 'RECIBIDO',
    observaciones TEXT,
    CONSTRAINT fk_orden_vehiculo
        FOREIGN KEY (id_vehiculo) REFERENCES vehiculo(id_vehiculo)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_orden_mecanico
        FOREIGN KEY (id_mecanico) REFERENCES usuario(id_usuario)
        ON DELETE SET NULL ON UPDATE CASCADE
);

-- ------------------------------------------------------------
-- 5. Tabla TrabajoRealizado
-- ------------------------------------------------------------
CREATE TABLE trabajo_realizado (
    id_trabajo INT AUTO_INCREMENT PRIMARY KEY,
    id_orden INT NOT NULL,
    descripcion TEXT NOT NULL,
    mano_obra_horas DECIMAL(5,2),
    repuestos_utilizados TEXT,
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_trabajo_orden
        FOREIGN KEY (id_orden) REFERENCES orden_trabajo(id_orden)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- ------------------------------------------------------------
-- 6. Tabla HistorialEstado (auditoría de cambios)
-- ------------------------------------------------------------
CREATE TABLE historial_estado (
    id_historial INT AUTO_INCREMENT PRIMARY KEY,
    id_orden INT NOT NULL,
    estado_anterior ENUM(
        'RECIBIDO',
        'EN_DIAGNOSTICO',
        'EN_REPARACION',
        'ESPERANDO_REPUESTO',
        'CONTROL_FINAL',
        'FINALIZADO',
        'ENTREGADO'
    ),
    estado_nuevo ENUM(
        'RECIBIDO',
        'EN_DIAGNOSTICO',
        'EN_REPARACION',
        'ESPERANDO_REPUESTO',
        'CONTROL_FINAL',
        'FINALIZADO',
        'ENTREGADO'
    ) NOT NULL,
    fecha_cambio DATETIME DEFAULT CURRENT_TIMESTAMP,
    id_usuario INT,
    observacion VARCHAR(255),
    CONSTRAINT fk_historial_orden
        FOREIGN KEY (id_orden) REFERENCES orden_trabajo(id_orden)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_historial_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
        ON DELETE SET NULL ON UPDATE CASCADE
);