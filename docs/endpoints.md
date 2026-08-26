# API — Endpoints

Base: `http://localhost:8080` · Prefijo: `/api/v1` · JSON · Errores: [RFC 9457 Problem Details](https://www.rfc-editor.org/rfc/rfc9457).

Auth: header `Authorization: Bearer <jwt>` cuando la fila dice JWT.

Documento espejo: **[API — Endpoints](https://linear.app/matias-sanmiguel/document/api-endpoints-11a0bc97a8f4)** en Linear.

Actualizado: 26 ago 2026. Contrato v1: **20 REST + `GET /uploads/**`**. El detalle de cada ruta es el **objetivo**. La tabla dice qué hay mergeado en `main` hoy.

---

## Inventario

| # | Estado | Método | Ruta | Auth hoy | Dueño | Issue |
|---|--------|--------|------|----------|-------|-------|
| 1 | Hecho | `GET` | `/properties` | Público | — | LIM-1 |
| 2 | Hecho | `POST` | `/properties` | Público (`ownerId` fijo `1`) | — / Facu | LIM-1 |
| 3 | Hecho | `GET` | `/properties/{id}` | Público (cualquier estado no borrado) | — | LIM-1 |
| 4 | Hecho | `DELETE` | `/properties/{id}` | Público | — | LIM-1 |
| 5 | Hecho | `PATCH` | `/properties/{id}` | Público | Augusto | LIM-3 |
| 6 | Hecho | `POST` | `/properties/{id}/publish` | Público | Lucas | LIM-3 |
| 7 | Hecho | `POST` | `/properties/{id}/pause` | Público | Lucas | LIM-3 |
| 8 | Pendiente | `POST` | `/auth/register` | — | Busse | LIM-2 |
| 9 | Pendiente | `POST` | `/auth/login` | — | Busse | LIM-2 |
| 10 | Pendiente | `POST` | `/auth/logout` | — | Busse | LIM-2 |
| 11 | Pendiente | `GET` | `/me` | — | Matías | LIM-2 |
| 12 | Pendiente | `PATCH` | `/me` | — | Matías | LIM-2 |
| 13 | Hecho (parcial) | `GET` | `/me/properties` | Header `X-User-Id` (no JWT) | Facu | LIM-2 |
| 14 | Hecho (parcial) | `POST` | `/properties/{id}/images` | Público · JSON `{ "url" }` | Lola | LIM-4 |
| 15 | Pendiente | `PATCH` | `/properties/{id}/images/{imageId}` | — | Lola | LIM-4 |
| 16 | Pendiente | `DELETE` | `/properties/{id}/images/{imageId}` | — | Lola | LIM-4 |
| 17 | Hecho (parcial) | `POST` | `/properties/{id}/inquiries` | Público · también en DRAFT | Micaela | LIM-4 |
| 18 | Hecho (parcial) | `GET` | `/me/inquiries` | Header `X-User-Id` (no JWT) | Nico | LIM-4 |
| 19 | Pendiente | `GET` | `/me/inquiries/{inquiryId}` | — | Nico | LIM-4 |
| 20 | Pendiente | `PATCH` | `/me/inquiries/{inquiryId}` | — | Nico | LIM-4 |
| — | Pendiente | `GET` | `/uploads/**` | — | Lola | LIM-4 |

### Gaps vs este contrato (lo que falta cerrar)

* **Busse / Matías:** no hay JWT ni `GET/PATCH /me`. Nico: inbox es lista (`X-User-Id`), sin paginación, `unreadOnly` ni detalle/PATCH.
* **Lucas:** publish no valida mínimos (`title`, `price`, `operation`, `city`). El listado público sigue mostrando DRAFT/PAUSED. No hay query `province` / `minBedrooms` / `minBathrooms`.
* **Lola:** no es multipart ni hay `sortOrder` / PATCH / DELETE / static files. `ImageResponse` es `{ id, url, createdAt }`.
* **Micaela:** no exige `PUBLISHED`. No hay `readAt` ni `propertyTitle`.
* **Facu:** `GET /me/properties` devuelve `List` (no página). Create pisa `ownerId = 1L`. Auth = `X-User-Id`.

`GET /uploads/**` no es `@RestController`: ResourceHandler de archivos. Sin esto las fotos no se ven.

### Pantallas → endpoints

| Pantalla | Endpoints |
|----------|-----------|
| Home / Buscar | `GET /properties` |
| Ficha | `GET /properties/{id}`, `POST .../inquiries`, `GET /uploads/**` |
| Login | `POST /auth/login` |
| Registro | `POST /auth/register` |
| Header / F5 | `GET /me` |
| Logout | `POST /auth/logout` |
| Perfil (nombre / inmobiliaria / password) | `PATCH /me` |
| Panel | `GET /me/properties`, publish, pause, `DELETE /properties/{id}` |
| Nuevo aviso | `POST /properties` |
| Editar aviso | `GET /properties/{id}`, `PATCH /properties/{id}`, POST/PATCH/DELETE images, publish |
| Consultas | `GET /me/inquiries`, `GET .../{id}`, `PATCH .../{id}` |

### Fuera de v1

Refresh token, cambiar email, `PUT` de aviso, resume aparte (usa `publish`), catálogo de ciudades, favoritos, similares, mapa, chat, responder consulta in-app, admin/moderación, pagos.

---

## Convenciones

* Prefijo `/api/v1`. IDs `Long`. Fechas ISO-8601 UTC.
* Listados: `PageResponse<T>`, orden default `createdAt DESC`.
* Soft delete en avisos (`deletedAt`). Nunca hard delete de properties.
* JSON bodies: `Content-Type: application/json`. Fotos: `multipart/form-data`.
* JWT `expiresIn`: `86400` (24 h). Algoritmo HS256. Password: bcrypt.

### Errores (RFC 9457)

```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "minPrice cannot be greater than maxPrice",
  "instance": "/api/v1/properties"
}
```

| Código | Cuándo |
|--------|--------|
| `400` | Validación, archivo inválido, publish sin campos mínimos |
| `401` | Sin token, token vencido/inválido, login fallido |
| `403` | Token válido pero no es el dueño |
| `404` | No existe, soft-deleted, o borrador oculto a un visitante |
| `409` | Email repetido, transición de estado inválida |

### Quién puede pegarle (después de LIM-2)

Público: `GET /properties`, `GET /properties/{id}`, `POST /auth/register`, `POST /auth/login`, `POST .../inquiries`, `GET /uploads/**`.

Todo lo demás: JWT. Mutaciones de aviso/imagen: además dueño (`403` si no).

---

## DTOs

### PageResponse

| Campo | Tipo |
|-------|------|
| `content` | `T[]` |
| `page` | int |
| `size` | int |
| `totalElements` | long |
| `totalPages` | int |

### UserResponse

| Campo | Tipo | Notas |
|-------|------|-------|
| `id` | long | |
| `email` | string | no se cambia en v1 |
| `name` | string | |
| `role` | UserRole | `USER` \| `AGENCY` \| `ADMIN` |
| `agencyName` | string \| null | sólo `AGENCY` |

### AuthResponse

| Campo | Tipo |
|-------|------|
| `token` | string |
| `tokenType` | `"Bearer"` |
| `expiresIn` | int (segundos) |
| `user` | UserResponse |

### OwnerResponse (embebe en PropertyResponse)

`id`, `name`, `role`, `agencyName`. Sin email (no filtrar el mail del publicador).

### ImageResponse

| Campo | Tipo |
|-------|------|
| `id` | long |
| `propertyId` | long |
| `url` | string (`/uploads/properties/{id}/{file}`) |
| `sortOrder` | int (`0` = principal) |

### PropertyResponse

| Campo | Tipo | Hoy | v1 |
|-------|------|-----|----|
| `id` | long | sí | sí |
| `title` | string \| null | sí | sí |
| `description` | string \| null | sí | sí |
| `type` | PropertyType \| null | sí | sí |
| `operation` | OperationType \| null | sí | sí |
| `price` | decimal \| null | sí | sí |
| `currency` | string \| null | sí | sí |
| `address` | string \| null | sí | sí |
| `city` | string \| null | sí | sí |
| `province` | string \| null | sí | sí |
| `bedrooms` | int \| null | sí | sí |
| `bathrooms` | int \| null | sí | sí |
| `coveredArea` | decimal \| null | sí | sí |
| `totalArea` | decimal \| null | sí | sí |
| `status` | PropertyStatus | sí | sí |
| `owner` | OwnerResponse \| null | no | LIM-2 |
| `images` | ImageResponse[] | no | LIM-4 (siempre array) |
| `createdAt` | datetime | sí | sí |
| `updatedAt` | datetime | sí | sí |

### CreatePropertyRequest / PatchPropertyRequest

Todos opcionales. `null` o ausente = no tocar (PATCH). En POST, ausente = null en DB.

| Campo | Tipo | Validación |
|-------|------|------------|
| `title` | string | máx. 120 |
| `description` | string | máx. 2000 |
| `type` | enum | PropertyType |
| `operation` | enum | OperationType |
| `price` | decimal | ≥ 0 |
| `currency` | string | máx. 3, uppercase al guardar |
| `address` | string | máx. 200 |
| `city` | string | máx. 100 |
| `province` | string | máx. 100 |
| `bedrooms` | int | ≥ 0 |
| `bathrooms` | int | ≥ 0 |
| `coveredArea` | decimal | ≥ 0 |
| `totalArea` | decimal | ≥ 0 |

No mandar `status` acá.

### InquiryResponse

| Campo | Tipo |
|-------|------|
| `id` | long |
| `propertyId` | long |
| `propertyTitle` | string \| null |
| `name` | string |
| `email` | string |
| `phone` | string \| null |
| `message` | string |
| `readAt` | datetime \| null |
| `createdAt` | datetime |

### Enums

**PropertyType:** `APARTMENT` · `HOUSE` · `LAND` · `COMMERCIAL` · `OTHER`

**OperationType:** `SALE` · `RENT` · `TEMPORARY_RENT`

**PropertyStatus:** `DRAFT` · `PUBLISHED` · `PAUSED`

**UserRole:** `USER` · `AGENCY` · `ADMIN`

---

## 1. `GET /api/v1/properties` · público · LIM-1

Listado paginado de avisos activos. Hoy: cualquier estado no borrado. Al cerrar LIM-3: si no viene `status`, **solo `PUBLISHED`**. `status` en este endpoint lo puede usar el dueño autenticado; un visitante que lo mande se ignora (siempre publicados).

| Query | Tipo | Default | Validación |
|-------|------|---------|------------|
| `page` | int | `0` | ≥ 0 |
| `size` | int | `20` | 1–100 |
| `city` | string | — | exacto, case-insensitive |
| `province` | string | — | exacto, case-insensitive. **Agregar — Lucas** |
| `type` | enum | — | PropertyType |
| `operation` | enum | — | OperationType |
| `status` | enum | — | ver regla de visibilidad |
| `minPrice` | decimal | — | ≥ 0 |
| `maxPrice` | decimal | — | ≥ 0 |
| `minBedrooms` | int | — | ≥ 0. **Agregar — Lucas** |
| `minBathrooms` | int | — | ≥ 0. **Agregar — Lucas** |

**200** `PageResponse<PropertyResponse>`

**400** `minPrice > maxPrice` o enum/número inválido

```bash
curl "http://localhost:8080/api/v1/properties?city=buenos%20aires&operation=RENT&minPrice=100000&minBedrooms=2&page=0&size=10"
```

---

## 2. `POST /api/v1/properties` · JWT (hoy público) · LIM-1

Crea aviso `DRAFT`. LIM-2: token obligatorio; `owner` = usuario del JWT.

**Body** `CreatePropertyRequest`

**201** `PropertyResponse` + header `Location: /api/v1/properties/{id}`

**400** validación · **401** sin token (post LIM-2)

```bash
curl -X POST http://localhost:8080/api/v1/properties \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"Loft","type":"APARTMENT","operation":"SALE","price":95000,"currency":"USD","city":"Rosario"}'
```

---

## 3. `GET /api/v1/properties/{id}` · mixto · LIM-1

**200** `PropertyResponse`

Visibilidad post LIM-2/3:

* Visitante: `200` sólo `PUBLISHED`; si no, `404`.
* Dueño: `200` con `DRAFT` / `PAUSED` / `PUBLISHED`.

**404** no existe o `deletedAt` set.

```bash
curl http://localhost:8080/api/v1/properties/1
curl http://localhost:8080/api/v1/properties/1 -H "Authorization: Bearer $TOKEN"
```

---

## 4. `DELETE /api/v1/properties/{id}` · JWT · dueño · LIM-1

Soft delete. Idempotente: si ya estaba borrado → `404`.

**204** vacío

**401 / 403 / 404**

```bash
curl -X DELETE http://localhost:8080/api/v1/properties/1 -H "Authorization: Bearer $TOKEN"
```

---

## 5. `PATCH /api/v1/properties/{id}` · Augusto · JWT · dueño · LIM-3

**Hoy:** mergeado, **público** (sin JWT). Body = `UpdatePropertyRequest` (mismos campos que create).

Parcial: sólo campos enviados. No cambia `status`. No toca imágenes.

Se puede editar en `DRAFT`, `PUBLISHED` y `PAUSED`. Un `PUBLISHED` editado **sigue publicado** (no vuelve a borrador).

**Body** `PatchPropertyRequest`

**200** `PropertyResponse`

**401 / 403 / 404** · **400** validación

```bash
curl -X PATCH http://localhost:8080/api/v1/properties/1 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"price":480000,"bedrooms":2}'
```

---

## 6. `POST /api/v1/properties/{id}/publish` · Lucas · JWT · dueño · LIM-3

**Hoy:** mergeado, público. Transición `DRAFT | PAUSED → PUBLISHED`. **No** valida mínimos todavía.

Mínimos: `title`, `price`, `operation`, `city` no nulos / no blank. Si falta alguno → `400`.

Ya `PUBLISHED` → `409`.

**200** `PropertyResponse` (`status: PUBLISHED`)

**401 / 403 / 404 / 409**

```bash
curl -X POST http://localhost:8080/api/v1/properties/1/publish -H "Authorization: Bearer $TOKEN"
```

También Lucas: default del listado público = `PUBLISHED`; query `province`, `minBedrooms`, `minBathrooms`.

---

## 7. `POST /api/v1/properties/{id}/pause` · Lucas · JWT · dueño · LIM-3

**Hoy:** mergeado, público. `PUBLISHED → PAUSED`. Sin body. Sale de la búsqueda (cuando el listado filtre publicados). Volver = `publish`.

Otro estado → `409`.

**200** `PropertyResponse` (`status: PAUSED`)

```
create
DRAFT ────► PUBLISHED ◄──► PAUSED
  │                │              │
  └──── DELETE (soft) ────────────┘
```

```bash
curl -X POST http://localhost:8080/api/v1/properties/1/pause -H "Authorization: Bearer $TOKEN"
```

---

## 8. `POST /api/v1/auth/register` · Busse · público · LIM-2

| Campo | Tipo | Validación |
|-------|------|------------|
| `email` | string | requerido, formato email, unique |
| `password` | string | requerido, mín. 8 |
| `name` | string | requerido, máx. 100 |
| `role` | enum | opcional, default `USER`. No permitir `ADMIN` por este endpoint (`400`) |
| `agencyName` | string | requerido si `role=AGENCY`; si no, ignorar / null |

**201** `AuthResponse` (sesión creada)

**400** validación · **409** email tomado

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"facu@uade.edu.ar","password":"minimo8chars","name":"Facu","role":"USER"}'
```

---

## 9. `POST /api/v1/auth/login` · Busse · público · LIM-2

| Campo | Tipo | Validación |
|-------|------|------------|
| `email` | string | requerido |
| `password` | string | requerido |

**200** `AuthResponse`

**401** email o password mal (mismo mensaje, no filtrar cuál)

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"facu@uade.edu.ar","password":"minimo8chars"}'
```

Respuesta:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400,
  "user": {
    "id": 1,
    "email": "facu@uade.edu.ar",
    "name": "Facu",
    "role": "USER",
    "agencyName": null
  }
}
```

Busse también: Spring Security, bcrypt, dueño en `properties.user_id`, `403` en mutaciones ajenas, `owner` en PropertyResponse.

---

## 10. `POST /api/v1/auth/logout` · Busse · JWT · LIM-2

Invalida el access token (denylist en tabla o cache hasta `exp`). El cliente igual borra el token.

Sin body.

**204** vacío

**401** sin token / ya inválido

```bash
curl -X POST http://localhost:8080/api/v1/auth/logout -H "Authorization: Bearer $TOKEN"
```

---

## 11. `GET /api/v1/me` · Matías · JWT · LIM-2

Sesión para header y reload.

**200** `UserResponse`

**401**

```bash
curl http://localhost:8080/api/v1/me -H "Authorization: Bearer $TOKEN"
```

---

## 12. `PATCH /api/v1/me` · Matías · JWT · LIM-2

| Campo | Tipo | Validación |
|-------|------|------------|
| `name` | string | máx. 100 |
| `agencyName` | string | sólo tiene efecto si `role=AGENCY` |
| `currentPassword` | string | requerido si viene `newPassword` |
| `newPassword` | string | mín. 8 |

Email y `role` no se cambian por acá. Parcial: sólo campos enviados.

**200** `UserResponse`

**400** validación · **401** · **403** `currentPassword` incorrecto

```bash
curl -X PATCH http://localhost:8080/api/v1/me \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Facundo"}'
```

---

## 13. `GET /api/v1/me/properties` · Facu · JWT · LIM-2

**Hoy:** mergeado. Header `X-User-Id` (no JWT). Respuesta = `List<PropertyResponse>` (no paginado). Al crear aviso se hardcodea `ownerId = 1L`.

Query: mismos que `GET /properties` (`page`, `size`, `status`, `city`, `type`, `operation`, `minPrice`, `maxPrice`, `minBedrooms`, `minBathrooms`, `province`).

**200** `PageResponse<PropertyResponse>`

**401**

```bash
curl "http://localhost:8080/api/v1/me/properties?status=DRAFT&page=0&size=20" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 14. `POST /api/v1/properties/{id}/images` · Lola · JWT · dueño · LIM-4

