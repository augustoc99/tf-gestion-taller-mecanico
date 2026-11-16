# MiTaller

## Sistema Web de GestiÃ³n y Trazabilidad para Taller MecÃ¡nico

**Trabajo Final Integrador â€” Tecnicatura Universitaria en ProgramaciÃ³n a Distancia**
**Universidad TecnolÃ³gica Nacional**

**Integrantes:** Augusto MatÃ­as CÃºneo Brouwer de Koning y NicolÃ¡s Azcuy
**Tutora:** Prof. MarÃ­a Candela Grosso

Repositorio oficial:
https://github.com/augustoc99/tf-gestion-taller-mecanico

AplicaciÃ³n desplegada:
https://tf-gestion-taller-mecanico-frontend.vercel.app

---

## 1. DescripciÃ³n del proyecto

MiTaller es una aplicaciÃ³n web desarrollada para centralizar la gestiÃ³n operativa de un taller mecÃ¡nico y mantener la trazabilidad de las intervenciones realizadas sobre cada vehÃ­culo.

El proyecto parte de una problemÃ¡tica habitual en talleres pequeÃ±os y medianos: la informaciÃ³n vinculada con clientes, vehÃ­culos, diagnÃ³sticos, reparaciones y estados de trabajo suele encontrarse distribuida entre distintos medios, como planillas, anotaciones, mensajes o registros independientes. Esta dispersiÃ³n dificulta conocer con precisiÃ³n quÃ© ocurriÃ³ durante una reparaciÃ³n y recuperar posteriormente el historial tÃ©cnico de un vehÃ­culo.

La propuesta consiste en integrar esa informaciÃ³n dentro de un Ãºnico sistema, estructurando el proceso desde el ingreso del vehÃ­culo hasta su entrega al cliente.

El eje funcional de MiTaller es la orden de trabajo. A travÃ©s de ella se registran el motivo de ingreso, el kilometraje, el mecÃ¡nico asignado, el diagnÃ³stico, los trabajos efectuados, las observaciones y cada cambio de estado producido durante la intervenciÃ³n.

---

## 2. Objetivo

El objetivo principal es disponer de una aplicaciÃ³n que permita registrar y consultar de manera organizada la informaciÃ³n necesaria para gestionar una reparaciÃ³n y reconstruir posteriormente todo su recorrido.

La trazabilidad no se limita a mostrar el estado actual de una orden. El sistema conserva tambiÃ©n los cambios producidos durante el proceso, el usuario responsable de cada acciÃ³n y el historial de intervenciones anteriores del vehÃ­culo.

De esta forma, MiTaller permite responder de manera directa preguntas como cuÃ¡ndo ingresÃ³ un vehÃ­culo, por quÃ© motivo, quÃ© diagnÃ³stico recibiÃ³, quÃ© trabajos se realizaron, quiÃ©n intervino y cuÃ¡l fue la evoluciÃ³n de la orden hasta su finalizaciÃ³n y entrega.

---

## 3. Alcance de la versiÃ³n final

La versiÃ³n final se concentra en el Producto MÃ­nimo Viable definido para el proyecto. Se priorizÃ³ la correcta implementaciÃ³n del circuito principal antes que la incorporaciÃ³n de funcionalidades complementarias.

El sistema permite administrar usuarios, clientes y vehÃ­culos, generar Ã³rdenes de trabajo, asignar mecÃ¡nicos, registrar informaciÃ³n tÃ©cnica, gestionar el avance de una reparaciÃ³n y consultar la trazabilidad completa de cada intervenciÃ³n.

TambiÃ©n se incorporÃ³ la consulta del historial de intervenciones por vehÃ­culo, de modo que las Ã³rdenes anteriores puedan recuperarse desde la propia ficha de la unidad.

La gestiÃ³n de turnos y el dashboard fueron considerados funcionalidades complementarias y no forman parte del alcance final implementado.

---

## 4. Arquitectura

MiTaller utiliza una arquitectura cliente-servidor basada en una API REST.

