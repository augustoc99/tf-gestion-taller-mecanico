# Sistema Web de Gestión para Taller Mecánico

Proyecto desarrollado en el marco del **Trabajo Final de la Tecnicatura en Programación**.

## Descripción

El proyecto consiste en el desarrollo de una aplicación web orientada a la gestión y seguimiento de vehículos dentro de un taller mecánico.

La propuesta surge de una problemática frecuente en talleres pequeños y medianos, donde la información vinculada con clientes, vehículos, turnos y reparaciones puede encontrarse distribuida entre agendas, registros en papel, planillas de cálculo o aplicaciones de mensajería.

El sistema busca centralizar esa información y permitir el seguimiento de cada vehículo desde la asignación de un turno hasta su entrega, manteniendo además un historial de las intervenciones realizadas.

## Objetivo

Desarrollar una aplicación web que permita organizar el proceso de atención de un vehículo dentro de un taller mecánico, centralizando la información de clientes, vehículos, turnos, órdenes de trabajo, diagnósticos y reparaciones.

El eje principal del proyecto será la **trazabilidad de las órdenes de trabajo**, permitiendo conocer el estado de una reparación y consultar posteriormente las intervenciones realizadas sobre cada vehículo.

## Alcance inicial

El producto mínimo contemplará la gestión de clientes, vehículos, mecánicos y turnos, junto con la generación y seguimiento de órdenes de trabajo.

Cada orden permitirá registrar el motivo de ingreso del vehículo, el kilometraje correspondiente, el mecánico responsable, el diagnóstico, las tareas realizadas, las observaciones y el estado de la reparación.

Los cambios de estado quedarán registrados para conservar la trazabilidad del proceso.

El sistema permitirá además consultar el historial de intervenciones de cada vehículo y contará con un dashboard básico para visualizar información general sobre la actividad del taller.

Quedan fuera del alcance inicial funcionalidades como facturación, gestión avanzada de stock, proveedores, pagos electrónicos, integración con WhatsApp, aplicación móvil y otras funciones administrativas que excedan el circuito principal de reparación. Estas podrán evaluarse posteriormente como posibles ampliaciones.

## Tecnologías

| Componente                | Tecnología                  |
| ------------------------- | --------------------------- |
| Backend                   | Java 17 + Spring Boot       |
| Persistencia              | Spring Data JPA / Hibernate |
| Gestión del proyecto Java | Maven                       |
| Base de datos             | H2                          |
| Frontend                  | HTML + CSS + TypeScript     |
| Entorno frontend          | Vite                        |
| Comunicación              | API REST + JSON + Fetch API |
| Pruebas de API            | Postman                     |
| Control de versiones      | Git + GitHub                |
| Despliegue previsto       | Vercel para el frontend     |

La selección tecnológica prioriza herramientas utilizadas durante la formación y una arquitectura de complejidad acorde al alcance del proyecto.

## Arquitectura preliminar

El sistema seguirá una arquitectura cliente-servidor.

El frontend será responsable de la interfaz de usuario y se comunicará con el backend mediante peticiones HTTP a una API REST, utilizando JSON como formato de intercambio de información.

```
Frontend
HTML + CSS + TypeScript + Vite
              |
              | HTTP / JSON
              v
           API REST
              |
              v
     Java 17 + Spring Boot
              |
              v
   Spring Data JPA / Hibernate
              |
              v
              H2
```

Dentro del backend se utilizará una organización basada en las capas **Controller, Service y Repository**.

El `Controller` recibirá las solicitudes HTTP, el `Service` concentrará la lógica de negocio y el `Repository` se encargará del acceso a los datos mediante Spring Data JPA.

## Flujo principal

El circuito principal previsto para la gestión de un vehículo será:

```
Turno
  |
  v
Ingreso del vehículo
  |
  v
Orden de trabajo
  |
  v
Diagnóstico
  |
  v
Reparación
  |
  v
Control final
  |
  v
Entrega
```

La orden de trabajo constituirá el elemento central del sistema y permitirá conservar la información generada durante cada intervención.

## Módulos principales

El sistema se organizará inicialmente en los siguientes módulos:

**Clientes:** registro y consulta de las personas que utilizan los servicios del taller.

**Vehículos:** registro de los vehículos asociados a cada cliente y consulta de sus intervenciones anteriores.

**Mecánicos:** administración básica de los mecánicos disponibles para la asignación de trabajos.

**Turnos:** organización de los ingresos programados al taller.

**Órdenes de trabajo:** registro del ingreso, diagnóstico, mecánico responsable, tareas realizadas, observaciones y estado de la reparación.

**Historial:** consulta de las órdenes e intervenciones realizadas anteriormente sobre cada vehículo.

**Dashboard:** visualización resumida de información relevante sobre la actividad actual del taller.

## Estados de una orden de trabajo

Durante el desarrollo se utilizará un conjunto controlado de estados para representar el avance de una reparación.