**Hoy:** mergeado, público. JSON `{ "url": "https://..." }` (máx. 500). **No** hay multipart ni `sortOrder`. `ImageResponse`: `{ id, url, createdAt }`.

| Parte | Tipo | Notas |
|-------|------|-------|
| `file` | file | requerido |
| `sortOrder` | int | opcional; default último+1. `0` = nueva principal (la anterior pasa a 1) |

**201** `ImageResponse`

**400** archivo / tipo / tamaño / tope · **401 / 403 / 404**

`PropertyResponse.images` siempre presente, ordenado por `sortOrder ASC`.

```bash
curl -X POST http://localhost:8080/api/v1/properties/1/images \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@foto.jpg" \
  -F "sortOrder=0"
```

---

## 15. `PATCH /api/v1/properties/{id}/images/{imageId}` · Lola · JWT · dueño · LIM-4

```json
{ "sortOrder": 0 }
```

`sortOrder` requerido, ≥ 0. Si es `0`, esta es principal y la que era `0` queda en `1`.

**200** `ImageResponse`

**400 / 401 / 403 / 404** (404 si la imagen no es de ese aviso)

```bash
curl -X PATCH http://localhost:8080/api/v1/properties/1/images/10 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"sortOrder":0}'
```

---

## 16. `DELETE /api/v1/properties/{id}/images/{imageId}` · Lola · JWT · dueño · LIM-4

