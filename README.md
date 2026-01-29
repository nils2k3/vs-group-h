# ToDo Application - Gruppe H (Full Stack)
**Nils Richter, Marc Walter**

Vue 3 Frontend + Spring Boot Backend (Gruppe K). Vollständige ToDo-Verwaltungsanwendung für Verteilte Systeme, HS Esslingen.

## 🎯 Features

- **Vue 3 Frontend**: Modernes SPA mit Composition API
- **Spring Boot Backend**: Gruppe K's Backend mit MapStruct DTOs
- **PostgreSQL Database**: Persistent Storage
- **Docker Support**: Vollständiger Stack containerisiert
- **Hot Reload**: Entwicklungsumgebung mit Vite
- **Health Checks**: Alle Services mit Healthchecks
- **API Documentation**: Swagger UI für Backend

## 📋 Voraussetzungen

- Docker & Docker Compose
- (Optional) Node.js 20+ für lokale Frontend-Entwicklung
- (Optional) Maven & Java 21 für lokale Backend-Entwicklung

## 🚀 Schnellstart (All-in-One)

```bash
cd "C:\Users\nilsr\Documents\H Esslingen\Semester 5\VS\vs-group-h"

# Stack mit allen Services starten
docker-compose -f docker-compose.dev.yml up -d
```

Das startet automatisch:
- ✅ **PostgreSQL** (Port 5432)
- ✅ **Spring Boot Backend** (Port 8080)
- ✅ **Adminer** (Port 7777) - Database GUI
- ✅ **Vue Frontend** (Port 3000)

### Zugriff

- **Frontend**: http://localhost:3000
- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080/todos
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Adminer (DB)**: http://localhost:7777

**Database Credentials:**
- Host: `localhost`
- Port: `5432`
- Database: `tododb`
- User: `todouser`
- Password: `secret`

## 🛑 Stack Stoppen

```bash
docker-compose -f docker-compose.dev.yml down
```

## 📁 Projektstruktur

```
vs-group-h/
├── frontend/                    # Vue 3 Frontend
│   ├── src/
│   │   ├── App.vue             # Hauptkomponente
│   │   ├── main.js             # Entry Point
│   │   ├── components/         # Vue Komponenten
│   │   │   ├── TodoForm.vue
│   │   │   └── TodoList.vue
│   │   └── services/
│   │       └── todoApi.js      # Axios API Client
│   ├── index.html
│   ├── package.json
│   ├── vite.config.js
│   ├── Dockerfile
│   └── README.md
│
├── backend/                     # [COPY] Gruppe K Spring Boot Backend
│   └── todoapp/
│       ├── src/
│       │   ├── main/java/de/vs_group4/todoapp/
│       │   │   ├── controllers/    # REST Endpoints
│       │   │   ├── services/       # Business Logic
│       │   │   ├── repositories/   # Data Access
│       │   │   ├── models/         # Entities
│       │   │   ├── dtos/           # Request/Response DTOs
│       │   │   ├── mappers/        # DTO Mapping
│       │   │   └── config/         # Spring Config
│       │   └── resources/
│       ├── pom.xml
│       ├── Dockerfile
│       └── mvnw
│
├── k8s/                         # Kubernetes Manifeste
│   ├── frontend-deployment.yaml
│   ├── frontend-service.yaml
│   └── README.md
│
├── .github/workflows/           # GitHub Actions CI/CD
│   └── build.yml
│
├── docker-compose.dev.yml       # Full Stack (Frontend + Backend + DB)
├── .env.dev                     # Environment Variables für Docker
├── .env.example                 # Beispiel .env für Frontend
├── API-SPEC.md                  # REST API Dokumentation
├── INTEGRATION-GUIDE.md         # Integration Guide
├── CI-CD.md                     # CI/CD Pipeline Dokumentation
├── DOCKER-SETUP.md              # Docker Setup Guide
└── README.md                    # Diese Datei
```

## 🔌 Architecture

```
┌─────────────┐
│  Frontend   │  Vue 3 + Vite
│  :3000      │  Nginx (Prod)
└──────┬──────┘
       │ HTTP
       │ VITE_API_URL=http://backend:8080
       │
┌──────▼──────────┐
│  Backend        │  Spring Boot 3.2.0
│  :8080          │  Java 21
│  /todos         │  MapStruct DTOs
└──────┬──────────┘
       │ JDBC
       │
┌──────▼──────────┐
│  PostgreSQL     │  postgres:18.1
│  :5432          │  tododb
│  todouser       │
└─────────────────┘
```

## 🔧 Lokale Entwicklung (ohne Docker)

### Frontend
```bash
cd frontend
npm install
npm run dev
# http://localhost:5173
```

### Backend
```bash
cd backend/todoapp
mvn clean install
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
# http://localhost:8080
```

Benötigt laufenden PostgreSQL:
```bash
docker run --name postgres -e POSTGRES_PASSWORD=secret -p 5432:5432 postgres:18.1-trixie
```

## 🔌 API-Kompatibilität

Dieses Backend ist von **Gruppe K** (mit [COPY] Markierung):
- Repository: https://github.com/vs-ws25-gruppe-k/vs-backend-group-k
- Spring Boot 4.0.1 mit Java 21
- PostgreSQL 18+ Datenbank
- 100% API-kompatibel mit Frontend

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

**Response:**
```json
{
  "id": 1,
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