# ToDo Application Frontend - Gruppe H
**Nils Richter, Marc Walter**

Vue 3 Frontend für die ToDo-Verwaltungsanwendung von Gruppe K. Entwickelt für Verteilte Systeme, HS Esslingen.

## 🎯 Features

- **Vue 3 Frontend**: Modernes SPA mit Composition API
- **API-Kompatibel**: Entwickelt für Gruppe K's Spring Boot Backend
- **Docker Support**: Containerisiertes Frontend-Setup
- **Hot Reload**: Entwicklungsumgebung mit Vite
- **Responsive Design**: Funktioniert auf allen Geräten

## 📋 Voraussetzungen

- **Gruppe K's Backend**: https://github.com/vs-ws25-gruppe-k/vs-backend-group-k
- Docker & Docker Compose (für containerisiertes Setup)
- (Optional) Node.js 20+ für lokale Entwicklung ohne Docker

## 🚀 Schnellstart

### Schritt 1: Gruppe K's Backend starten

```bash
# Gruppe K's Repository klonen
git clone https://github.com/vs-ws25-gruppe-k/vs-backend-group-k.git
cd vs-backend-group-k

# CORS konfigurieren: ALLOWED_ORIGIN=* in .env oder docker-compose.yml setzen

# Backend mit Docker starten (läuft auf Port 8080)
docker-compose up -d
```

### Schritt 2: Frontend starten

**Option A: Mit Docker**
```bash
cd "C:\Users\nilsr\Documents\H Esslingen\Semester 5\VS\vs-group-h"
docker-compose -f docker-compose.dev.yml up -d
```

**Option B: Lokal (für Entwicklung)**
```bash
cd frontend
npm install
npm run dev
```

### Zugriff

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080/todos
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Adminer (DB)**: http://localhost:7777 (von Gruppe K's Backend)

> **Wichtig**: CORS muss im Backend konfiguriert sein: `ALLOWED_ORIGIN=*` oder `ALLOWED_ORIGIN=http://localhost:3000`

## 📁 Projektstruktur

```
vs-group-h/
├── frontend/               # Vue 3 Frontend
│   ├── src/
│   │   ├── App.vue        # Hauptkomponente
│   │   ├── main.js        # Entry Point
│   │   ├── components/    # Vue Komponenten
│   │   │   ├── TodoForm.vue
│   │   │   └── TodoList.vue
│   │   └── services/
│   │       └── todoApi.js # Axios API Client
│   ├── index.html
│   ├── package.json
│   ├── vite.config.js
│   ├── Dockerfile
│   └── README.md
├── docker-compose.dev.yml # Docker Setup für Frontend
├── API-SPEC.md           # REST API Dokumentation (Gruppe K)
├── INTEGRATION-GUIDE.md  # Setup-Anleitung mit Gruppe K Backend
└── README.md             # Diese Datei
```

> **Hinweis**: Das Backend wird von Gruppe K bereitgestellt. Siehe deren Repository für Backend-Code.

## 🔌 API-Kompatibilität

Dieses Frontend ist entwickelt für:

✅ **Gruppe K Backend**
- Repository: https://github.com/vs-ws25-gruppe-k/vs-backend-group-k
- Spring Boot 3.2.0 mit Java 21
- PostgreSQL 18+ Datenbank
- 100% API-kompatibel

### API-Spezifikation:

```
GET    /todos          # Alle ToDos abrufen
GET    /todos/{id}     # Einzelnes ToDo abrufen
POST   /todos          # Neues ToDo erstellen
PUT    /todos/{id}     # ToDo aktualisieren
DELETE /todos/{id}     # ToDo löschen
```

**Request Body (POST/PUT):**
```json
{
  "title": "Aufgabe erledigen",
  "description": "Detaillierte Beschreibung",
  "completed": false
}
```

**Response Body:**
```json
{
  "id": 1,
  "title": "Aufgabe erledigen",
  "description": "Detaillierte Beschreibung",
  "completed": false
}
```

Vollständige API-Dokumentation: [API-SPEC.md](API-SPEC.md)

## 🛠️ Entwicklung

### Frontend lokal entwickeln

```bash
cd frontend
npm install
npm run dev  # Läuft auf http://localhost:3000
```

### Hot Reload mit Docker

Die docker-compose.dev.yml verwendet Bind Mounts:
- Änderungen in `frontend/src` werden sofort übernommen
- Vite Hot Module Replacement (HMR) aktiv

