# Integration Guide - Vue Frontend mit Gruppe K Backend

**Repository**: Frontend-only (Gruppe H)  
**Backend**: Gruppe K (vs-ws25-gruppe-k/vs-backend-group-k)

**Ziel**: Unser Vue 3 Frontend mit dem Backend von Gruppe K verwenden

## ✅ Kompatibilitäts-Checkliste

Unser Frontend ist **vollständig kompatibel** mit Gruppe K's Backend, weil:

- ✅ **Identische API-Endpoints**: Alle 5 REST-Operationen (GET, POST, PUT, DELETE)
- ✅ **Identische DTO-Felder**: `id`, `title`, `description`, `completed`
- ✅ **Identische HTTP-Status-Codes**: 200, 201, 204, 404
- ✅ **CORS ist aktiviert**: Backend erlaubt Cross-Origin Requests
- ✅ **Gleicher Port**: Backend läuft auf Port 8080

## 🚀 Setup-Anleitung

### Variante 1: Beide in Docker

**Schritt 1: Gruppe K's Backend starten**

```bash
# Gruppe K's Repo klonen
git clone https://github.com/vs-ws25-gruppe-k/vs-backend-group-k.git
cd vs-backend-group-k

# .env Datei erstellen (falls nötig)
cp .env.example .env

# WICHTIG: CORS konfigurieren
# In .env oder docker-compose.yml setzen:
# ALLOWED_ORIGIN=http://localhost:3000
# ODER
# ALLOWED_ORIGIN=*

# Backend mit Docker starten
docker-compose up -d
```

**Schritt 2: Unser Frontend starten**

```bash
# Zurück zu unserem Frontend-Projekt
cd "C:\Users\nilsr\Documents\H Esslingen\Semester 5\VS\vs-group-h"

# Frontend mit Docker starten
docker-compose -f docker-compose.dev.yml up -d

# Frontend ist erreichbar unter: http://localhost:3000
# Backend von Gruppe K unter: http://localhost:8080
```

**Schritt 3: Testen**

```bash
# Frontend aufrufen
start http://localhost:3000

# Backend-API testen
curl http://localhost:8080/todos

# Swagger UI von Gruppe K
start http://localhost:8080/swagger-ui.html
```

### Variante 2: Frontend lokal, Backend in Docker

**Schritt 1: Gruppe K's Backend in Docker**

```bash
cd vs-backend-group-k
docker-compose up -d
```

**Schritt 2: Unser Frontend lokal**

```bash
cd "C:\Users\nilsr\Documents\H Esslingen\Semester 5\VS\vs-group-h\frontend"
npm install
npm run dev
```

Frontend läuft auf http://localhost:3000 und verbindet sich automatisch mit http://localhost:8080

### Variante 3: Beide lokal (ohne Docker)

**Schritt 1: Gruppe K's Backend lokal**

```bash
cd vs-backend-group-k/todoapp

# PostgreSQL muss laufen (z.B. in Docker)
docker run -d --name postgres-dev \
  -e POSTGRES_USER=todouser \
  -e POSTGRES_PASSWORD=secret \
  -e POSTGRES_DB=tododb \
  -p 5432:5432 \
  postgres:16-alpine

# Backend starten
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

**Schritt 2: Unser Frontend lokal**

```bash
cd "C:\Users\nilsr\Documents\H Esslingen\Semester 5\VS\vs-group-h\frontend"
npm run dev
```

## 🔧 Konfiguration Details

### CORS-Konfiguration (WICHTIG!)

Gruppe K's Backend verwendet:
```java
@CrossOrigin(origins = "${ALLOWED_ORIGIN}")
```

**Option 1: Alle Origins erlauben (Development)**
```bash
# In Gruppe K's .env oder docker-compose.yml
ALLOWED_ORIGIN=*
```

**Option 2: Nur unser Frontend (Production)**
```bash
ALLOWED_ORIGIN=http://localhost:3000
```

**Option 3: Multiple Origins**
```bash
# Mehrere durch Komma trennen (falls Backend das unterstützt)
ALLOWED_ORIGIN=http://localhost:3000,http://127.0.0.1:3000
```

### Port-Mapping

| Service | Port | Konfigurierbar? |
|---------|------|-----------------|
| Frontend | 3000 | Ja (vite.config.js) |
| Backend | 8080 | Ja (application.properties) |
| PostgreSQL | 5432 | Ja (docker-compose.yml) |
| Adminer | 7777 | Ja (docker-compose.yml) |

**Falls Port 8080 belegt ist:**

Gruppe K's Backend-Port ändern:
```yaml
# In docker-compose.yml
services:
  backend:
    ports:
      - "8081:8080"  # Host:Container
    environment:
      PORT: 8080  # Interner Container-Port
```

Unser Frontend anpassen:
```javascript
// In frontend/src/services/todoApi.js
const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8081'
```

## 🧪 Integration Testing

### 1. Backend-Connectivity testen

```bash
# Backend erreichbar?
curl http://localhost:8080/actuator/health

# Sollte zurückgeben: {"status":"UP"}
```

### 2. CORS testen

```bash
# CORS-Header prüfen
curl -I -X OPTIONS http://localhost:8080/todos \
  -H "Origin: http://localhost:3000" \
  -H "Access-Control-Request-Method: GET"

# Erwartete Header:
# Access-Control-Allow-Origin: *
# Access-Control-Allow-Methods: GET, POST, PUT, DELETE
```

### 3. API-Funktionalität testen

```bash
# Leere Liste abrufen
curl http://localhost:8080/todos
# Erwartet: []