Borra fila + archivo. Si era principal, la de menor `sortOrder` restante pasa a `0`.

**204**

**401 / 403 / 404**

```bash
curl -X DELETE http://localhost:8080/api/v1/properties/1/images/10 \
  -H "Authorization: Bearer $TOKEN"
```

---

## `GET /uploads/**` · Lola · público · LIM-4

`GET http://localhost:8080/uploads/properties/1/abc.webp` → `200` + bytes, `Content-Type` de la imagen.

**404** si no está el archivo.

```bash
curl -I http://localhost:8080/uploads/properties/1/abc.webp
```

---

## 17. `POST /api/v1/properties/{id}/inquiries` · Micaela · público · LIM-4

**Hoy:** mergeado. Acepta consulta sobre **cualquier** aviso no borrado (también DRAFT). `InquiryResponse` sin `propertyTitle` ni `readAt`.

| Campo | Tipo | Validación |
|-------|------|------------|
| `name` | string | requerido, máx. 100 |
| `email` | string | requerido, email |
| `phone` | string | opcional, máx. 30 |
| `message` | string | requerido, máx. 1000 |

**201** `InquiryResponse` (`readAt: null`)

**400** · **404** si el aviso no existe, está borrado o no está publicado

```bash
curl -X POST http://localhost:8080/api/v1/properties/1/inquiries \
  -H "Content-Type: application/json" \
  -d '{"name":"Ana Pérez","email":"ana@mail.com","phone":"+54 9 11 1234-5678","message":"Sigue disponible?"}'
```

