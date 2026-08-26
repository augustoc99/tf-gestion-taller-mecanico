# Sistema Web de Gestión para Taller Mecánico

Proyecto desarrollado en el marco del **Trabajo Final de la Tecnicatura Universitaria en Programación a Distancia** de la Universidad Tecnológica Nacional.

## Descripción

El proyecto consiste en el desarrollo de una aplicación web orientada a la gestión y seguimiento de vehículos dentro de un taller mecánico.

La propuesta surge de una problemática que puede presentarse en talleres pequeños y medianos, donde la información vinculada con clientes, vehículos, turnos y reparaciones puede encontrarse distribuida entre agendas, registros en papel, planillas de cálculo, llamadas telefónicas o aplicaciones de mensajería.

El sistema busca centralizar esa información y permitir el seguimiento de cada vehículo desde su ingreso al taller —ya sea programado mediante turno o realizado de forma directa— hasta su entrega, manteniendo además un historial organizado de las intervenciones realizadas.

## Objetivo

Desarrollar una aplicación web que permita gestionar el proceso de atención de un vehículo dentro de un taller mecánico, desde su ingreso —programado mediante turno o realizado de forma directa— hasta su entrega, centralizando la información correspondiente a clientes, vehículos, órdenes de trabajo, diagnósticos e intervenciones realizadas.

El eje principal del proyecto será la **trazabilidad de las órdenes de trabajo**, permitiendo conocer el estado de una reparación y consultar posteriormente las intervenciones realizadas sobre cada vehículo.

## Alcance inicial

El producto mínimo contemplará la gestión de clientes, vehículos, mecánicos y turnos, junto con la generación y seguimiento de órdenes de trabajo.

Cada orden permitirá registrar el motivo de ingreso del vehículo, el kilometraje correspondiente a esa intervención, el mecánico responsable, el diagnóstico, las tareas realizadas, las observaciones y el estado de la reparación.

Los cambios de estado quedarán registrados para conservar la trazabilidad del proceso. Las diferentes órdenes permanecerán vinculadas al vehículo correspondiente, permitiendo consultar posteriormente su historial de intervenciones.

El sistema contará además con un dashboard básico para visualizar información resumida sobre la actividad general del taller.

Quedan fuera del alcance inicial funcionalidades como facturación electrónica, integración con organismos fiscales, contabilidad, gestión avanzada de stock, administración completa de proveedores, pagos electrónicos, integración automática con WhatsApp, aplicación móvil, gestión de aseguradoras y funcionalidades basadas en inteligencia artificial. Estas características podrán evaluarse posteriormente como posibles ampliaciones.

## Tecnologías

| Componente | Tecnología |
|---|---|
| Lenguaje backend | Java 17 |
| Framework backend | Spring Boot |
| Persistencia | Spring Data JPA / Hibernate |
| Gestión del proyecto Java | Maven |
| Base de datos | H2 |
| Frontend | HTML + CSS + TypeScript |
| Entorno frontend | Vite |
| Comunicación | API REST + HTTP + JSON |
| Consumo de API | Fetch API |
| Pruebas de API | Postman |
| Control de versiones | Git + GitHub |
| Despliegue previsto | Vercel |

La selección tecnológica prioriza herramientas utilizadas durante la formación y una arquitectura de complejidad acorde con el alcance y los plazos del proyecto.

Durante el desarrollo local se prevé utilizar H2 en modo archivo para conservar la información entre ejecuciones.

## Arquitectura preliminar

El sistema seguirá una arquitectura cliente-servidor con separación entre la interfaz de usuario, la lógica de negocio y la persistencia.

```text
FRONTEND
HTML + CSS + TypeScript + Vite
              |
              | HTTP / JSON
              v
           API REST
              |
              v
BACKEND
Java 17 + Spring Boot
              |
              v
Spring Data JPA / Hibernate
              |
              v
              H2
```

El frontend será responsable de presentar la información y recibir las acciones de las personas usuarias. La comunicación con el backend se realizará mediante solicitudes HTTP a una API REST, utilizando JSON para el intercambio de datos.

Dentro del backend se utilizará una organización basada en las capas **Controller, Service y Repository**.

```text
CONTROLLER
    |
    v
SERVICE
    |
    v
REPOSITORY
Spring Data JPA
    |
    v
H2
```

El `Controller` recibirá las solicitudes HTTP y expondrá los endpoints de la API. El `Service` concentrará la lógica de negocio y el `Repository` se encargará del acceso a los datos mediante Spring Data JPA.

