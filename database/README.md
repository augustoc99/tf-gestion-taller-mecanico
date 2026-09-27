# Base de datos

MiTaller utilizará **H2** como base de datos relacional, de acuerdo con el stack definido para el proyecto.

El esquema correspondiente a la segunda entrega se encuentra en:

- [schema.sql](./schema.sql)

El modelo incluye las seis entidades principales del MVP:

- Usuario
- Cliente
- Vehículo
- Orden de Trabajo
- Detalle de Trabajo
- Historial de Estado de la Orden

Las restricciones estructurales se expresan en el esquema mediante claves primarias, claves foráneas, valores controlados y restricciones de integridad. Las reglas que dependen del flujo funcional —por ejemplo, exigir un mecánico antes de pasar una orden a `EN_DIAGNOSTICO` o validar las transiciones permitidas— se implementarán en la capa Service del backend.