```
RECIBIDO
EN_DIAGNOSTICO
EN_REPARACION
ESPERANDO_REPUESTO
CONTROL_FINAL
FINALIZADO
ENTREGADO
```

Las transiciones entre estados formarán parte de la lógica de negocio del sistema y cada modificación quedará registrada para conservar la trazabilidad de la orden.

## Modelo conceptual preliminar

Las principales relaciones previstas son:

```
Cliente
   |
   | 1
   |
   | N
Vehiculo
   |
   |---------------- Turno
   |
   `---------------- OrdenTrabajo
                         |
                         |-- Mecanico
                         |
                         |-- DetalleTrabajo
                         |
                         `-- HistorialEstado
```

El modelo definitivo de datos y sus relaciones serán desarrollados en la etapa correspondiente de acuerdo con la hoja de ruta de la asignatura.

## Estructura del repositorio

Todo el proyecto se desarrolla dentro de un único repositorio de GitHub.

```
tf-gestion-taller-mecanico/
|
|-- backend/          # API y lógica de negocio
|-- frontend/         # Interfaz web
|-- database/         # Modelo y documentación de datos
|-- docs/             # Informes y entregas
|   `-- entrega-1/
|-- .gitignore
`-- README.md
```

La estructura se ampliará progresivamente a medida que se incorporen los componentes del sistema.

## Plan de trabajo

El desarrollo se realizará de forma incremental y siguiendo las etapas establecidas para el Trabajo Final.

| Etapa                            | Período previsto | Resultado                                                                            |
| -------------------------------- | ---------------- | ------------------------------------------------------------------------------------ |
| Propuesta y planificación        | hasta 30/08      | Definición del problema, alcance, tecnologías, arquitectura preliminar y repositorio |
| Arquitectura y modelo            | 31/08 al 13/09   | Definición de entidades, relaciones y estructura técnica                             |
| Persistencia y módulos iniciales | 14/09 al 27/09   | Implementación inicial con H2, JPA, clientes, vehículos y mecánicos                  |
| Turnos                           | 28/09 al 05/10   | Gestión de turnos                                                                    |
| Órdenes de trabajo               | 06/10 al 18/10   | Implementación del circuito principal de reparación                                  |
| Estados e historial              | 19/10 al 25/10   | Trazabilidad de órdenes y registro de cambios                                        |
| Frontend e integración           | 26/10 al 01/11   | Interfaces y comunicación con la API                                                 |
| Dashboard                        | 02/11 al 04/11   | Información general del taller                                                       |
| Pruebas y correcciones           | 05/11 al 08/11   | Validaciones, pruebas y resolución de errores                                        |
| Despliegue                       | 09/11 al 10/11   | Publicación del componente web                                                       |
| Documentación y cierre           | 11/11 al 14/11   | Informe, documentación y preparación de la entrega final                             |

Las fechas podrán ajustarse durante el desarrollo de acuerdo con el avance del proyecto y las observaciones realizadas por la tutora.

## Despliegue

De acuerdo con los requisitos de la asignatura, al menos uno de los componentes principales del proyecto deberá encontrarse alojado y funcionando en un servicio online.

Para el alcance inicial se prevé desplegar el frontend web mediante **Vercel**.

Durante el desarrollo se evaluará también la posibilidad de desplegar el backend, siempre que ello resulte compatible con la arquitectura implementada y no comprometa la estabilidad ni el alcance principal del proyecto.

## Organización del trabajo

El proyecto utilizará **Git y GitHub** para el control de versiones y la centralización de todo el desarrollo.

Las tareas se organizarán progresivamente mediante herramientas de seguimiento del repositorio, permitiendo registrar el avance de los distintos módulos y mantener trazabilidad sobre las modificaciones realizadas.

El desarrollo se realizará de manera compartida entre los integrantes, procurando que ambos participen en las diferentes áreas del sistema y puedan comprender y justificar las decisiones adoptadas durante la defensa final.

## Integrantes

**Augusto Matías Cúneo Brouwer de Koning**
**Nicolás Azcuy**

**Tutora:** Prof. María Candela Grosso

## Estado actual

El proyecto se encuentra actualmente en etapa de **análisis, planificación y definición de arquitectura preliminar**, correspondiente a la primera instancia de entrega.

En esta etapa se encuentran definidos el problema, el objetivo general, el alcance inicial, el stack tecnológico, la arquitectura preliminar, la estructura del repositorio y el plan de trabajo.

La implementación del backend, frontend y modelo definitivo de datos se realizará progresivamente de acuerdo con la hoja de ruta establecida.

## Instalación y ejecución

En la instancia actual todavía no existen componentes ejecutables del sistema.

Las instrucciones de instalación, configuración y ejecución serán incorporadas en esta sección una vez inicializados los proyectos de backend y frontend y se mantendrán actualizadas durante todo el desarrollo.