## Flujo principal

El proceso podrá comenzar mediante un turno previamente registrado o mediante el ingreso directo de un vehículo al taller. La existencia de un turno no será condición obligatoria para generar una orden de trabajo.

El circuito principal previsto será:

```text
TURNO PREVIO
(opcional)
     |
     v
INGRESO DEL VEHÍCULO
     |
     v
ORDEN DE TRABAJO
     |
     v
DIAGNÓSTICO
     |
     v
REPARACIÓN
     |
     v
CONTROL FINAL
     |
     v
ENTREGA
     |
     v
HISTORIAL DEL VEHÍCULO
```

La orden de trabajo constituirá el elemento central del sistema y permitirá conservar la información generada durante cada intervención.

## Módulos principales

El sistema se organizará inicialmente en los módulos de clientes, vehículos, mecánicos, turnos, órdenes de trabajo, trabajos realizados, historial y dashboard.

El módulo de **clientes** permitirá registrar y consultar la información básica de las personas que utilizan el servicio del taller.

El módulo de **vehículos** permitirá registrar las unidades asociadas a cada cliente y acceder posteriormente a sus intervenciones anteriores.

El módulo de **mecánicos** mantendrá la información necesaria para asignar responsables a las órdenes de trabajo.

El módulo de **turnos** permitirá organizar los ingresos programados al taller, sin impedir la generación de órdenes de trabajo correspondientes a vehículos que ingresen sin turno previo.

El módulo de **órdenes de trabajo** constituirá el núcleo funcional del sistema y permitirá registrar el ingreso, diagnóstico, mecánico responsable, tareas realizadas, observaciones y estado de cada reparación.

El módulo de **trabajos realizados** permitirá detallar las tareas efectuadas dentro de cada orden.

El **historial** permitirá consultar cronológicamente las intervenciones anteriores realizadas sobre un vehículo a partir de sus órdenes de trabajo.

El **dashboard** presentará información resumida sobre la actividad actual del taller.

## Estados de una orden de trabajo

Durante el desarrollo se utilizará un conjunto controlado de estados para representar el avance de una reparación.

```text
RECIBIDO
    |
    v
EN DIAGNÓSTICO
    |
    v
EN REPARACIÓN
    |
    v
CONTROL FINAL
    |
    v
FINALIZADO
    |
    v
ENTREGADO
```

Cuando la continuidad de una reparación dependa de la disponibilidad de un componente necesario podrá utilizarse temporalmente el estado **ESPERANDO REPUESTO**.

Las transiciones entre estados formarán parte de la lógica de negocio del sistema y cada modificación quedará registrada para conservar la trazabilidad de la orden.

## Modelo conceptual preliminar

Las principales entidades identificadas son Cliente, Vehículo, Mecánico, Turno, Orden de Trabajo, Detalle de Trabajo e Historial de Estado de la Orden.

De manera preliminar, sus relaciones principales son:

```text
CLIENTE
   |
   | 1 : N
   v
VEHÍCULO
   |
   |---- TURNO
   |
   `---- ORDEN DE TRABAJO
              |
              |---- MECÁNICO
              |
              |---- DETALLE DE TRABAJO
              |
              `---- HISTORIAL DE ESTADO
```

Un cliente podrá tener varios vehículos. Cada vehículo podrá registrar múltiples turnos y múltiples órdenes de trabajo a lo largo del tiempo.

Una orden de trabajo corresponderá a un vehículo y podrá tener un mecánico responsable, varios trabajos realizados y múltiples registros de cambio de estado.

El historial general del vehículo no requerirá inicialmente una entidad independiente, ya que podrá obtenerse mediante la consulta cronológica de las órdenes de trabajo vinculadas a la unidad.

El modelo definitivo de datos y sus relaciones será desarrollado durante la etapa correspondiente de acuerdo con la hoja de ruta de la asignatura.

## Estructura del repositorio

Todo el proyecto se desarrolla dentro de un único repositorio de GitHub.

```text
tf-gestion-taller-mecanico/
|
|-- backend/          # Backend y API REST
|-- frontend/         # Interfaz web
|-- database/         # Modelo y documentación de datos
|-- docs/             # Informes y entregas
|   `-- entrega-1/
|       |-- README.md
|       `-- Trabajo_Final_Primera_Entrega.pdf
|-- .gitignore
`-- README.md
```

La estructura se ampliará progresivamente a medida que se incorporen los componentes del sistema.

## Organización del trabajo

El proyecto utiliza **Git y GitHub** para el control de versiones y la centralización del código y la documentación.

La planificación se realiza mediante **GitHub Projects**, utilizando una organización tipo Kanban.

```text
BACKLOG
   |
   v
