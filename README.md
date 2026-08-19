# Lime

API de marketplace construida con Java 21, Spring Boot y SQL Server.

## Ejecutar con Docker

La aplicacion y su base SQL Server se administran con Docker Compose:

```bash
docker compose up --build
```

Esto levanta tres servicios: `db` (SQL Server), `db-init` (crea la base `lime` si no existe) y `api` (la aplicacion Spring Boot). La API queda disponible en `http://localhost:8080`.

Los datos de SQL Server se guardan en el volumen administrado por Docker `lime-sqlserver-data`, montado dentro del contenedor en `/var/opt/mssql`. Detener o recrear el contenedor no elimina los datos.

```bash
docker compose down
docker compose up -d
```

Para seguir los logs:

```bash
docker compose logs -f api
```

Para detener la aplicacion conservando la base:

```bash
docker compose down
```

Para eliminar tambien los datos persistidos:

```bash
docker compose down --volumes
```

## Configuracion

Compose configura la conexion a SQL Server mediante variables de entorno (`SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, etc.), definidas en `compose.yaml`. La contrasena del usuario `sa` se toma de la variable `MSSQL_SA_PASSWORD` (por defecto `Lime_SA_2026!`).

Fuera de Docker (por ejemplo corriendo `./mvnw spring-boot:run` localmente sin setear esas variables), `application.properties` cae a un archivo SQLite local (`./data/lime.db` por defecto, configurable con `DATABASE_PATH`).

## Endpoints

```text
GET    /api/v1/properties
POST   /api/v1/properties
GET    /api/v1/properties/{id}
DELETE /api/v1/properties/{id}
```