### Mit verschiedenen Backend-URLs testen

```bash
# .env Datei im frontend/ Verzeichnis erstellen
echo "VITE_API_URL=http://localhost:8081" > frontend/.env

# Oder direkt beim Start
VITE_API_URL=http://andere-url:8080 npm run dev
```

## 🧪 Testing

### Integration Test

1. **Backend prüfen**: 
   ```bash
   curl http://localhost:8080/actuator/health
   # Sollte: {"status":"UP"}
   ```

2. **Frontend öffnen**: http://localhost:3000

3. **Funktionalität testen**:
   - ✅ ToDo erstellen
   - ✅ ToDos werden angezeigt
   - ✅ Status ändern (Checkbox)
   - ✅ ToDo löschen

### API direkt testen

```bash
# Neues ToDo erstellen
curl -X POST http://localhost:8080/todos \
  -H "Content-Type: application/json" \
  -d '{"title":"Test","description":"Test Task","completed":false}'

# Alle ToDos abrufen
curl http://localhost:8080/todos
```

## 🐳 Docker Commands

```bash
# Frontend starten
docker-compose -f docker-compose.dev.yml up -d

# Mit Rebuild (nach Code-Änderungen am Dockerfile)
docker-compose -f docker-compose.dev.yml up -d --build

# Logs anzeigen
docker-compose -f docker-compose.dev.yml logs -f frontend

# Frontend neu starten
docker-compose -f docker-compose.dev.yml restart frontend

# Frontend stoppen
docker-compose -f docker-compose.dev.yml down
```

## 🔧 Konfiguration

### Frontend-Umgebungsvariablen

In `frontend/src/services/todoApi.js`:
```javascript
const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'
```

Überschreiben mit `.env` Datei:
```
VITE_API_URL=http://localhost:8080
```

### Backend-Umgebungsvariablen

In `docker-compose.dev.yml` oder als System-Env:
```yaml
POSTGRES_HOST=db
POSTGRES_PORT=5432
POSTGRES_DB=tododb
POSTGRES_USER=todouser
POSTGRES_PASSWORD=secret
ALLOWED_ORIGIN=http://localhost:3000
```

## ⚠️ Troubleshooting

### Frontend zeigt weiße Seite
- **Problem**: `index.html` im falschen Verzeichnis
- **Lösung**: `index.html` muss im Frontend-Root liegen (nicht in `public/`)

### "process is not defined" Error
- **Problem**: Vite verwendet `import.meta.env`, nicht `process.env`
- **Lösung**: Bereits gefixt in `todoApi.js`

### CORS-Fehler
- **Problem**: Backend erlaubt Frontend-Origin nicht
- **Lösung**: `ALLOWED_ORIGIN=http://localhost:3000` im Backend setzen

### Port already in use
- **Problem**: Port 3000, 8080 oder 5432 bereits belegt
- **Lösung**: Ports in `docker-compose.dev.yml` ändern oder andere Services stoppen

### Frontend nicht erreichbar (404)
- **Problem**: Vite bindet an localhost statt 0.0.0.0
- **Lösung**: Bereits gefixt mit `--host 0.0.0.0` Flag in Dockerfile

### Maven Wrapper Fehler (exit 127)
- **Problem**: Windows CRLF Line Endings
- **Lösung**: Bereits gefixt durch direkten Maven-Install statt Wrapper

## 📚 Weitere Dokumentation

- [INTEGRATION-GUIDE.md](INTEGRATION-GUIDE.md) - **Wichtig**: Setup mit Gruppe K Backend
- [API-SPEC.md](API-SPEC.md) - REST API Spezifikation von Gruppe K
- [frontend/README.md](frontend/README.md) - Frontend-spezifische Dokumentation

## 🎓 Entwickelt für

**Hochschule Esslingen**  
Verteilte Systeme - WS 2025/26  
Gruppe H: Nils Richter, Marc Walter

## 📝 Hinweise

- Dieses Repository enthält **nur das Frontend**
- Das Backend wird von **Gruppe K** bereitgestellt
- Frontend ist eine Eigenentwicklung in Vue 3
- 100% API-kompatibel mit Gruppe K's Backend
- Für Backend-Details siehe: https://github.com/vs-ws25-gruppe-k/vs-backend-group-k

## 🔗 Verwandte Repositories

- Gruppe K Backend: https://github.com/vs-ws25-gruppe-k/vs-backend-group-k