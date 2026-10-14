# Microservicio de parcelas

Microservicio de parcelas para el sistema de huertos urbanos. Incluye persistencia JPA, validación y sanitización de entradas, manejo global de errores y API REST para registrar y consultar parcelas.

## Tecnologías

- Java 17+
- Spring Boot 3.3.5
- Spring Web
- Spring Data JPA
- Spring Validation
- PostgreSQL 16
- Maven
- Docker Compose

## Estructura

```text
backend/
├── Dockerfile
├── mvnw
├── mvnw.cmd
├── .mvn/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/barroso/parcelservice/
    │   │   ├── ParcelServiceApplication.java
    │   │   ├── dto/ParcelRequestDTO.java
    │   │       ├── dto/ErrorResponseDto.java
    │   │       ├── dto/FieldErrorDto.java
    │   │       ├── exception/BadRequestException.java
    │   │       ├── exception/GlobalExceptionHandler.java
    │   │       ├── exception/ResourceNotFoundException.java
    │   │       ├── model/Parcel.java
    │   │   ├── model/SoilType.java
    │   │   └── repository/ParcelRepository.java
    │   └── resources/application.properties
    └── test/
        ├── java/com/barroso/parcelservice/ParcelServiceApplicationTests.java
        ├── java/com/barroso/parcelservice/controller/ParcelControllerTest.java
        ├── java/com/barroso/parcelservice/dto/ParcelRequestDTOTest.java
        ├── java/com/barroso/parcelservice/exception/GlobalExceptionHandlerTest.java
        ├── java/com/barroso/parcelservice/repository/ParcelRepositoryTest.java
        ├── java/com/barroso/parcelservice/service/ParcelServiceImplTest.java
        └── resources/application.properties
compose.yaml
```

`ParcelService` mapea las solicitudes a entidades y las respuestas a DTOs. `ParcelController` expone los endpoints indicados abajo. `model/SoilType.java` limita el tipo de suelo a `ARCILLOSO`, `LIMOSO` o `ARENOSO`; `dto/ParcelRequestDTO.java` valida la entrada y limpia sus campos de texto. `exception/GlobalExceptionHandler.java` centraliza respuestas JSON para validaciones, recursos inexistentes, errores de negocio, solicitudes mal formadas y errores inesperados.

## API REST

- `POST /api/v1/parcels`: registra una parcela validada; devuelve `201 Created`, el recurso y el encabezado `Location`.
- `GET /api/v1/parcels`: devuelve las parcelas registradas.
- `GET /api/v1/parcels/{id}`: devuelve el detalle de una parcela; devuelve `404 Not Found` si no existe.

## Ejecutar con Docker Compose

Se necesita Docker Desktop con Compose. Desde la carpeta raíz:

```powershell
Copy-Item .env.example .env
docker compose up --build
```

La aplicación queda en `http://localhost:8080` y PostgreSQL en `localhost:5432`. Flyway crea la tabla `parcels` al iniciar. Para detener los contenedores:

```powershell
docker compose down
```

Los datos de PostgreSQL persisten en el volumen `postgres_data`. Para eliminar también ese volumen de desarrollo:

```powershell
docker compose down --volumes
```

## Ejecutar y probar

Requiere PostgreSQL local disponible con los parámetros de `.env.example` o las variables `DB_URL`, `DB_USERNAME` y `DB_PASSWORD` configuradas.

```powershell
cd backend
.\mvnw.cmd spring-boot:run
.\mvnw.cmd test
```

Las pruebas usan H2 en memoria e incluyen solicitudes HTTP a la aplicación completa: creación válida (`201`), entrada inválida (`400`), tipo de suelo desconocido (`400`), listado, detalle y parcela inexistente (`404`). Flyway administra el esquema; Hibernate no modifica automáticamente el esquema de PostgreSQL.

## Configuración

Las credenciales de desarrollo se pueden cambiar en `.env`. No se deben usar estos valores de ejemplo en producción.
