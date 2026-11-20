# MiTaller

## Sistema Web de Gestión y Trazabilidad para Taller Mecánico

**Trabajo Final Integrador — Tecnicatura Universitaria en Programación a Distancia**
**Universidad Tecnológica Nacional**

**Integrantes:** Augusto Matías Cúneo Brouwer de Koning y Nicolás Azcuy
**Tutora:** Prof. María Candela Grosso

Repositorio oficial:
https://github.com/augustoc99/tf-gestion-taller-mecanico

Aplicación desplegada:
https://tf-gestion-taller-mecanico-frontend.vercel.app

Video demostrativo:
https://drive.google.com/file/d/1GjkmBgZEQXpnCtCkZUO4oBsn04Xg2TlX/view?usp=drive_link


---

## 1. Descripción del proyecto

MiTaller es una aplicación web desarrollada para centralizar la gestión operativa de un taller mecánico y mantener la trazabilidad de las intervenciones realizadas sobre cada vehículo.

El proyecto parte de una problemática habitual en talleres pequeños y medianos: la información vinculada con clientes, vehículos, diagnósticos, reparaciones y estados de trabajo suele encontrarse distribuida entre distintos medios, como planillas, anotaciones, mensajes o registros independientes. Esta dispersión dificulta conocer con precisión qué ocurrió durante una reparación y recuperar posteriormente el historial técnico de un vehículo.

La propuesta consiste en integrar esa información dentro de un único sistema, estructurando el proceso desde el ingreso del vehículo hasta su entrega al cliente.

El eje funcional de MiTaller es la orden de trabajo. A través de ella se registran el motivo de ingreso, el kilometraje, el mecánico asignado, el diagnóstico, los trabajos efectuados, las observaciones y cada cambio de estado producido durante la intervención.

---

## 2. Objetivo

El objetivo principal es disponer de una aplicación que permita registrar y consultar de manera organizada la información necesaria para gestionar una reparación y reconstruir posteriormente todo su recorrido.

La trazabilidad no se limita a mostrar el estado actual de una orden. El sistema conserva también los cambios producidos durante el proceso, el usuario responsable de cada acción y el historial de intervenciones anteriores del vehículo.

De esta forma, MiTaller permite responder de manera directa preguntas como cuándo ingresó un vehículo, por qué motivo, qué diagnóstico recibió, qué trabajos se realizaron, quién intervino y cuál fue la evolución de la orden hasta su finalización y entrega.

---

## 3. Alcance de la versión final

La versión final se concentra en el Producto Mínimo Viable definido para el proyecto. Se priorizó la correcta implementación del circuito principal antes que la incorporación de funcionalidades complementarias.

El sistema permite administrar usuarios, clientes y vehículos, generar órdenes de trabajo, asignar mecánicos, registrar información técnica, gestionar el avance de una reparación y consultar la trazabilidad completa de cada intervención.

También se incorporó la consulta del historial de intervenciones por vehículo, de modo que las órdenes anteriores puedan recuperarse desde la propia ficha de la unidad.

La gestión de turnos y el dashboard fueron considerados funcionalidades complementarias y no forman parte del alcance final implementado.

---

## 4. Arquitectura

MiTaller utiliza una arquitectura cliente-servidor basada en una API REST.

El frontend fue desarrollado con React y Vite. Su función es presentar la interfaz de usuario y consumir los servicios del backend mediante solicitudes HTTP.

El backend fue desarrollado con Spring Boot y concentra la lógica de negocio, las validaciones, la gestión de estados y el acceso a datos. La persistencia se implementa mediante Spring Data JPA e Hibernate sobre una base H2.

La comunicación entre ambos componentes se realiza mediante JSON.

```text
Frontend
React + Vite
      |
      | HTTP / JSON
      v
API REST
Spring Boot
      |
      v
Servicios y lógica de negocio
      |
      v
JPA / Hibernate
      |
      v
Base de datos H2
```

La separación entre frontend y backend permite mantener desacopladas la interfaz, la lógica de negocio y la persistencia.

---

## 5. Modelo funcional

### Usuarios y roles

