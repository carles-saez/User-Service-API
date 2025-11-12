# User Service API

API REST construida con Spring Boot para gestionar el ciclo de vida de usuarios (creación, consulta, actualización y eliminación). El servicio aplica validaciones, cifrado de credenciales y filtros dinámicos para ofrecer una capa de persistencia robusta respaldada por PostgreSQL.

## Características principales
- CRUD completo de usuarios expuesto bajo `/api/users` con operaciones para creación, lectura, actualización total y parcial, y eliminación.
- Validaciones declarativas en DTOs de entrada para garantizar emails únicos, contraseñas seguras y datos consistentes.
- Cifrado de contraseñas mediante `BCryptPasswordEncoder` antes de persistir en base de datos.
- Especificaciones JPA dinámicas para combinar múltiples filtros de búsqueda sin modificar el repositorio.
- Cobertura de pruebas con tests de integración sobre el controlador, repositorio y servicio para asegurar el comportamiento extremo a extremo.

## Stack tecnológico
- Java 21
- Spring Boot 3.5.7 (Web, Data JPA, Validation)
- PostgreSQL (tiempo de ejecución) / H2 (tests)
- Spring Security Crypto para hashing de contraseñas
- MapStruct para mapeos entre capas
- Springdoc OpenAPI para documentación interactiva
- Maven como herramienta de build

## Arquitectura
El proyecto sigue un enfoque hexagonal/por capas, separando responsabilidades en paquetes:

| Capa | Descripción | Paquetes relevantes |
| --- | --- | --- |
| Presentación | Expone los endpoints REST y traduce peticiones HTTP a comandos de aplicación. | `infrastructure.controller` → `UserController`|
| Aplicación | Contiene la lógica orquestadora, validaciones adicionales y manejo de excepciones específicas del dominio. | `application.service.UserService`|
| Dominio | Modela entidades y contratos (interfaces del repositorio) agnósticos de infraestructura. | `domain.model.User` |
| Infraestructura | Implementa la persistencia, entidades JPA, especificaciones y configuración técnica. | `infrastructure.entity.UserEntity`|

## Requisitos previos
- Java 21 (JDK)
- Maven >= 3.8.7
- PostgreSQL 16+ (opcional si se usa una instancia externa)
- Acceso a variables de entorno para credenciales de base de datos

## Configuración
La aplicación toma la configuración desde `application.properties`, con variables de entorno para la base de datos.

| Variable | Descripción | Ejemplo |
| --- | --- | --- |
| `DB_URL` | Cadena de conexión JDBC hacia PostgreSQL | `jdbc:postgresql://localhost:5432/users_db`|
| `DB_USER` | Usuario con permisos de lectura/escritura | `user_example`|
| `DB_PASSWORD` | Contraseña del usuario definido | `password_example`|

Si prefieres ejecutar con una base en memoria durante el desarrollo, puedes emplear H2 sustituyendo la URL por `jdbc:h2:mem:usersdb` y añadiendo el driver correspondiente en `application.properties`.

## Puesta en marcha local
1. Exporta las variables de entorno anteriores.
2. Ejecuta la aplicación con Maven:
   ```bash
   mvn spring-boot:run
   ```
3. El servicio quedará disponible en `http://localhost:8080`.

### Ejecución de pruebas
```bash
mvn test
```
Las pruebas incluyen escenarios de integración completos sobre el stack web y la capa de persistencia.

## Documentación de la API
- El contrato OpenAPI se encuentra en `src/main/resources/static/openapi.yaml`.
- Springdoc expone la interfaz interactiva en `http://localhost:8080/swagger-ui.html` una vez levantado el servicio.

## Estructura del proyecto
```
users-api/
├── pom.xml
├── README.md
├── src
│   ├── main
│   │   ├── java/com/futurasmus/users_api/
│   │   │   ├── application/
│   │   │   │   ├── dto/
│   │   │   │   └── service/
│   │   │   ├── common/
│   │   │   │   ├── exception/
│   │   │   │   └── mapper/
│   │   │   ├── domain/
│   │   │   │   ├── model/
│   │   │   │   └── repository/
│   │   │   └── infrastructure/
│   │   │       ├── config/
│   │   │       ├── controller/
│   │   │       ├── entity/
│   │   │       └── repository/
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/openapi.yaml
│   └── test/java/com/futurasmus/users_api/
│       ├── application/service
│       ├── common/mapper
│       ├── integration
│       └── util
└── target/
```

## Buenas prácticas y decisiones destacadas
- Uso de DTOs separados para creación, actualización completa y parcial para delimitar los datos expuestos.
- MapStruct automatiza conversiones entre entidades JPA y objetos de dominio, reduciendo código repetitivo y errores manuales.
- Filtros dinámicos con Specifications habilitan combinaciones complejas sin proliferación de métodos específicos en repositorios.
- Pruebas de integración que cubren rutas felices y escenarios de error habituales para garantizar estabilidad ante regresiones.