El frontend fue desarrollado con React y Vite. Su funciÃ³n es presentar la interfaz de usuario y consumir los servicios del backend mediante solicitudes HTTP.

El backend fue desarrollado con Spring Boot y concentra la lÃ³gica de negocio, las validaciones, la gestiÃ³n de estados y el acceso a datos. La persistencia se implementa mediante Spring Data JPA e Hibernate sobre una base H2.

La comunicaciÃ³n entre ambos componentes se realiza mediante JSON.

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
Servicios y lÃ³gica de negocio
      |
      v
JPA / Hibernate
      |
      v
Base de datos H2
```

La separaciÃ³n entre frontend y backend permite mantener desacopladas la interfaz, la lÃ³gica de negocio y la persistencia.

---

## 5. Modelo funcional

### Usuarios y roles

El sistema contempla dos roles operativos:

`RECEPCION_ADMINISTRACION`

`MECANICO`

RecepciÃ³n/AdministraciÃ³n concentra las tareas vinculadas con el ingreso y seguimiento administrativo del vehÃ­culo. Puede gestionar clientes y vehÃ­culos, generar Ã³rdenes, asignar mecÃ¡nicos, consultar informaciÃ³n y registrar la entrega final.

El rol MecÃ¡nico interviene sobre las Ã³rdenes que le fueron asignadas. Puede registrar diagnÃ³sticos, trabajos realizados y avanzar una reparaciÃ³n dentro de los estados tÃ©cnicos permitidos.

La versiÃ³n actual no incorpora autenticaciÃ³n mediante usuario y contraseÃ±a. Para poder demostrar y probar las reglas de autorizaciÃ³n del MVP, el frontend permite seleccionar un â€œUsuario actualâ€. Las validaciones relevantes se realizan igualmente en el backend.

---

### Clientes y vehÃ­culos

Los clientes pueden ser registrados, consultados, buscados y modificados.

Cada vehÃ­culo se encuentra asociado a un cliente. El sistema permite registrar sus datos, modificarlos y consultar las Ã³rdenes de trabajo relacionadas.

Desde el mÃ³dulo de vehÃ­culos tambiÃ©n se encuentra disponible el historial de intervenciones, que permite visualizar las distintas Ã³rdenes registradas para una misma unidad y acceder nuevamente al detalle de cada una.

---

### Ã“rdenes de trabajo

La orden de trabajo constituye el nÃºcleo del sistema.

En cada intervenciÃ³n se registran el vehÃ­culo, el cliente, el kilometraje de ingreso, el motivo informado, el mecÃ¡nico asignado, el diagnÃ³stico, las observaciones tÃ©cnicas y los trabajos realizados.

Cada orden posee ademÃ¡s un estado que representa la etapa actual del proceso.

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

El avance no puede realizarse libremente entre cualquier estado. Las transiciones se encuentran definidas y controladas por la lÃ³gica de negocio del backend.

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

En total se encuentran implementadas nueve transiciones vÃ¡lidas.

Una orden puede crearse inicialmente sin mecÃ¡nico asignado, pero debe contar con uno antes de comenzar el diagnÃ³stico.

`ENTREGADO` representa el estado terminal del circuito.

---

## 7. Trazabilidad

Cada cambio de estado genera automÃ¡ticamente un registro histÃ³rico.

El sistema conserva el estado anterior, el nuevo estado, la fecha y hora del cambio, el usuario responsable y una observaciÃ³n cuando corresponde.

Esto permite reconstruir posteriormente el recorrido completo de una orden, en lugar de conservar Ãºnicamente su situaciÃ³n actual.

La trazabilidad se complementa con el historial de intervenciones por vehÃ­culo, que permite recuperar las reparaciones realizadas en distintos ingresos al taller.

---

## 8. Reglas de negocio relevantes

Las principales reglas se encuentran implementadas en la capa de servicios del backend.

El sistema verifica que las transiciones de estado sean vÃ¡lidas, que los usuarios responsables se encuentren activos y que posean el rol necesario para realizar cada operaciÃ³n.

Los trabajos realizados solamente pueden ser registrados cuando la orden se encuentra en `EN_REPARACION` o `CONTROL_FINAL`. AdemÃ¡s, el usuario debe poseer rol `MECANICO` y coincidir con el mecÃ¡nico asignado a la orden.

La transiciÃ³n entre `FINALIZADO` y `ENTREGADO` corresponde a RecepciÃ³n/AdministraciÃ³n.

Estas restricciones no dependen Ãºnicamente de la interfaz. El backend vuelve a validarlas antes de modificar la informaciÃ³n persistida.

---

## 9. TecnologÃ­as utilizadas

| Componente | TecnologÃ­a |
|---|---|
| Backend | Java, Spring Boot |
| API | Spring Web / REST |
| Persistencia | Spring Data JPA, Hibernate |
| Base de datos | H2 |
| Frontend | React, Vite |
| NavegaciÃ³n | React Router |
| ComunicaciÃ³n HTTP | Axios |
| GestiÃ³n de dependencias frontend | pnpm |
| Build backend | Maven Wrapper |
| Pruebas | JUnit |
| Control de versiones | Git y GitHub |
| Pruebas de API | Postman |
| Despliegue backend | Railway |
| Despliegue frontend | Vercel |

---

## 10. Estructura del repositorio

Todo el proyecto se mantiene dentro de un Ãºnico repositorio.

```text
tf-gestion-taller-mecanico/
â”‚
â”œâ”€â”€ backend/
â”‚   â””â”€â”€ API REST, dominio, lÃ³gica de negocio y persistencia
â”‚
â”œâ”€â”€ frontend/
â”‚   â””â”€â”€ AplicaciÃ³n web React
â”‚
â”œâ”€â”€ database/
â”‚   â””â”€â”€ DocumentaciÃ³n vinculada con el modelo de datos
â”‚
â”œâ”€â”€ docs/
â”‚   â””â”€â”€ Informes y documentaciÃ³n del Trabajo Final
â”‚
â”œâ”€â”€ .gitignore
â””â”€â”€ README.md
```

La rama `main` contiene la versiÃ³n integrada utilizada como versiÃ³n final del proyecto.

---

## 11. EjecuciÃ³n local

### Requisitos

Para ejecutar la aplicaciÃ³n localmente es necesario contar con Java, Node.js, pnpm y Git.

### Backend

Desde la raÃ­z del repositorio:

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

Desde la raÃ­z del repositorio:

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

Para generar y validar el build de producciÃ³n:

```powershell
pnpm build
```

---

## 12. Despliegue

La versiÃ³n final se encuentra disponible online mediante dos servicios independientes.

### Frontend

El frontend se encuentra publicado en Vercel:

```text
https://tf-gestion-taller-mecanico-frontend.vercel.app
```

La aplicaciÃ³n utiliza la variable de entorno `VITE_API_URL` para indicar la direcciÃ³n pÃºblica del backend.

En producciÃ³n:

```text
https://tf-gestion-taller-mecanico-production.up.railway.app/api
```

El archivo `frontend/vercel.json` contiene la configuraciÃ³n necesaria para que React Router funcione correctamente tambiÃ©n al acceder directamente o actualizar rutas internas.

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

La API pÃºblica se encuentra disponible en:

```text
https://tf-gestion-taller-mecanico-production.up.railway.app/api
```

En producciÃ³n se utiliza una base H2 almacenada en modo archivo sobre un volumen persistente de Railway:

```text
jdbc:h2:file:/data/taller
```

El directorio `/data` se encuentra asociado al volumen utilizado por el servicio, evitando perder los registros cuando la aplicaciÃ³n vuelve a desplegarse.

La consola H2 permanece deshabilitada en producciÃ³n.

Debido a que el repositorio contiene frontend, backend y documentaciÃ³n, el despliegue utilizado durante la entrega se realiza indicando expresamente que el directorio `backend` debe ser tratado como raÃ­z:

```powershell
railway up .\backend --path-as-root
```

---

## 13. ConfiguraciÃ³n de CORS

El backend admite solicitudes desde el frontend local y desde la aplicaciÃ³n publicada en Vercel.

La configuraciÃ³n se controla mediante la variable:

```text
CORS_ALLOWED_ORIGINS
```

Entre los orÃ­genes utilizados durante el desarrollo y la puesta en producciÃ³n se encuentran:

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

Las consultas permiten tambiÃ©n realizar bÃºsquedas mediante parÃ¡metros.

### VehÃ­culos

```text
GET    /api/vehiculos
GET    /api/vehiculos/{id}
POST   /api/vehiculos
PUT    /api/vehiculos/{id}
```

TambiÃ©n pueden consultarse vehÃ­culos por cliente y realizar bÃºsquedas mediante parÃ¡metros.

### Ã“rdenes de trabajo

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

Las solicitudes invÃ¡lidas y las operaciones que incumplen reglas de negocio son rechazadas mediante respuestas HTTP adecuadas.

Por ejemplo, cuando el cuerpo de una solicitud contiene un JSON invÃ¡lido, la API responde con estado `400 Bad Request` y una estructura informativa:

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

La versiÃ³n final fue validada mediante la suite de tests del proyecto y mediante pruebas funcionales sobre la aplicaciÃ³n desplegada.

Durante la validaciÃ³n integral se comprobÃ³ el circuito completo:

```text
Usuario
  â†“