El sistema contempla dos roles operativos:

`RECEPCION_ADMINISTRACION`

`MECANICO`

Recepción/Administración concentra las tareas vinculadas con el ingreso y seguimiento administrativo del vehículo. Puede gestionar clientes y vehículos, generar órdenes, asignar mecánicos, consultar información y registrar la entrega final.

El rol Mecánico interviene sobre las órdenes que le fueron asignadas. Puede registrar diagnósticos, trabajos realizados y avanzar una reparación dentro de los estados técnicos permitidos.

La versión actual no incorpora autenticación mediante usuario y contraseña. Para poder demostrar y probar las reglas de autorización del MVP, el frontend permite seleccionar un “Usuario actual”. Las validaciones relevantes se realizan igualmente en el backend.

---

### Clientes y vehículos

Los clientes pueden ser registrados, consultados, buscados y modificados.

Cada vehículo se encuentra asociado a un cliente. El sistema permite registrar sus datos, modificarlos y consultar las órdenes de trabajo relacionadas.

Desde el módulo de vehículos también se encuentra disponible el historial de intervenciones, que permite visualizar las distintas órdenes registradas para una misma unidad y acceder nuevamente al detalle de cada una.

---

### Órdenes de trabajo

La orden de trabajo constituye el núcleo del sistema.

En cada intervención se registran el vehículo, el cliente, el kilometraje de ingreso, el motivo informado, el mecánico asignado, el diagnóstico, las observaciones técnicas y los trabajos realizados.

Cada orden posee además un estado que representa la etapa actual del proceso.

---

## 6. Flujo de estados

El circuito implementado utiliza los siguientes estados:

```text
RECIBIDO
EN_DIAGNOSTICO
EN_REPARACION
ESPERANDO_REPUESTO
CONTROL_FINAL
FINALIZADO
ENTREGADO
```

El avance no puede realizarse libremente entre cualquier estado. Las transiciones se encuentran definidas y controladas por la lógica de negocio del backend.

El flujo general es el siguiente:

```text
RECIBIDO
   |
   v
EN_DIAGNOSTICO
   | \
   |  \
   v   v
EN_REPARACION <----> ESPERANDO_REPUESTO
   |
   v
CONTROL_FINAL
   | \
   |  \
   |   v
   | EN_REPARACION
   v
FINALIZADO
   |
   v
ENTREGADO
```

En total se encuentran implementadas nueve transiciones válidas.

Una orden puede crearse inicialmente sin mecánico asignado, pero debe contar con uno antes de comenzar el diagnóstico.

`ENTREGADO` representa el estado terminal del circuito.

---

## 7. Trazabilidad

Cada cambio de estado genera automáticamente un registro histórico.

El sistema conserva el estado anterior, el nuevo estado, la fecha y hora del cambio, el usuario responsable y una observación cuando corresponde.

Esto permite reconstruir posteriormente el recorrido completo de una orden, en lugar de conservar únicamente su situación actual.

La trazabilidad se complementa con el historial de intervenciones por vehículo, que permite recuperar las reparaciones realizadas en distintos ingresos al taller.

---

## 8. Reglas de negocio relevantes

Las principales reglas se encuentran implementadas en la capa de servicios del backend.

El sistema verifica que las transiciones de estado sean válidas, que los usuarios responsables se encuentren activos y que posean el rol necesario para realizar cada operación.

Los trabajos realizados solamente pueden ser registrados cuando la orden se encuentra en `EN_REPARACION` o `CONTROL_FINAL`. Además, el usuario debe poseer rol `MECANICO` y coincidir con el mecánico asignado a la orden.

La transición entre `FINALIZADO` y `ENTREGADO` corresponde a Recepción/Administración.

Estas restricciones no dependen únicamente de la interfaz. El backend vuelve a validarlas antes de modificar la información persistida.

---

## 9. Tecnologías utilizadas