Micaela: entidad `Inquiry` + FK a property. Nico consume esa tabla.

---

## 18. `GET /api/v1/me/inquiries` · Nico · JWT · LIM-4

Inbox: consultas de avisos del usuario.

| Query | Tipo | Default |
|-------|------|---------|
| `page` | int | `0` |
| `size` | int | `20` (1–100) |
| `propertyId` | long | — |
| `unreadOnly` | boolean | `false` |

**200** `PageResponse<InquiryResponse>` orden `createdAt DESC`

**401** · **403** si `propertyId` no es propio (o `404` para no filtrar existencia: preferir **404**)

```bash
curl "http://localhost:8080/api/v1/me/inquiries?unreadOnly=true&page=0&size=20" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 19. `GET /api/v1/me/inquiries/{inquiryId}` · Nico · JWT · LIM-4

Detalle. Sólo si la consulta es de un aviso propio.

**200** `InquiryResponse`

**401 / 404**

```bash
curl http://localhost:8080/api/v1/me/inquiries/3 -H "Authorization: Bearer $TOKEN"
```

---

## 20. `PATCH /api/v1/me/inquiries/{inquiryId}` · Nico · JWT · LIM-4

Marcar leída. Body:

```json
{ "read": true }
```

`read: true` setea `readAt` a now si era null (idempotente). `read: false` no se soporta en v1 → `400`.

**200** `InquiryResponse`

**400 / 401 / 404**

```bash
curl -X PATCH http://localhost:8080/api/v1/me/inquiries/3 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"read":true}'
```

Responder la consulta es por mail del visitante. No hay endpoint de reply.

---

## Checklist por persona

1. **Matías** — LIM-1 (CRUD, mergeado) + `#11 GET /me` + `#12 PATCH /me` **pendiente**. Depende del JWT de Busse.
2. **Augusto** — `#5 PATCH` **mergeado** (aún público, sin JWT).
3. **Lucas** — `#6 publish` + `#7 pause` **mergeado**. Falta listado solo `PUBLISHED` y query `province` / `minBedrooms` / `minBathrooms`.
4. **Busse** — `#8 #9 #10` **sin branch**. JWT, bcrypt, dueño, `owner` en PropertyResponse.
5. **Facu** — `#13 GET /me/properties` **mergeado** con `X-User-Id`. Falta JWT y paginación.
6. **Lola** — `#14 POST images` **mergeado** (JSON `url`). Falta `#15 #16` y `/uploads/**`.
7. **Micaela** — `#17 POST inquiries` **mergeado**. Falta exigir `PUBLISHED`.
8. **Nico** — `#18 GET /me/inquiries` **mergeado** con `X-User-Id`. Faltan `#19 #20`, JWT y `unreadOnly`.

Dependencias: Matías, Facu y Nico → JWT (Busse). Nico → Inquiry (Micaela). Micaela → avisos publicados (Lucas). Lola → dueño en el aviso (Busse) para el `403`.
