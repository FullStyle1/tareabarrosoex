# Microservicio de parcelas

Base del proyecto para el sistema de huertos urbanos. Incluye una aplicación Spring Boot vacía y PostgreSQL; todavía no implementa la entidad `Parcel`, endpoints, validaciones, sanitización ni excepciones del dominio.

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
    │   │   └── ParcelServiceApplication.java
    │   └── resources/application.properties
    └── test/
        ├── java/com/barroso/parcelservice/
        │   └── ParcelServiceApplicationTests.java
        └── resources/application.properties
compose.yaml
```

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

La prueba de contexto usa H2 en memoria; PostgreSQL se mantiene como base de datos de ejecución.

## Configuración

Las credenciales de desarrollo se pueden cambiar en `.env`. No se deben usar estos valores de ejemplo en producción. La configuración JPA no crea ni modifica tablas automáticamente; el esquema se definirá al agregar el modelo de dominio.