Cliente
  â†“
VehÃ­culo
  â†“
Orden de trabajo
  â†“
AsignaciÃ³n de mecÃ¡nico
  â†“
DiagnÃ³stico
  â†“
Trabajo realizado
  â†“
Cambios de estado
  â†“
Control final
  â†“
FinalizaciÃ³n
  â†“
Entrega
  â†“
Historial de intervenciones
```

TambiÃ©n se comprobaron las restricciones de roles, las transiciones vÃ¡lidas e invÃ¡lidas, la persistencia de diagnÃ³sticos y trabajos, el historial de estados y la consulta del historial de cada vehÃ­culo.

---

## 17. Limitaciones del MVP

El objetivo del proyecto fue completar correctamente el circuito principal de gestiÃ³n y trazabilidad. Por ese motivo, algunas funcionalidades previstas durante las primeras etapas fueron deliberadamente dejadas fuera del producto final.

La versiÃ³n actual no incorpora autenticaciÃ³n real, gestiÃ³n de turnos, dashboard, presupuestos, control de stock, facturaciÃ³n ni portal para clientes.

Estas funcionalidades no son necesarias para demostrar el problema central abordado y pueden incorporarse en futuras iteraciones sin modificar la base conceptual de la soluciÃ³n.

---

## 18. Posibles evoluciones

Una evoluciÃ³n natural del sistema podrÃ­a incorporar autenticaciÃ³n y autorizaciÃ³n mediante sesiones o tokens, gestiÃ³n de turnos, presupuestos, repuestos, archivos asociados a Ã³rdenes, reportes, notificaciones y acceso para clientes.

TambiÃ©n podrÃ­a reemplazarse H2 por un motor de base de datos orientado a un entorno productivo de mayor escala e incorporarse un proceso automatizado de integraciÃ³n y despliegue continuo.

---

## 19. Estado del proyecto

La versiÃ³n final integrada en `main` contiene el frontend, el backend, la persistencia, las reglas de negocio, las pruebas y la configuraciÃ³n necesaria para su despliegue.

El sistema se encuentra operativo online y permite ejecutar de manera completa el circuito principal definido para el Trabajo Final.

MiTaller alcanza asÃ­ el objetivo planteado: disponer de una aplicaciÃ³n web funcional capaz de centralizar la gestiÃ³n de las reparaciones y conservar la trazabilidad de las intervenciones realizadas sobre cada vehÃ­culo.
