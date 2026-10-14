# Microservicio de parcelas

Base del proyecto para el sistema de huertos urbanos. Incluye la entidad de persistencia `Parcel`, un repositorio JPA, un DTO de solicitud con validación y sanitización, y manejo global de errores. Los endpoints de negocio todavía no están implementados.

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
        ├── java/com/barroso/parcelservice/dto/ParcelRequestDTOTest.java
        ├── java/com/barroso/parcelservice/repository/ParcelRepositoryTest.java
        └── resources/application.properties
compose.yaml
```

La entidad y el repositorio se encuentran en `model/Parcel.java` y `repository/ParcelRepository.java`. `model/SoilType.java` limita el tipo de suelo a `ARCILLOSO`, `LIMOSO` o `ARENOSO`; `dto/ParcelRequestDTO.java` valida la entrada y limpia sus campos de texto. `exception/GlobalExceptionHandler.java` centraliza respuestas JSON para validaciones, recursos inexistentes, errores de negocio, solicitudes mal formadas y errores inesperados.

## Ejecutar con Docker Compose

Se necesita Docker Desktop con Compose. Desde la carpeta raíz:

```powershell
Copy-Item .env.example .env
docker compose up --build
```

La aplicación queda en `http://localhost:8080` y PostgreSQL en `localhost:5432`. No hay rutas HTTP implementadas aún. Para detener los contenedores:

```powershell
docker compose down
```

Los datos de PostgreSQL persisten en el volumen `postgres_data`. Para eliminar también ese volumen de desarrollo:

```powershell
docker compose down --volumes
```

## Ejecutar y probar solo el backend

Requiere PostgreSQL local disponible con los parámetros de `.env.example` o las variables `DB_URL`, `DB_USERNAME` y `DB_PASSWORD` configuradas.

```powershell
cd backend
.\mvnw.cmd spring-boot:run
.\mvnw.cmd test
```

Las pruebas usan H2 en memoria; PostgreSQL se mantiene como base de datos de ejecución. El esquema de pruebas se crea y elimina automáticamente y la configuración normal no modifica el esquema de PostgreSQL.

## Configuración

Las credenciales de desarrollo se pueden cambiar en `.env`. No se deben usar estos valores de ejemplo en producción. La configuración JPA no crea ni modifica tablas automáticamente; el esquema se definirá al agregar el modelo de dominio.
