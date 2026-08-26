# Lime

Marketplace inmobiliario fullstack estilo **Argenprop**: publicar, buscar y consultar propiedades en venta o alquiler.

| Capa | Stack |
|------|-------|
| Backend | Java 21 · Spring Boot 4.1 · JPA |
| Frontend | React · Vite · TypeScript *(próximo)* |
| Base de datos | SQL Server 2022 |

**Gestión del proyecto:** [Linear — Lime Project](https://linear.app/matias-sanmiguel/project/lime-project-26b8aba9d809)

## Qué hace

- **Visitantes** buscan avisos por ciudad, tipo, operación y precio.
- **Publicadores** (particulares o inmobiliarias) crean avisos, los publican y reciben consultas.
- **API REST** en `/api/v1` + **frontend React** para la experiencia web.

## Estructura del repo

```text
Lime/
├── src/                 # API Spring Boot
├── frontend/            # React (a crear — ver Linear LIM-5)
├── docs/
│   ├── brand-identity.md
│   ├── brand/           # Isotipo, lockup, preview Editorial Lime
│   └── endpoints.md     # Referencia API
├── compose.yaml         # SQL Server + API
├── Dockerfile
└── pom.xml
```

## Quick start (Docker)

```bash
docker compose up --build
```

Levanta tres servicios:

| Servicio | Rol |
|----------|-----|
| `db` | SQL Server 2022 (volumen `lime-sqlserver-data`) |
| `db-init` | Crea la base `lime` si no existe |
| `api` | Spring Boot en `http://localhost:8080` |

```bash
docker compose logs -f api    # logs
docker compose down           # parar (conserva datos)
docker compose down --volumes # parar y borrar datos
```

## Desarrollo local (API en host, DB en Docker)

Levantá SQL Server y creá la base `lime`:

```bash
docker compose up db db-init -d
```

Corré la API apuntando a `localhost:1433`:

```bash
./mvnw spring-boot:run
```

La contraseña de `sa` se toma de `MSSQL_SA_PASSWORD` (default `Lime_SA_2026!`), la misma que usa Compose.

Maven necesita **JDK 21**. Si `JAVA_HOME` apunta a otra versión (o a un path roto), `./mvnw` falla. En Arch: `export JAVA_HOME=/usr/lib/jvm/java-21-openjdk`.

Frontend *(cuando exista)*:

```bash
cd frontend
npm install
npm run dev    # http://localhost:5173
```

`.env` del frontend:

```text
VITE_API_URL=http://localhost:8080
```

## Configuración

`application.properties` apunta a SQL Server en `localhost:1433`, base `lime`, usuario `sa`.

En Docker Compose, el servicio `api` sobreescribe el host a `db` vía `SPRING_DATASOURCE_*`. La contraseña de `sa` viene de `MSSQL_SA_PASSWORD` (default `Lime_SA_2026!`).

## API

Contrato v1 (20 REST + uploads): [`docs/endpoints.md`](docs/endpoints.md) y Linear (**[API — Endpoints](https://linear.app/matias-sanmiguel/document/api-endpoints-11a0bc97a8f4)**).

Implementados hoy (merge en `main`; el contrato v1 está en el doc):

```text
GET    /api/v1/properties
POST   /api/v1/properties
GET    /api/v1/properties/{id}
PATCH  /api/v1/properties/{id}
DELETE /api/v1/properties/{id}
POST   /api/v1/properties/{id}/publish
POST   /api/v1/properties/{id}/pause
POST   /api/v1/properties/{id}/images      # JSON { "url" } (no multipart)
POST   /api/v1/properties/{id}/inquiries
GET    /api/v1/me/properties               # header X-User-Id (no JWT)
```

Ejemplo:

```bash
curl "http://localhost:8080/api/v1/properties?city=buenos%20aires&operation=RENT"
```

## Estado del proyecto

| Hecho | Pendiente |
|-------|-----------|
| CRUD avisos + filtros + PATCH | Frontend React |
| Docker + SQL Server | Auth JWT (Busse) · `GET/PATCH /me` (Matías) |
| Soft delete | Inbox consultas (Nico) |
| Publish / pause | Multipart fotos + servir `/uploads` |
| POST imagen (URL) · POST consulta | Tests + Flyway + CI |
| `GET /me/properties` (`X-User-Id`) | |

Ver [Roadmap en Linear](https://linear.app/matias-sanmiguel/document/roadmap-3601501dc6ab) e issues **LIM-1** a **LIM-7**.

## Identidad

**Editorial Lime:** papel + tinta, precisión tipo macOS, lima como firma. Newsreader (títulos) + Inter (UI). Paleta Ink / Paper / Lime `#B8F34B`.

Fuente: [`docs/brand-identity.md`](docs/brand-identity.md) · preview [`docs/brand/preview.html`](docs/brand/preview.html)

## Documentación (Linear)

| Documento | Contenido |
|-----------|-----------|
| [Visión del producto](https://linear.app/matias-sanmiguel/document/vision-del-producto-80a9ad3e4929) | Usuarios, flujos, alcance MVP |
| [Arquitectura](https://linear.app/matias-sanmiguel/document/arquitectura-2fbd89a5baa9) | Capas, DB, Docker, decisiones |
| [API — Endpoints](https://linear.app/matias-sanmiguel/document/api-endpoints-11a0bc97a8f4) | Contrato REST completo |
| [Modelo de dominio](https://linear.app/matias-sanmiguel/document/modelo-de-dominio-3346925dc741) | Entidades y reglas |
| [Frontend React — Spec](https://linear.app/matias-sanmiguel/document/frontend-react-spec-e1d35daca685) | Páginas, componentes, setup |
| [Roadmap](https://linear.app/matias-sanmiguel/document/roadmap-3601501dc6ab) | Fases y prioridades |
| [Identidad Editorial Lime](docs/brand-identity.md) | Marca, tipo, paleta, tokens |
