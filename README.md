# MiTaller

**Sistema Web de Gestión y Trazabilidad para Taller Mecánico**

Proyecto desarrollado como Trabajo Final de la **Tecnicatura Universitaria en Programación a Distancia de la Universidad Tecnológica Nacional**.

## Sobre el proyecto

MiTaller es una aplicación web pensada para talleres mecánicos pequeños y medianos que necesitan centralizar la información de sus clientes, vehículos y reparaciones.

El proyecto parte de un problema concreto: la información de una intervención puede quedar distribuida entre anotaciones, planillas, mensajes y distintos registros, lo que dificulta conocer el estado actual de una reparación y recuperar posteriormente lo ocurrido.

Por eso, el eje de MiTaller es la **orden de trabajo**. Cada intervención quedará asociada a un vehículo y permitirá registrar los datos de ingreso, el mecánico responsable, el diagnóstico, los trabajos realizados, las observaciones técnicas y el estado de la reparación.

Además del estado actual, el sistema conservará los cambios producidos durante la intervención para poder reconstruir su evolución.

## Producto Mínimo Viable

El MVP comprende las funcionalidades necesarias para gestionar una intervención desde el ingreso del vehículo hasta su entrega.

Incluye:

- usuarios y roles;
- clientes;
- vehículos;
- órdenes de trabajo;
- asignación de mecánico responsable;
- diagnóstico;
- trabajos realizados;
- observaciones técnicas;
- estados de reparación;
- historial de cambios de estado;
- consulta de intervenciones anteriores de cada vehículo.

La **gestión de turnos** y el **dashboard operativo** quedan como funcionalidades complementarias y no condicionan el funcionamiento del MVP.

Se implementarán únicamente si el circuito principal se encuentra completo, probado y estable.

## Roles

MiTaller contempla inicialmente dos roles funcionales.

### Recepción / Administración

Será responsable de:

- registrar, consultar y modificar clientes;
- registrar, consultar y modificar vehículos;
- crear órdenes de trabajo;
- cargar los datos iniciales de ingreso;
- asignar el mecánico responsable;
- consultar las órdenes y su estado;
- consultar el historial de intervenciones de los vehículos;
- registrar la entrega del vehículo.

### Mecánico

Trabajará sobre las órdenes que tenga asignadas y podrá:

- consultar sus órdenes;
- registrar el diagnóstico;
- cargar los trabajos realizados;
- incorporar observaciones técnicas;
- actualizar los estados correspondientes al proceso de reparación.

No se contempla un acceso para clientes dentro del MVP.

## Flujo de una orden de trabajo

Toda orden comienza en estado **RECIBIDO**.

El flujo normal previsto es:

RECIBIDO  
↓  
EN DIAGNÓSTICO  
↓  
EN REPARACIÓN  
↓  
CONTROL FINAL  
↓  
FINALIZADO  
↓  
ENTREGADO

También existe el estado temporal **ESPERANDO REPUESTO**.

Una orden podrá pasar a **ESPERANDO REPUESTO** desde **EN DIAGNÓSTICO** o desde **EN REPARACIÓN** cuando la intervención no pueda continuar por falta de un componente.

Una vez disponible el repuesto, la orden continuará en **EN REPARACIÓN**.

Si durante **CONTROL FINAL** se detectan nuevas tareas o correcciones, la orden podrá volver a **EN REPARACIÓN**.

Las transiciones previstas para el MVP son:

| Estado de origen | Estado de destino | Responsable |
|---|---|---|
| RECIBIDO | EN DIAGNÓSTICO | Mecánico |
| EN DIAGNÓSTICO | EN REPARACIÓN | Mecánico |
| EN DIAGNÓSTICO | ESPERANDO REPUESTO | Mecánico |
| EN REPARACIÓN | ESPERANDO REPUESTO | Mecánico |
| ESPERANDO REPUESTO | EN REPARACIÓN | Mecánico |
| EN REPARACIÓN | CONTROL FINAL | Mecánico |
| CONTROL FINAL | EN REPARACIÓN | Mecánico |
| CONTROL FINAL | FINALIZADO | Mecánico |
| FINALIZADO | ENTREGADO | Recepción / Administración |

Las transiciones estarán controladas por la lógica de negocio y no podrán realizarse de forma arbitraria.

Cada cambio de estado conservará:

- estado anterior;
- nuevo estado;
- fecha y hora;
- usuario responsable.

El estado **ENTREGADO** será terminal dentro del circuito previsto para el MVP.

## Modelo conceptual

Para el MVP se definieron seis entidades principales:

- Cliente
- Vehículo
- Usuario
- Orden de Trabajo
- Detalle de Trabajo
- Historial de Estado de la Orden

La **Orden de Trabajo** es la entidad central del sistema.

Un cliente puede tener varios vehículos y un vehículo puede registrar distintas órdenes de trabajo a lo largo del tiempo.

Una orden podrá crearse inicialmente sin mecánico asignado mientras permanezca en estado **RECIBIDO**, pero deberá tener un mecánico responsable antes de pasar a **EN DIAGNÓSTICO**.

El diagnóstico y el estado actual forman parte de la Orden de Trabajo.

Los trabajos realizados se registrarán mediante Detalles de Trabajo y los cambios de estado mediante el Historial de Estado de la Orden.

