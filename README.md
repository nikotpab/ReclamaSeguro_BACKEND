# ReclamaSeguro - Backend

## Descripción General

Este repositorio aloja el servidor y la lógica de negocio de la plataforma ReclamaSeguro. Su función principal es actuar como intermediario seguro entre la interfaz de usuario (la página web) y la base de datos, garantizando que la información de los clientes se procese correctamente.

El sistema está diseñado para recibir las solicitudes de consulta de seguros, validar la integridad de los datos ingresados y almacenarlos para su posterior gestión operativa.

## Funcionalidades Principales

El sistema se encarga de las siguientes tareas automáticas:

1. **Recepción de Datos:** Captura la información enviada desde el formulario web (Nombre, Correo Electrónico y Teléfono).
2. **Validación de Seguridad:** Verifica que los datos recibidos sean reales y cumplan con los formatos requeridos antes de procesarlos.
3. **Almacenamiento:** Guarda la información de los prospectos en la base de datos de manera organizada.
4. **Conexión API:** Provee los puntos de acceso necesarios para que la página web pueda enviar y consultar información en tiempo real.

## Tecnologías Utilizadas

El proyecto está construido sobre estándares de la industria para garantizar estabilidad y seguridad:

* **Lenguaje:** Java
* **Framework:** Spring Boot (para la gestión del servidor)
* **Gestión de Dependencias:** Maven
* **Base de Datos:** Configurable (H2 para pruebas / PostgreSQL para producción)

## Guía de Inicio Rápido

Instrucciones para ejecutar el servidor en un entorno local de desarrollo.

### Requisitos Previos

* Tener instalado Java JDK (versión 17 o superior).
* Tener instalado Maven.
* Cliente Git.

### Instalación y Ejecución

1. Clonar el repositorio en su máquina local:
   git clone https://github.com/nikotpab/ReclamaSeguro_BACKEND.git

2. Navegar a la carpeta del proyecto:
   cd ReclamaSeguro_BACKEND

3. Construir e instalar las dependencias:
   mvn clean install

4. Iniciar el servidor:
   mvn spring-boot:run

Una vez iniciado, el sistema estará disponible localmente en el puerto 8080.

## Contacto y Soporte

Este proyecto es de carácter privado y su acceso está restringido a los desarrolladores autorizados. Para consultas sobre la implementación o reporte de errores, favor contactar al propietario del repositorio.

---
2024 ReclamaSeguro