TO DO
   |
   v
IN PROGRESS
   |
   v
REVIEW
   |
   v
DONE
```

Las tareas principales se registran mediante Issues vinculadas al repositorio. Cada Issue describe el objetivo de la tarea, su alcance y las condiciones necesarias para considerarla finalizada.

La incorporación del estado `Review` permite separar una tarea técnicamente finalizada de una tarea efectivamente revisada antes de considerarla cerrada.

El desarrollo se realizará de manera compartida entre los integrantes, procurando que ambos participen en las diferentes áreas del sistema y puedan comprender y justificar las decisiones adoptadas durante la defensa final.

## Plan de trabajo

El desarrollo se realizará de manera incremental, respetando los hitos establecidos por la asignatura y priorizando inicialmente la definición del dominio y del modelo de datos antes de avanzar sobre la implementación.

| Etapa | Período previsto | Resultado esperado |
|---|---|---|
| Propuesta y planificación | hasta 30/08 | Definición del problema, alcance, tecnologías, arquitectura preliminar, repositorio y plan de trabajo |
| Arquitectura y modelo conceptual | 31/08 al 13/09 | Definición de entidades, relaciones, arquitectura y estructura técnica del proyecto |
| Diseño de datos y módulos | 14/09 al 27/09 | Modelo relacional, listado definitivo de módulos, documentación de arquitectura y preparación de la segunda entrega |
| Inicialización técnica | 28/09 al 04/10 | Configuración de backend, frontend, H2 y persistencia mediante JPA |
| Módulos iniciales | 05/10 al 11/10 | Implementación de clientes, vehículos y mecánicos |
| Gestión de turnos | 12/10 al 18/10 | Implementación del módulo de turnos e ingresos programados |
| Órdenes, estados e historial | 19/10 al 28/10 | Desarrollo del circuito principal de reparación, estados y trazabilidad de órdenes |
| Frontend, integración y dashboard | 29/10 al 03/11 | Integración de interfaces con la API REST y visualización de información general del taller |
| Pruebas y correcciones | 04/11 al 08/11 | Validación del funcionamiento, resolución de errores y estabilización |
| Despliegue | 09/11 al 10/11 | Publicación del componente web previsto y evaluación de integración online |
| Documentación y cierre | 11/11 al 14/11 | Actualización final del repositorio, documentación y preparación de la entrega final |

La planificación podrá ajustarse de acuerdo con el avance real del proyecto y con las observaciones realizadas por la tutora, manteniendo como prioridad la finalización correcta del producto mínimo antes de incorporar funcionalidades adicionales.

## Despliegue

De acuerdo con los requisitos de la asignatura, al menos uno de los componentes principales del proyecto deberá encontrarse alojado y funcionando en un servicio online.

Para cumplir con este requisito se prevé publicar el frontend web mediante **Vercel**.

El backend y la base de datos se desarrollarán inicialmente en un entorno local. Durante el desarrollo se evaluará también el despliegue del backend con el objetivo de disponer, si resulta técnicamente viable dentro del alcance previsto, de una versión completamente integrada y accesible en línea.

Esta evaluación se realizará sin comprometer el desarrollo del producto mínimo ni incorporar infraestructura innecesariamente compleja.

## Integrantes

**Augusto Matías Cúneo Brouwer de Koning**
**Nicolás Azcuy**

**Tutora:** Prof. María Candela Grosso

## Estado actual

El proyecto se encuentra actualmente en etapa de **análisis, planificación y definición de arquitectura preliminar**, correspondiente a la primera instancia de entrega.

En esta etapa se encuentran definidos la problemática, la solución propuesta, el objetivo general, el alcance inicial, el stack tecnológico, la arquitectura preliminar, el modelo conceptual inicial, la estructura del repositorio y el plan de trabajo.

La implementación del backend, frontend y modelo definitivo de datos se realizará progresivamente de acuerdo con la hoja de ruta establecida.

## Instalación y ejecución

En la instancia actual todavía no existen componentes ejecutables del sistema.

Las instrucciones de instalación, configuración y ejecución serán incorporadas en esta sección una vez inicializados los proyectos de backend y frontend y se mantendrán actualizadas durante todo el desarrollo.