El historial general de un vehículo no requiere una entidad propia, ya que se obtendrá consultando cronológicamente sus órdenes de trabajo.

La entidad Turno podrá incorporarse posteriormente si se desarrolla esa funcionalidad complementaria.

## Dashboard

El dashboard será una funcionalidad complementaria destinada principalmente a **Recepción / Administración**.

En caso de implementarse, podrá mostrar:

- cantidad de órdenes activas;
- distribución de órdenes por estado;
- vehículos actualmente en reparación;
- órdenes en **ESPERANDO REPUESTO**;
- órdenes **FINALIZADAS** pendientes de entrega.

Su objetivo será ofrecer una visión rápida de la situación operativa del taller.

El dashboard no forma parte de los requisitos indispensables del MVP.

## Tecnologías

| Componente | Tecnología |
|---|---|
| Backend | Java 17 |
| Framework backend | Spring Boot |
| Persistencia | Spring Data JPA / Hibernate |
| Gestión del proyecto Java | Maven |
| Base de datos | H2 |
| Frontend | HTML, CSS y TypeScript |
| Herramienta frontend | Vite |
| Comunicación | API REST mediante HTTP y JSON |
| Consumo de API | Fetch API |
| Pruebas de API | Postman |
| Control de versiones | Git y GitHub |
| Despliegue previsto | Vercel para el frontend |

Durante el desarrollo local se utilizará H2 en modo archivo para conservar la información entre ejecuciones.

## Arquitectura

MiTaller utilizará una arquitectura cliente-servidor.

El frontend estará desarrollado con **HTML, CSS, TypeScript y Vite** y se comunicará mediante HTTP y JSON con una **API REST** desarrollada con **Java 17 y Spring Boot**.

El backend se organizará mediante las capas:

**Controller → Service → Repository → H2**

La capa **Controller** recibirá las solicitudes y expondrá los endpoints de la API.

La capa **Service** concentrará la lógica de negocio, incluyendo las reglas correspondientes a los roles y las transiciones permitidas entre estados.

La capa **Repository** gestionará el acceso a los datos mediante Spring Data JPA.

## Estructura del repositorio

Todo el proyecto se desarrolla dentro de un único repositorio de GitHub.

La estructura principal es:

- **backend/** — API REST y lógica de negocio.
- **frontend/** — interfaz web.
- **database/** — documentación y archivos relacionados con el modelo de datos.
- **docs/** — documentación correspondiente a las distintas entregas.
  - **entrega-1/**
  - **entrega-2/**
  - **entrega-final/**
- **.gitignore**
- **README.md**

La estructura podrá ampliarse a medida que avance la implementación.

## Organización del trabajo

El proyecto utiliza **Git y GitHub** para el control de versiones y la centralización del código y la documentación.

Las tareas se registran mediante Issues y se organizan con GitHub Projects utilizando el flujo:

**Backlog → To Do → In Progress → Review → Done**

El desarrollo será compartido entre ambos integrantes para que los dos conozcan la arquitectura, el modelo de datos, las reglas de negocio y las decisiones técnicas adoptadas.

## Plan de trabajo

| Etapa | Período |
|---|---|
| Revisión de propuesta y definición del MVP | 31/08 al 07/09 |
| Modelo conceptual y arquitectura | 08/09 al 14/09 |
| Modelo relacional y definición de módulos | 15/09 al 27/09 |
| Inicialización técnica | 28/09 al 04/10 |
| Usuarios, clientes y vehículos | 05/10 al 11/10 |
| Órdenes de trabajo y trazabilidad | 12/10 al 25/10 |
| Frontend e integración | 26/10 al 03/11 |
| Pruebas y correcciones | 04/11 al 08/11 |
| Despliegue | 09/11 al 10/11 |
| Documentación y cierre | 11/11 al 14/11 |

Turnos y dashboard quedan fuera de la ruta crítica del MVP.

## Despliegue

Como parte de los requisitos del Trabajo Final, al menos uno de los componentes principales deberá encontrarse disponible online.

Se prevé desplegar el **frontend mediante Vercel**.

Durante las primeras etapas, el backend y la base de datos funcionarán localmente.

Posteriormente podrá evaluarse también el despliegue del backend si resulta viable sin agregar complejidad innecesaria al proyecto.

## Documentación

La documentación correspondiente a la primera entrega se encuentra en:

**docs/entrega-1/**

El documento principal es:

**Trabajo_Final_Primera_Entrega.pdf**

La documentación se irá actualizando durante las distintas etapas del proyecto.

## Estado actual

El proyecto se encuentra en etapa de definición del MVP, arquitectura y modelo de datos.

Actualmente se encuentran definidos:

- problemática y propuesta de solución;
- alcance;
- Producto Mínimo Viable;
- roles y responsabilidades;
- circuito de órdenes de trabajo;
- estados y transiciones;
- modelo conceptual preliminar;
- tecnologías;
- arquitectura;
- estructura del repositorio;
- planificación del desarrollo.

La implementación del backend y frontend se realizará progresivamente de acuerdo con el plan de trabajo.

## Integrantes

**Augusto Matías Cúneo Brouwer de Koning**  
**Nicolás Azcuy**

**Tutora:** Prof. María Candela Grosso

## Instalación y ejecución

El proyecto todavía se encuentra en etapa de diseño y planificación, por lo que actualmente no existen componentes ejecutables.