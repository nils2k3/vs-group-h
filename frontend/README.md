
# Vue 3 Frontend - Gruppe H

ToDo-Anwendung Frontend mit Vue 3 + Vite + Axios

**Gruppe H:** Nils Richter, Marc Walter

## Features

- ✅ Vue 3 mit Composition API
- ✅ Vite als Build Tool
- ✅ Axios für API-Kommunikation
- ✅ Responsive Design mit CSS Grid
- ✅ Error & Success Handling
- ✅ Docker Support (Development + Production)
- ✅ Nginx für Production Deployment

## Tech Stack

- **Frontend Framework:** Vue 3
- **Build Tool:** Vite 5
- **HTTP Client:** Axios
- **Styling:** Scoped CSS
- **Runtime:** Node.js 20
- **Server (Prod):** Nginx

## Projektstruktur

```
frontend/
├── src/
│   ├── main.js              # Vue App Entry Point
│   ├── App.vue              # Root Component
│   ├── components/
│   │   ├── TodoForm.vue     # Form für neue ToDos
│   │   └── TodoList.vue     # Liste der ToDos
│   ├── services/
│   │   └── todoApi.js       # API Integration (Axios)
│   └── styles/
│
├── public/
│   └── index.html           # HTML Template
│
├── Dockerfile               # Development Image
├── Dockerfile.prod          # Production Image
├── nginx.conf              # Nginx Config (Production)
├── vite.config.js          # Vite Config
├── package.json            # Dependencies
├── .gitignore
├── .dockerignore
└── README.md
```

## Installation & Setup

### 1. Dependencies installieren

```bash
npm install
```

### 2. Development Server starten

```bash
npm run dev
```

Die App ist dann erreichbar unter: `http://localhost:3000`

### 3. Production Build

```bash
npm run build
```

Output wird in `/dist` erstellt.

### 4. Build Preview

```bash
npm run preview
```

## API Integration

Die App kommuniziert mit dem Backend über folgende Endpoints:

### Alle ToDos abrufen
```javascript
GET /todos
```

### Einzelnes ToDo abrufen
```javascript
GET /todos/{id}
```

### Neues ToDo erstellen
```javascript
POST /todos
Body: { title: string, description: string, completed?: boolean }
```

### ToDo aktualisieren
```javascript
PUT /todos/{id}
Body: { title: string, description: string, completed: boolean }
```

### ToDo löschen
```javascript
DELETE /todos/{id}
```

## Umgebungsvariablen

### Development

Automatisch in `vite.config.js` konfiguriert:
- Backend läuft unter `http://localhost:8080`
- Requests werden proxyfied zu `/todos` Endpoints

### Production

Setze die Environment Variable beim Build:

```bash
VITE_API_URL=https://api.example.com npm run build
```

In der App wird die URL gelesen über:
```javascript
const API_URL = process.env.VITE_API_URL || 'http://localhost:8080'
```

## Docker Deployment

### Development mit Docker

```bash
# Image bauen
docker build -t todo-frontend-dev .

# Container starten
docker run -p 3000:3000 todo-frontend-dev
```

Oder mit docker-compose (siehe root docker-compose.dev.yml):

```bash
docker-compose -f docker-compose.dev.yml up --build frontend
```

### Production mit Docker

```bash
# Production Image bauen
docker build -f Dockerfile.prod -t todo-frontend-prod .

# Container starten
docker run -p 80:80 todo-frontend-prod
```

Oder mit docker-compose (siehe root docker-compose.yml):

```bash
docker-compose up --build frontend
```

## Components

### TodoForm.vue

Form zum Erstellen neuer ToDos
- Input für Titel
- Textarea für Beschreibung
- Submit Button
- Validierung der Eingaben
- Emits: `add-todo`

### TodoList.vue

Zeigt Liste aller ToDos
- Checkbox zum Toggeln des Status
- Anzeigte von Titel und Beschreibung
- Delete Button
- Status Badge (Offen/Abgeschlossen)
- Emits: `toggle-todo`, `delete-todo`, `edit-todo`

## Services

### todoApi.js

Axios-basierter Service für API-Kommunikation

Methods:
- `getAllTodos()` - GET /todos
- `getTodoById(id)` - GET /todos/{id}
- `createTodo(data)` - POST /todos
- `updateTodo(id, data)` - PUT /todos/{id}
- `deleteTodo(id)` - DELETE /todos/{id}

## Styling

- CSS Grid für Layouts
- Gradient Background
- Responsive Design (Mobile First)
- Smooth Transitions
- Accessibility fokussiert

## Development Tipps

### Hot Module Replacement (HMR)

Vite unterstützt HMR - Änderungen an `.vue` Files werden automatisch im Browser aktualisiert ohne Seiten-Refresh.

### Debugging

```javascript
// In Chrome DevTools
- Vue DevTools Extension installieren
- Components, State, etc. inspizieren
```

### API Testing

```bash
# Mit curl
curl http://localhost:8080/todos

# Mit Axios direkt
import { todoApi } from '@/services/todoApi'
todoApi.getAllTodos().then(res => console.log(res.data))
```

## Error Handling

- Alle API-Fehler werden im UI angezeigt
- User-freundliche Fehlermeldungen
- Automatic Retry (optional in todoApi.js)
- Success Notifications

## Performance Optimizations

- Vite für schnellen Dev Server und Build
- Code Splitting automatisch
- Lazy Loading Components
- Gzip Kompression (Nginx)
- Caching statischer Assets (1 Jahr)

## Known Issues & Workarounds

### CORS Fehler

Falls der Browser CORS-Fehler wirft:
- Backend muss CORS aktivieren: `ALLOWED_ORIGIN=http://localhost:3000`
- Oder: Proxy in `vite.config.js` nutzen (ist bereits konfiguriert)

### Backend nicht erreichbar

- Prüfe ob Backend läuft: `curl http://localhost:8080/todos`
- Prüfe Firewall & Port 8080
- Prüfe ALLOWED_ORIGIN im Backend

## Production Checkliste

- [ ] `VITE_API_URL` gesetzt auf Production URL
- [ ] `npm run build` erfolgreich
- [ ] `/dist` Folder existiert und enthält Dateien
- [ ] Nginx Config konfiguriert für deine Domain
- [ ] SSL/TLS Zertifikat installiert
- [ ] Logging eingerichtet
- [ ] Error Tracking (z.B. Sentry) integriert

## Nächste Schritte

1. Backend mit Gruppe K kompatibel haben ✅
2. Vue App mit Docker laufen ✅
3. Integration mit PostgreSQL testen
4. Zusätzliche Features:
   - User Authentication
   - Todo Categories/Tags
   - Search & Filter
   - Drag & Drop Sorting
   - Dark Mode

## Support & Fragen

Bei Fragen:
- VS Code Debugger nutzen
- Browser DevTools inspizieren
- Logs in Console prüfen
- README in root-Verzeichnis lesen
