# API — Endpoints

Base: `http://localhost:8080` · Prefijo: `/api/v1` · Formato: JSON

Errores en [RFC 9457 Problem Details](https://www.rfc-editor.org/rfc/rfc9457).

Documento espejo del doc **API — Endpoints** en Linear.

---

## Properties

### `GET /api/v1/properties`

Listado paginado de avisos activos (`deletedAt` null).

| Param | Tipo | Default | Descripción |
|-------|------|---------|-------------|
| `page` | int | `0` | Página (≥ 0) |
| `size` | int | `20` | Tamaño (1–100) |
| `city` | string | — | Filtro exacto case-insensitive |
| `type` | enum | — | Ver PropertyType |
| `operation` | enum | — | Ver OperationType |
| `status` | enum | — | Ver PropertyStatus |
| `minPrice` | decimal | — | ≥ 0 |
| `maxPrice` | decimal | — | ≥ 0 |

**200** — `PageResponse<PropertyResponse>`

**400** — si `minPrice > maxPrice` o parámetros inválidos.

Orden: `createdAt DESC`.

---

### `POST /api/v1/properties`

Crea aviso en estado `DRAFT`.

**Body** — `CreatePropertyRequest` (campos opcionales):

```json
{
  "title": "Depto 2 amb en Palermo",
  "description": "Luminoso, balcón...",
  "type": "APARTMENT",
  "operation": "RENT",
  "price": 450000,
  "currency": "ARS",
  "address": "Av. Santa Fe 1234",
  "city": "Buenos Aires",
  "province": "CABA",
  "bedrooms": 1,
  "bathrooms": 1,
  "coveredArea": 45.5,
  "totalArea": 50.0
}
```

**201** — `PropertyResponse` + `Location: /api/v1/properties/{id}`

---

### `GET /api/v1/properties/{id}`

**200** — `PropertyResponse`

**404** — no existe o borrado.

---

### `PATCH /api/v1/properties/{id}`

Actualiza únicamente los campos no nulos enviados en el body. Los campos omitidos conservan su valor actual.

**Body** — `UpdatePropertyRequest` (campos opcionales):

```json
{
  "price": 150000,
  "description": "Departamento renovado con balcón"
}
```

Los campos editables son los mismos de `CreatePropertyRequest`. El estado se modifica mediante los endpoints específicos de publicación y pausa.

**200** — `PropertyResponse`

**400** — body o valores inválidos.

**404** — no existe o está borrado.

---

### `DELETE /api/v1/properties/{id}`

Soft delete.

**204** — sin body.

**404** — no existe o ya borrado.

---

## PropertyResponse

```json
{
  "id": 1,
  "title": "Depto 2 amb en Palermo",
  "description": "...",
  "type": "APARTMENT",
  "operation": "RENT",
  "price": 450000,
  "currency": "ARS",
  "address": "Av. Santa Fe 1234",
  "city": "Buenos Aires",
  "province": "CABA",
  "bedrooms": 1,
  "bathrooms": 1,
  "coveredArea": 45.5,
  "totalArea": 50.0,
  "status": "DRAFT",
  "createdAt": "2026-08-24T18:00:00Z",
  "updatedAt": "2026-08-24T18:00:00Z"
}
```

---

## Enums

**PropertyType:** `APARTMENT` · `HOUSE` · `LAND` · `COMMERCIAL` · `OTHER`

**OperationType:** `SALE` · `RENT` · `TEMPORARY_RENT`

**PropertyStatus:** `DRAFT` · `PUBLISHED` · `PAUSED`

---

## Planificados

| Método | Ruta | Descripción |
|--------|------|-------------|
| `POST` | `/properties/{id}/publish` | DRAFT → PUBLISHED |
| `POST` | `/properties/{id}/pause` | PUBLISHED → PAUSED |
| `POST` | `/auth/register` | Alta usuario |
| `POST` | `/auth/login` | JWT |
| `GET` | `/me/properties` | Mis avisos |
| `POST` | `/properties/{id}/images` | Subir foto |
| `POST` | `/properties/{id}/inquiries` | Enviar consulta |
| `GET` | `/me/inquiries` | Consultas recibidas |

---

## Ejemplos

```bash
curl "http://localhost:8080/api/v1/properties?city=rosario&operation=SALE&page=0&size=10"

curl -X POST http://localhost:8080/api/v1/properties \
  -H "Content-Type: application/json" \
  -d '{"title":"Loft","type":"APARTMENT","operation":"SALE","price":95000,"currency":"USD","city":"Rosario"}'

curl http://localhost:8080/api/v1/properties/1

curl -X PATCH http://localhost:8080/api/v1/properties/1 \
  -H "Content-Type: application/json" \
  -d '{"price":150000,"description":"Departamento renovado con balcón"}'

curl -X DELETE http://localhost:8080/api/v1/properties/1
```
