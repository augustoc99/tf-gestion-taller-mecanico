# 2.ª Entrega — Arquitectura y Módulos

**Proyecto:** Sistema de Gestión para Taller Mecánico
**Materia:** Trabajo Final — Tecnicatura Universitaria en Programación
**Fecha:** Septiembre 2026

---

## 1. ESQUEMA DE LA BASE DE DATOS

Se opta por un **modelo de base de datos relacional**, utilizando **MySQL** como motor. Esta decisión se fundamenta en la naturaleza de los datos del sistema: la información (clientes, vehículos, órdenes de trabajo, mecánicos) posee una estructura fija y predecible, y las relaciones entre entidades son críticas para garantizar la trazabilidad. El modelo relacional, con sus restricciones de integridad referencial (claves foráneas) y transacciones ACID, asegura que no existan órdenes de trabajo sin un vehículo asociado, ni vehículos sin un cliente propietario.

### 1.1. Entidades y Atributos

#### Usuario
Representa a los usuarios del sistema (recepción/administración y mecánicos).

| Atributo | Tipo | Restricción | Descripción |
| :--- | :--- | :--- | :--- |
| `id_usuario` | INT | PK, AUTO_INCREMENT | Identificador único del usuario. |
| `username` | VARCHAR(50) | NOT NULL, UNIQUE | Nombre de usuario para el login. |
| `password_hash` | VARCHAR(255) | NOT NULL | Contraseña encriptada (BCrypt). |
| `nombre_completo` | VARCHAR(100) | NOT NULL | Nombre y apellido del usuario. |
| `email` | VARCHAR(100) | UNIQUE | Correo electrónico de contacto. |
| `rol` | ENUM('ADMIN', 'MECANICO') | NOT NULL | Rol del usuario en el sistema. |
| `activo` | BOOLEAN | DEFAULT TRUE | Indica si el usuario está habilitado. |
| `fecha_creacion` | DATETIME | DEFAULT CURRENT_TIMESTAMP | Fecha de alta del usuario. |

#### Cliente
Almacena los datos de los propietarios de vehículos.

| Atributo | Tipo | Restricción | Descripción |
| :--- | :--- | :--- | :--- |
| `id_cliente` | INT | PK, AUTO_INCREMENT | Identificador único del cliente. |
| `nombre` | VARCHAR(100) | NOT NULL | Nombre y apellido o razón social. |
| `dni_cuit` | VARCHAR(20) | UNIQUE | Documento o CUIT del cliente. |
| `telefono` | VARCHAR(20) | NOT NULL | Teléfono de contacto. |
| `email` | VARCHAR(100) | | Correo electrónico. |
| `direccion` | VARCHAR(200) | | Domicilio del cliente. |
| `fecha_registro` | DATETIME | DEFAULT CURRENT_TIMESTAMP | Fecha de alta del cliente. |

#### Vehiculo
Registra los vehículos que ingresan al taller.

| Atributo | Tipo | Restricción | Descripción |
| :--- | :--- | :--- | :--- |
| `id_vehiculo` | INT | PK, AUTO_INCREMENT | Identificador único del vehículo. |
| `patente` | VARCHAR(10) | NOT NULL, UNIQUE | Patente del vehículo (única). |
| `marca` | VARCHAR(50) | NOT NULL | Marca (ej. Toyota). |
| `modelo` | VARCHAR(50) | NOT NULL | Modelo (ej. Hilux). |
| `anio` | INT | | Año de fabricación. |
| `kilometraje_actual` | INT | | Último kilometraje registrado. |
| `id_cliente` | INT | FK → Cliente | Propietario del vehículo. |

#### OrdenTrabajo
Es el núcleo del sistema. Cada intervención en un vehículo se gestiona a través de una orden.

| Atributo | Tipo | Restricción | Descripción |
| :--- | :--- | :--- | :--- |
| `id_orden` | INT | PK, AUTO_INCREMENT | Identificador único de la orden. |
| `id_vehiculo` | INT | FK → Vehiculo | Vehículo sobre el que se trabaja. |
| `id_mecanico` | INT | FK → Usuario | Mecánico asignado a la orden. |
| `fecha_ingreso` | DATETIME | NOT NULL | Fecha y hora de ingreso del vehículo. |
| `fecha_egreso` | DATETIME | | Fecha y hora de entrega (NULL si no finalizó). |
| `kilometraje_ingreso` | INT | NOT NULL | Kilometraje al momento del ingreso. |
| `problema_informado` | TEXT | NOT NULL | Motivo de consulta del cliente. |
| `diagnostico` | TEXT | | Diagnóstico técnico del mecánico. |
| `estado` | ENUM(...) | NOT NULL, DEFAULT 'RECIBIDO' | Estado actual de la orden. |
| `observaciones` | TEXT | | Notas adicionales. |