| Componente | Tecnología |
|---|---|
| Backend | Java, Spring Boot |
| API | Spring Web / REST |
| Persistencia | Spring Data JPA, Hibernate |
| Base de datos | H2 |
| Frontend | React, Vite |
| Navegación | React Router |
| Comunicación HTTP | Axios |
| Gestión de dependencias frontend | pnpm |
| Build backend | Maven Wrapper |
| Pruebas | JUnit |
| Control de versiones | Git y GitHub |
| Pruebas de API | Postman |
| Despliegue backend | Railway |
| Despliegue frontend | Vercel |

---

## 10. Estructura del repositorio

Todo el proyecto se mantiene dentro de un único repositorio.

```text
tf-gestion-taller-mecanico/
│
├── backend/
│   └── API REST, dominio, lógica de negocio y persistencia
│
├── frontend/
│   └── Aplicación web React
│
├── database/
│   └── Documentación vinculada con el modelo de datos
│
├── docs/
│   └── Informes y documentación del Trabajo Final
│
├── .gitignore
└── README.md
```

La rama `main` contiene la versión integrada utilizada como versión final del proyecto.

---

## 11. Ejecución local

### Requisitos

Para ejecutar la aplicación localmente es necesario contar con Java, Node.js, pnpm y Git.

### Backend

Desde la raíz del repositorio:

```powershell
cd backend
.\mvnw spring-boot:run
```

La API queda disponible por defecto en:

```text
http://localhost:8080/api
```

Para ejecutar las pruebas automatizadas:

```powershell
cd backend
.\mvnw test
```

---

### Frontend

Desde la raíz del repositorio:

```powershell
cd frontend
pnpm install
pnpm dev
```

El servidor de desarrollo queda disponible normalmente en:

```text
http://localhost:5173
```

Si no se configura una variable `VITE_API_URL`, el frontend utiliza como API:

```text
http://localhost:8080/api
```

Para generar y validar el build de producción:

```powershell
pnpm build
```

---

## 12. Despliegue

La versión final se encuentra disponible online mediante dos servicios independientes.

### Frontend

El frontend se encuentra publicado en Vercel:

```text
https://tf-gestion-taller-mecanico-frontend.vercel.app
```

La aplicación utiliza la variable de entorno `VITE_API_URL` para indicar la dirección pública del backend.

En producción:

```text
https://tf-gestion-taller-mecanico-production.up.railway.app/api
```

El archivo `frontend/vercel.json` contiene la configuración necesaria para que React Router funcione correctamente también al acceder directamente o actualizar rutas internas.

El despliegue utilizado para la entrega se realiza manualmente desde el directorio `frontend`:

```powershell
pnpm dlx vercel --prod --token $env:VERCEL_TOKEN
```

El token utilizado para autenticarse en Vercel es privado y no forma parte del repositorio.

---

### Backend

El backend se encuentra desplegado en Railway:

```text
https://tf-gestion-taller-mecanico-production.up.railway.app
```

La API pública se encuentra disponible en:

```text
https://tf-gestion-taller-mecanico-production.up.railway.app/api
```

En producción se utiliza una base H2 almacenada en modo archivo sobre un volumen persistente de Railway:

```text
jdbc:h2:file:/data/taller
```

El directorio `/data` se encuentra asociado al volumen utilizado por el servicio, evitando perder los registros cuando la aplicación vuelve a desplegarse.

La consola H2 permanece deshabilitada en producción.

Debido a que el repositorio contiene frontend, backend y documentación, el despliegue utilizado durante la entrega se realiza indicando expresamente que el directorio `backend` debe ser tratado como raíz:

```powershell
railway up .\backend --path-as-root
```

---

## 13. Configuración de CORS

El backend admite solicitudes desde el frontend local y desde la aplicación publicada en Vercel.

La configuración se controla mediante la variable:

```text
CORS_ALLOWED_ORIGINS
```

Entre los orígenes utilizados durante el desarrollo y la puesta en producción se encuentran:

```text
http://localhost:5173
https://tf-gestion-taller-mecanico-frontend.vercel.app
```

---

## 14. API REST

La API se encuentra organizada por recursos.

### Usuarios

```text
GET    /api/usuarios
GET    /api/usuarios/{id}
GET    /api/usuarios/mecanicos
GET    /api/usuarios/roles
POST   /api/usuarios
PUT    /api/usuarios/{id}
PATCH  /api/usuarios/{id}/activo
```

