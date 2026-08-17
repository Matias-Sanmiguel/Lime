# Lime

API de marketplace construida con Java 21, Spring Boot y SQLite.

## Ejecutar con Docker

La aplicacion y su base SQLite se administran con Docker Compose:

```bash
docker compose up --build
```

La API queda disponible en `http://localhost:8080`.

El archivo SQLite se guarda en el volumen administrado por Docker `lime-data`, montado dentro del contenedor en `/app/data`. Detener o recrear el contenedor no elimina los datos.

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

La variable `DATABASE_PATH` define la ubicacion del archivo SQLite. Compose la configura como:

```text
/app/data/lime.db
```

Fuera de Docker, el valor predeterminado es `./data/lime.db`.

## Endpoints

```text
GET    /api/v1/properties
POST   /api/v1/properties
GET    /api/v1/properties/{id}
DELETE /api/v1/properties/{id}
```