#### TrabajoRealizado
Detalla las tareas específicas ejecutadas dentro de una orden.

| Atributo | Tipo | Restricción | Descripción |
| :--- | :--- | :--- | :--- |
| `id_trabajo` | INT | PK, AUTO_INCREMENT | Identificador único del trabajo. |
| `id_orden` | INT | FK → OrdenTrabajo | Orden a la que pertenece. |
| `descripcion` | TEXT | NOT NULL | Detalle del trabajo realizado. |
| `mano_obra_horas` | DECIMAL(5,2) | | Horas de mano de obra insumidas. |
| `repuestos_utilizados` | TEXT | | Repuestos y materiales empleados. |
| `fecha_registro` | DATETIME | DEFAULT CURRENT_TIMESTAMP | Fecha de carga del trabajo. |

#### HistorialEstado
Permite auditar y reconstruir la evolución de una orden a lo largo del tiempo.

| Atributo | Tipo | Restricción | Descripción |
| :--- | :--- | :--- | :--- |
| `id_historial` | INT | PK, AUTO_INCREMENT | Identificador único del registro. |
| `id_orden` | INT | FK → OrdenTrabajo | Orden cuyo estado cambió. |
| `estado_anterior` | ENUM(...) | | Estado previo al cambio. |
| `estado_nuevo` | ENUM(...) | NOT NULL | Nuevo estado de la orden. |
| `fecha_cambio` | DATETIME | DEFAULT CURRENT_TIMESTAMP | Fecha y hora del cambio. |
| `id_usuario` | INT | FK → Usuario | Usuario que realizó el cambio. |
| `observacion` | VARCHAR(255) | | Comentario opcional sobre el cambio. |

### 1.2. Relaciones entre Entidades

- **Cliente (1) → Vehiculo (N):** Un cliente puede poseer varios vehículos. Cada vehículo pertenece a un único cliente.
- **Vehiculo (1) → OrdenTrabajo (N):** Un vehículo puede tener múltiples órdenes a lo largo del tiempo (historial de reparaciones). Cada orden corresponde a un solo vehículo.
- **Usuario (1) → OrdenTrabajo (N):** Un mecánico puede tener asignadas varias órdenes. Cada orden tiene un único mecánico responsable.
- **OrdenTrabajo (1) → TrabajoRealizado (N):** Una orden puede contener uno o varios trabajos realizados.
- **OrdenTrabajo (1) → HistorialEstado (N):** Una orden genera múltiples registros de cambio de estado.

### 1.3. Diagrama Entidad-Relación

```mermaid
erDiagram
    CLIENTE ||--o{ VEHICULO : "posee"
    VEHICULO ||--o{ ORDEN_TRABAJO : "tiene"
    USUARIO ||--o{ ORDEN_TRABAJO : "es asignado"
    ORDEN_TRABAJO ||--o{ TRABAJO_REALIZADO : "contiene"
    ORDEN_TRABAJO ||--o{ HISTORIAL_ESTADO : "registra"

    CLIENTE {
        int id_cliente PK
        string nombre
        string dni_cuit
        string telefono
        string email
        string direccion
        datetime fecha_registro
    }
    VEHICULO {
        int id_vehiculo PK
        string patente
        string marca
        string modelo
        int anio
        int kilometraje_actual
        int id_cliente FK
    }
    USUARIO {
        int id_usuario PK
        string username
        string password_hash
        string nombre_completo
        string email
        enum rol
        boolean activo
        datetime fecha_creacion
    }
    ORDEN_TRABAJO {
        int id_orden PK
        int id_vehiculo FK
        int id_mecanico FK
        datetime fecha_ingreso
        datetime fecha_egreso
        int kilometraje_ingreso
        text problema_informado
        text diagnostico
        enum estado
        text observaciones
    }
    TRABAJO_REALIZADO {
        int id_trabajo PK
        int id_orden FK
        text descripcion
        decimal mano_obra_horas
        text repuestos_utilizados
        datetime fecha_registro
    }
    HISTORIAL_ESTADO {
        int id_historial PK
        int id_orden FK
        enum estado_anterior
        enum estado_nuevo
        datetime fecha_cambio
        int id_usuario FK
        string observacion
    }