### Clientes

```text
GET    /api/clientes
GET    /api/clientes/{id}
POST   /api/clientes
PUT    /api/clientes/{id}
```

Las consultas permiten también realizar búsquedas mediante parámetros.

### Vehículos

```text
GET    /api/vehiculos
GET    /api/vehiculos/{id}
POST   /api/vehiculos
PUT    /api/vehiculos/{id}
```

También pueden consultarse vehículos por cliente y realizar búsquedas mediante parámetros.

### Órdenes de trabajo

```text
GET    /api/ordenes
POST   /api/ordenes
GET    /api/ordenes/{id}

PUT    /api/ordenes/{id}/estado
PUT    /api/ordenes/{id}/tecnica
PUT    /api/ordenes/{id}/mecanico

GET    /api/ordenes/{id}/historial

POST   /api/ordenes/{id}/trabajos
GET    /api/ordenes/{id}/trabajos

GET    /api/ordenes/vehiculo/{idVehiculo}
```

---

## 15. Manejo de errores

El backend implementa un manejo centralizado de excepciones mediante un controlador global.

Las solicitudes inválidas y las operaciones que incumplen reglas de negocio son rechazadas mediante respuestas HTTP adecuadas.

Por ejemplo, cuando el cuerpo de una solicitud contiene un JSON inválido, la API responde con estado `400 Bad Request` y una estructura informativa:

```json
{
  "error": "JSON_INVALIDO",
  "detalle": "El cuerpo JSON no es valido o contiene una codificacion incorrecta."
}
```

Este criterio permite evitar que los errores internos del backend se trasladen directamente al cliente y facilita el tratamiento de respuestas desde el frontend.

---

## 16. Pruebas

El backend cuenta con pruebas automatizadas sobre los principales servicios y reglas de negocio.

La versión final fue validada mediante la suite de tests del proyecto y mediante pruebas funcionales sobre la aplicación desplegada.

Durante la validación integral se comprobó el circuito completo:

```text
Usuario
  ↓
Cliente
  ↓
Vehículo
  ↓
Orden de trabajo
  ↓
Asignación de mecánico
  ↓
Diagnóstico
  ↓
Trabajo realizado
  ↓
Cambios de estado
  ↓
Control final
  ↓
Finalización
  ↓
Entrega
  ↓
Historial de intervenciones
```

También se comprobaron las restricciones de roles, las transiciones válidas e inválidas, la persistencia de diagnósticos y trabajos, el historial de estados y la consulta del historial de cada vehículo.

---

## 17. Limitaciones del MVP

El objetivo del proyecto fue completar correctamente el circuito principal de gestión y trazabilidad. Por ese motivo, algunas funcionalidades previstas durante las primeras etapas fueron deliberadamente dejadas fuera del producto final.

La versión actual no incorpora autenticación real, gestión de turnos, dashboard, presupuestos, control de stock, facturación ni portal para clientes.

Estas funcionalidades no son necesarias para demostrar el problema central abordado y pueden incorporarse en futuras iteraciones sin modificar la base conceptual de la solución.

---

## 18. Posibles evoluciones

Una evolución natural del sistema podría incorporar autenticación y autorización mediante sesiones o tokens, gestión de turnos, presupuestos, repuestos, archivos asociados a órdenes, reportes, notificaciones y acceso para clientes.

También podría reemplazarse H2 por un motor de base de datos orientado a un entorno productivo de mayor escala e incorporarse un proceso automatizado de integración y despliegue continuo.

---

## 19. Estado del proyecto

La versión final integrada en `main` contiene el frontend, el backend, la persistencia, las reglas de negocio, las pruebas y la configuración necesaria para su despliegue.

El sistema se encuentra operativo online y permite ejecutar de manera completa el circuito principal definido para el Trabajo Final.

MiTaller alcanza así el objetivo planteado: disponer de una aplicación web funcional capaz de centralizar la gestión de las reparaciones y conservar la trazabilidad de las intervenciones realizadas sobre cada vehículo.
