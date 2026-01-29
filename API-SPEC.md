# Backend API Spezifikation - ToDo REST API

**Version**: 1.0  
**Backend**: Gruppe K (vs-ws25-gruppe-k/vs-backend-group-k)  
**Frontend**: Gruppe H (dieses Repository)

Dieses Dokument definiert die REST API-Schnittstelle des Backends von Gruppe K, mit der unser Frontend kommuniziert.

## 🔗 Backend-Implementierung

Diese API-Spezifikation wird bereitgestellt von:
- ✅ **Gruppe K Backend**: https://github.com/vs-ws25-gruppe-k/vs-backend-group-k

Unser Vue 3 Frontend ist vollständig kompatibel mit dieser API.

## Base URL

```
http://localhost:8080
```

---

## Endpoints

### 1. Alle ToDos abrufen

**Endpoint:** `GET /todos`

**Request:**
- Keine Parameter
- Kein Request Body

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "title": "Code Review durchführen",
    "description": "Pull Request #42 überprüfen und Feedback geben",
    "completed": false
  },
  {
    "id": 2,
    "title": "Dokumentation aktualisieren",
    "description": "API-Endpoints im Wiki dokumentieren",
    "completed": true
  }
]
```

**Response bei leerer Liste:**
```json
[]
```

---

### 2. Einzelnes ToDo abrufen

**Endpoint:** `GET /todos/{id}`

**Request:**
- **Path Parameter:** `id` (Long/Number) - ID des ToDos

**Response:** `200 OK`
```json
{
  "id": 1,
  "title": "Code Review durchführen",
  "description": "Pull Request #42 überprüfen und Feedback geben",
  "completed": false
}
```

**Error Response:** `404 Not Found` (wenn ToDo nicht existiert)

---

### 3. Neues ToDo erstellen

**Endpoint:** `POST /todos`

**Request Body:** `application/json`
```json
{
  "title": "Unit Tests schreiben",
  "description": "Tests für UserService implementieren",
  "completed": false
}
```

**Feldvalidierung:**
- `title`: **REQUIRED**, String, nicht leer
- `description`: **REQUIRED**, String, nicht leer
- `completed`: **OPTIONAL**, Boolean (default: false wenn nicht angegeben)

**Response:** `201 Created`
```json
{
  "id": 3,
  "title": "Unit Tests schreiben",
  "description": "Tests für UserService implementieren",
  "completed": false
}
```

**Error Response:** `400 Bad Request` (bei fehlenden/ungültigen Feldern)

---

### 4. ToDo aktualisieren

**Endpoint:** `PUT /todos/{id}`

**Request:**
- **Path Parameter:** `id` (Long/Number) - ID des zu aktualisierenden ToDos

**Request Body:** `application/json`
```json
{
  "title": "Code Review abgeschlossen",
  "description": "Pull Request #42 reviewed und approved",
  "completed": true
}
```

**Feldvalidierung:**
- Alle drei Felder müssen angegeben werden (auch wenn sie sich nicht ändern)
- `title`: String, nicht leer
- `description`: String, nicht leer
- `completed`: Boolean

**Response:** `200 OK`
```json
{
  "id": 1,
  "title": "Code Review abgeschlossen",
  "description": "Pull Request #42 reviewed und approved",
  "completed": true
}
```

**Error Response:** `404 Not Found` (wenn ToDo nicht existiert)

---

### 5. ToDo löschen

**Endpoint:** `DELETE /todos/{id}`

**Request:**
- **Path Parameter:** `id` (Long/Number) - ID des zu löschenden ToDos

**Response:** `204 No Content`
- Kein Response Body

**Error Response:** `404 Not Found` (wenn ToDo nicht existiert)

---

## Datenmodell

### ToDoItem (Response)
```typescript
{
  id: number,           // Auto-generiert, eindeutig
  title: string,        // Nicht leer
  description: string,  // Nicht leer
  completed: boolean    // true oder false
}
```

### ToDoItem (Request - POST/PUT)
```typescript
{
  title: string,          // REQUIRED, nicht leer
  description: string,    // REQUIRED, nicht leer
  completed: boolean      // OPTIONAL bei POST, REQUIRED bei PUT
}
```

---

## HTTP Status Codes

| Code | Bedeutung | Verwendung |
|------|-----------|------------|
| `200 OK` | Erfolgreich | GET, PUT |
| `201 Created` | Erfolgreich erstellt | POST |
| `204 No Content` | Erfolgreich gelöscht | DELETE |
| `400 Bad Request` | Ungültige Daten | Validierungsfehler |
| `404 Not Found` | Ressource nicht gefunden | GET/PUT/DELETE mit ungültiger ID |

---

## CORS-Konfiguration

**Wichtig für Frontend-Kompatibilität:**

```
Access-Control-Allow-Origin: *
Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS
Access-Control-Allow-Headers: Content-Type, Authorization
```

In der Entwicklung: Erlaube alle Origins (`*`)  
In der Produktion: Setze spezifische Frontend-URL

---

## Content-Type Header

**Alle Requests und Responses verwenden:**
```
Content-Type: application/json
```

---

## Wichtige Implementierungsdetails

### 1. ID-Generierung
- IDs sind vom Typ `Long` (Java) / `number` (TypeScript)
- Auto-Increment, beginnt bei 1
- IDs werden niemals wiederverwendet

### 2. Boolean-Handling
- Das Feld heißt im Response `completed` (nicht `isCompleted`)
- Erlaubte Werte: `true`, `false`, `null` (bei POST)
- Default bei POST ohne Angabe: `false`

### 3. String-Validierung
- Leere Strings (`""`) sind **nicht erlaubt** für title und description
- Whitespace-only Strings sollten abgelehnt werden
- Keine Längenbeschränkung definiert

### 4. PUT-Semantik
- PUT ersetzt das **komplette** ToDo-Objekt
- Alle Felder müssen gesendet werden (auch unveränderte)
- Nicht gesendete Felder werden **nicht** auf null gesetzt

---

## Beispiel-Requests (curl)

### Alle ToDos abrufen
```bash
curl http://localhost:8080/todos
```

### ToDo erstellen
```bash
curl -X POST http://localhost:8080/todos \
  -H "Content-Type: application/json" \
  -d '{"title":"Meeting vorbereiten","description":"Agenda für Sprint Planning erstellen","completed":false}'
```

### ToDo aktualisieren
```bash
curl -X PUT http://localhost:8080/todos/1 \
  -H "Content-Type: application/json" \
  -d '{"title":"Meeting durchgeführt","description":"Sprint Planning erfolgreich abgeschlossen","completed":true}'
```

### ToDo löschen
```bash
curl -X DELETE http://localhost:8080/todos/1
```

---

## Datenbank-Schema (Referenz)

```sql
CREATE TABLE todos (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    is_completed BOOLEAN DEFAULT FALSE
);
```

**Wichtig:** Das Datenbankfeld heißt `is_completed`, wird aber im JSON als `completed` gemappt.

---

## Checklist für dein Backend

- [ ] Alle 5 Endpoints implementiert
- [ ] Exakte Response-Struktur (id, title, description, completed)
- [ ] Korrekte HTTP-Status-Codes
- [ ] Validierung: title und description dürfen nicht leer sein
- [ ] CORS aktiviert (mindestens für Entwicklung)
- [ ] Content-Type: application/json
- [ ] IDs sind numerisch und auto-generiert
- [ ] PUT aktualisiert alle Felder
- [ ] DELETE gibt 204 zurück (kein Body)
- [ ] Port 8080 (oder konfigurierbar)
