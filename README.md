# Sistema Web de Gestión para Taller Mecánico

Proyecto correspondiente al **Trabajo Final de la Tecnicatura en Programación**.

## Descripción

El proyecto consiste en el desarrollo de una aplicación web destinada a organizar y centralizar la gestión de vehículos dentro de un taller mecánico.

El sistema permitirá registrar clientes y vehículos, administrar turnos, generar órdenes de trabajo, asignar mecánicos, registrar diagnósticos y trabajos realizados, realizar el seguimiento del estado de cada reparación y conservar un historial de las intervenciones efectuadas sobre cada vehículo.

## Problemática

En talleres mecánicos pequeños y medianos, parte de la información relacionada con turnos, vehículos y reparaciones puede encontrarse distribuida entre diferentes medios, como agendas, registros en papel, planillas de cálculo o aplicaciones de mensajería.

Cuando esta información no se encuentra centralizada, puede resultar difícil realizar el seguimiento de un vehículo durante su permanencia en el taller y consultar posteriormente las reparaciones efectuadas.

## Objetivo

Desarrollar una aplicación web que permita gestionar el proceso de atención de un vehículo dentro de un taller mecánico, desde la asignación de un turno hasta su entrega, centralizando la información relacionada con clientes, vehículos, órdenes de trabajo, diagnósticos e intervenciones realizadas.

## Funcionalidades principales

El producto mínimo previsto incluye:

* Gestión de clientes.
* Gestión de vehículos.
* Gestión de mecánicos.
* Gestión de turnos.
* Generación y seguimiento de órdenes de trabajo.
* Registro de diagnósticos.
* Registro de trabajos realizados.
* Seguimiento de estados de reparación.
* Historial de intervenciones por vehículo.
* Dashboard básico de información del taller.

## Tecnologías

### Backend

* Java 17
* Spring Boot
* Spring Data JPA / Hibernate
* Maven

### Frontend

* HTML
* CSS
* TypeScript
* Vite

### Base de datos

* H2

### Comunicación

* API REST
* JSON
* Fetch API

### Herramientas

* Git
* GitHub
* Postman

### Despliegue previsto

* Vercel para el frontend web.

## Arquitectura preliminar

El sistema utilizará una arquitectura cliente-servidor.

```text
Frontend
HTML + CSS + TypeScript + Vite
              ↓
          API REST
              ↓
     Java + Spring Boot
              ↓
   Spring Data JPA / Hibernate
              ↓
              H2
```

El backend se organizará mediante las capas **Controller, Service y Repository**, separando la recepción de solicitudes, la lógica de negocio y el acceso a los datos.

## Estructura del repositorio

```text
tf-gestion-taller-mecanico/
│
├── backend/
├── frontend/
├── database/
├── docs/
│   └── entrega-1/
├── .gitignore
└── README.md
```

* `backend/`: código correspondiente a la API y lógica de negocio.
* `frontend/`: interfaz web del sistema.
* `database/`: documentación y recursos asociados al modelo de datos.
* `docs/`: documentación y entregas del proyecto.

## Integrantes

* Augusto Matías Cúneo Brouwer de Koning
* Nicolás Azcuy

## Tutora

**Prof. María Candela Grosso**

## Estado actual

El proyecto se encuentra en etapa de análisis, planificación y definición de arquitectura preliminar.

La implementación del backend, frontend y modelo definitivo de base de datos se realizará de manera progresiva según la hoja de ruta del proyecto.

## Instalación y ejecución

Las instrucciones de instalación, configuración y ejecución serán incorporadas a este README cuando se encuentren inicializados los componentes ejecutables del proyecto.