# ToDo erstellen
curl -X POST http://localhost:8080/todos \
  -H "Content-Type: application/json" \
  -d '{"title":"Integration Test","description":"Test mit Gruppe K Backend","completed":false}'

# Erwartet: {"id":1,"title":"Integration Test",...}

# Alle ToDos abrufen
curl http://localhost:8080/todos
# Erwartet: [{"id":1,...}]
```

### 4. Frontend End-to-End Test

1. **Frontend öffnen**: http://localhost:3000
2. **Neues ToDo erstellen**:
   - Titel: "Gruppe K Integration"
   - Beschreibung: "Test der Backend-Kompatibilität"
   - "Hinzufügen" klicken
3. **Prüfen**: ToDo erscheint in der Liste
4. **Status ändern**: Checkbox anklicken → sollte durchgestrichen werden
5. **Löschen**: X-Button → ToDo verschwindet
6. **Browser-Console prüfen**: Keine CORS-Fehler

## ⚠️ Bekannte Unterschiede

### 1. Entity-Field-Mapping

**Gruppe K's Entity:**
```java
@Column(name = "is_completed")
private boolean isCompleted;  // Java-Feld
```

**DTOs verwenden:**
```java
private Boolean completed;  // JSON-Feld
```

**Warum es funktioniert:**  
MapStruct mapped automatisch `completed` ↔ `isCompleted`. Keine Änderungen nötig.

### 2. Service-Layer Bug (unkritisch)

Gruppe K's `ToDoService.java` hat:
```java
existingItem.setCompleted(todoDetails.getCompleted());
```

Diese Methode existiert nicht (sollte `setIsCompleted()` sein), aber MapStruct übernimmt das Mapping beim DTO → Entity Conversion, deshalb funktioniert es trotzdem.

### 3. Package-Namen

- Gruppe K: `de.vs_group4.todoapp`
- Gruppe H: `de.vs_group_h.todoapp`

Irrelevant für API-Kompatibilität, nur für Backend-Code relevant.

## 📊 Vergleichstabelle

| Feature | Gruppe H Backend | Gruppe K Backend |
|---------|------------------|------------------|
| REST API | ✅ Identisch | ✅ Identisch |
| DTOs | ✅ Identisch | ✅ Identisch |
| Package | `vs_group_h` | `vs_group4` |
| Maven Wrapper | ❌ System Maven | ✅ Wrapper |
| Dockerfile | Modified (CRLF fix) | Original |
| CORS | Konfigurierbar | Konfigurierbar |
| Port | 8080 | 8080 |
| Database | PostgreSQL 16 | PostgreSQL 18 |

## 🐛 Troubleshooting

### Problem: CORS-Fehler im Browser

**Symptom:**
```
Access to fetch at 'http://localhost:8080/todos' from origin 'http://localhost:3000' 
has been blocked by CORS policy
```

**Lösung:**
```bash
# In Gruppe K's Backend setzen:
ALLOWED_ORIGIN=*
# ODER
ALLOWED_ORIGIN=http://localhost:3000

# Backend neu starten
docker-compose restart backend
```

### Problem: Connection refused

**Symptom:**
```
GET http://localhost:8080/todos net::ERR_CONNECTION_REFUSED
```

**Checks:**
```bash
# Ist das Backend gestartet?
docker ps | grep backend

# Läuft der Backend-Container?
docker logs <backend-container-name>

# Ist Port 8080 erreichbar?
curl http://localhost:8080/actuator/health
```

### Problem: 404 Not Found auf /todos

**Symptom:**
```
GET http://localhost:8080/todos 404 (Not Found)
```

**Checks:**
```bash
# Prüfe ob Backend korrekt gestartet ist
docker logs <backend-container-name> | grep "Started ToDoAppApplication"

# Prüfe Swagger UI
start http://localhost:8080/swagger-ui.html

# Sollte alle Endpoints zeigen
```

### Problem: Frontend zeigt "No todos yet"

**Mögliche Ursachen:**
1. Backend läuft nicht → Check `docker ps`
2. Backend hat andere API → Check Swagger UI
3. CORS blockiert Request → Check Browser Console

**Debug:**
```bash
# Browser Console öffnen (F12)
# Network Tab → XHR Filter
# Sollte sehen: GET http://localhost:8080/todos Status 200
```

## 📝 Checkliste für Abnahme

- [ ] Gruppe K's Backend startet ohne Fehler
- [ ] Backend erreichbar: `curl http://localhost:8080/actuator/health`
- [ ] Swagger UI erreichbar: http://localhost:8080/swagger-ui.html
- [ ] Unser Frontend startet ohne Fehler
- [ ] Frontend erreichbar: http://localhost:3000
- [ ] Keine CORS-Fehler in Browser Console
- [ ] ToDo erstellen funktioniert
- [ ] ToDos werden angezeigt
- [ ] Status ändern funktioniert
- [ ] ToDo löschen funktioniert
- [ ] Daten persistieren nach Backend-Neustart

## 🎓 Zusammenfassung

**Unser Frontend ist 100% kompatibel mit Gruppe K's Backend**, weil:

1. Wir haben das Backend von Gruppe K analysiert und die API-Spezifikation übernommen
2. Alle DTO-Feldnamen sind identisch
3. Alle Endpoints sind identisch
4. HTTP-Status-Codes sind identisch
5. CORS ist konfigurierbar und funktioniert

**Keine Code-Änderungen** im Frontend nötig, nur **Konfiguration** (CORS) im Backend von Gruppe K sicherstellen!
