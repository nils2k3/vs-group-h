# Docker Setup Guide - Backend + Vue Frontend

**Gruppe H**: Nils Richter, Marc Walter

Vollständige Anleitung für ein containerisiertes Setup mit Spring Boot Backend, PostgreSQL Datenbank und Vue 3 Frontend.

> **Hinweis**: Dieses Setup funktioniert mit unserem eigenen Backend (adaptiert von Gruppe K) ODER mit dem Original-Backend von Gruppe K. Siehe [INTEGRATION-GUIDE.md](INTEGRATION-GUIDE.md) für Details zur Verwendung mit Gruppe K's Backend.

---

## Übersicht der Container

```
┌─────────────────────────────────────────────┐
│  Docker Compose Umgebung                    │
├─────────────────────────────────────────────┤
│                                             │
│  ┌─────────────┐  ┌──────────────┐        │
│  │   Vue 3     │  │  Spring Boot │        │
│  │  Frontend   │  │   Backend    │        │
│  │  Port 3000  │  │  Port 8080   │        │
│  └──────┬──────┘  └──────┬───────┘        │
│         │                 │                 │
│         │                 │                 │
│         │          ┌──────▼────────┐       │
│         │          │  PostgreSQL   │       │
│         │          │  Port 5432    │       │
│         │          └───────────────┘       │
│         │                                   │
│         │          ┌───────────────┐       │
│         └─────────►│   Adminer     │       │
│                    │  Port 8888    │       │
│                    └───────────────┘       │
└─────────────────────────────────────────────┘
```

**Services:**
- **frontend**: Vue 3 mit Vite Dev Server (Hot Reload)
- **backend**: Spring Boot 3.2.0 mit Java 21 (Gruppe K adaptiert)
- **db**: PostgreSQL 16 mit Health Check
- **adminer**: Web-basierte Datenbank-Administration

---

## 1. Backend Dockerfile

**Datei:** `backend/Dockerfile`

### Multi-Stage Build (Best Practice)

```dockerfile
# ============================================
# Stage 1: Build Stage
# ============================================
FROM eclipse-temurin:21-jdk-alpine AS build

# Arbeitsverzeichnis setzen
WORKDIR /app

# Maven Wrapper und Dependencies kopieren (für Layer Caching)
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn
COPY pom.xml .

# Maven Wrapper ausführbar machen
RUN chmod +x ./mvnw

# Dependencies herunterladen (wird gecached wenn pom.xml sich nicht ändert)
RUN ./mvnw dependency:resolve

# Source Code kopieren
COPY src ./src

# Anwendung bauen (Tests überspringen für schnelleren Build)
RUN ./mvnw clean package -DskipTests

# ============================================
# Stage 2: Runtime Stage (kleiner, sicherer)
# ============================================
FROM eclipse-temurin:21-jre-alpine AS runtime

# curl für Health Checks installieren + Non-root User erstellen
RUN apk add --no-cache curl && \
    addgroup -g 1001 -S appuser && \
    adduser -u 1001 -S appuser -G appuser

# Arbeitsverzeichnis
WORKDIR /app

# JAR aus Build-Stage kopieren
COPY --from=build /app/target/*.jar app.jar

# Ownership an non-root user übergeben
RUN chown -R appuser:appuser /app

# Zu non-root user wechseln (Security Best Practice)
USER appuser

# Environment Variables mit Defaults
ENV SPRING_PROFILES_ACTIVE=prod
ENV PORT=8080
ENV JVM_OPTS="-Xmx512m -Xms256m"

# Database Config (wird von docker-compose überschrieben)
ENV POSTGRES_HOST=db
ENV POSTGRES_PORT=5432
ENV POSTGRES_DB=tododb
ENV POSTGRES_USER=todouser
ENV POSTGRES_PASSWORD=secret

# CORS Config
ENV ALLOWED_ORIGIN=http://localhost:3000

# Port exponieren
EXPOSE ${PORT}

# Health Check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:${PORT}/actuator/health || exit 1

# Anwendung starten
ENTRYPOINT ["sh", "-c", "java ${JVM_OPTS} -Dserver.port=${PORT} -jar app.jar"]
```

### Wichtige Dockerfile-Konzepte:

- **Multi-Stage Build**: Build-Tools nur in Build-Stage, Runtime ist klein
- **Layer Caching**: Dependencies zuerst, dann Source Code
- **Non-root User**: Security Best Practice
- **Health Checks**: Docker kann automatisch prüfen ob Container healthy ist
- **Environment Variables**: Konfiguration von außen steuerbar

---

## 2. Frontend Dockerfile (React)

**Datei:** `frontend/Dockerfile`

### Variante A: Development (mit Hot Reload)

```dockerfile
FROM node:20-alpine

WORKDIR /app

# Package files kopieren
COPY package*.json ./

# Dependencies installieren
RUN npm install

# Source Code kopieren
COPY . .

# Port exponieren
EXPOSE 3000

# Development Server starten
CMD ["npm", "start"]
```

### Variante B: Production (optimiert mit Nginx)

```dockerfile
# ============================================
# Stage 1: Build React App
# ============================================
FROM node:20-alpine AS build

WORKDIR /app

# Dependencies installieren
COPY package*.json ./
RUN npm ci --only=production

# App bauen
COPY . .
RUN npm run build

# ============================================
# Stage 2: Nginx Server
# ============================================
FROM nginx:alpine

# Build-Output kopieren
COPY --from=build /app/build /usr/share/nginx/html

# Custom Nginx Config (optional)
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
```

**Datei:** `frontend/nginx.conf` (für Production)

```nginx
server {
    listen 80;
    server_name localhost;
    root /usr/share/nginx/html;
    index index.html;

    # React Router Support
    location / {
        try_files $uri $uri/ /index.html;
    }

    # API Proxy (optional)
    location /api {
        proxy_pass http://backend:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_cache_bypass $http_upgrade;
    }

    # Caching für statische Assets
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
```

---

## 3. Docker Compose - Development

**Datei:** `docker-compose.dev.yml`

```yaml
version: '3.8'

services:
  # PostgreSQL Datenbank
  db:
    image: postgres:16-alpine
    container_name: todo-db-dev
    restart: unless-stopped
    environment:
      POSTGRES_DB: ${POSTGRES_DB:-tododb}
      POSTGRES_USER: ${POSTGRES_USER:-todouser}
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD:-secret}
    ports:
      - "${DB_PORT:-5432}:5432"
    volumes:
      - postgres_data_dev:/var/lib/postgresql/data
    networks:
      - app-network
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${POSTGRES_USER:-todouser}"]
      interval: 10s
      timeout: 5s
      retries: 5

  # Adminer - Database UI (nur Development)
  adminer:
    image: adminer:latest
    container_name: todo-adminer-dev
    restart: unless-stopped
    ports:
      - "7777:8080"
    networks:
      - app-network
    depends_on:
      - db

  # Backend API
  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
    container_name: todo-backend-dev
    restart: unless-stopped
    environment:
      SPRING_PROFILES_ACTIVE: dev
      POSTGRES_HOST: db
      POSTGRES_PORT: 5432
      POSTGRES_DB: ${POSTGRES_DB:-tododb}
      POSTGRES_USER: ${POSTGRES_USER:-todouser}
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD:-secret}
      ALLOWED_ORIGIN: http://localhost:3000
      JVM_OPTS: "-Xmx256m -Xms128m"
    ports:
      - "${BACKEND_PORT:-8080}:8080"
    networks:
      - app-network
    depends_on:
      db:
        condition: service_healthy
    volumes:
      # Optional: Hot reload für Development
      - ./backend/src:/app/src

  # React Frontend
  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
    container_name: todo-frontend-dev
    restart: unless-stopped
    environment:
      REACT_APP_API_URL: http://localhost:8080
      CHOKIDAR_USEPOLLING: "true"  # Für Hot Reload in Docker
    ports:
      - "${FRONTEND_PORT:-3000}:3000"
    networks:
      - app-network
    depends_on:
      - backend
    volumes:
      # Hot Reload für Development
      - ./frontend/src:/app/src
      - ./frontend/public:/app/public
      - /app/node_modules  # node_modules nicht überschreiben

networks:
  app-network:
    driver: bridge

volumes:
  postgres_data_dev:
    driver: local
```

---

## 4. Docker Compose - Production

**Datei:** `docker-compose.yml`

```yaml
version: '3.8'

services:
  # PostgreSQL Datenbank
  db:
    image: postgres:16-alpine
    container_name: todo-db
    restart: always
    environment:
      POSTGRES_DB: ${POSTGRES_DB}
      POSTGRES_USER: ${POSTGRES_USER}
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - app-network
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${POSTGRES_USER}"]
      interval: 30s
      timeout: 10s
      retries: 3

  # Backend API
  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
    container_name: todo-backend
    restart: always
    environment:
      SPRING_PROFILES_ACTIVE: prod
      POSTGRES_HOST: db
      POSTGRES_PORT: 5432
      POSTGRES_DB: ${POSTGRES_DB}
      POSTGRES_USER: ${POSTGRES_USER}
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
      ALLOWED_ORIGIN: ${FRONTEND_URL}
      JVM_OPTS: "-Xmx512m -Xms256m"
    expose:
      - "8080"
    networks:
      - app-network
    depends_on:
      db:
        condition: service_healthy

  # React Frontend (Production)
  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
      target: runtime  # Nutzt Nginx Stage
    container_name: todo-frontend
    restart: always
    ports:
      - "${FRONTEND_PORT:-80}:80"
    networks:
      - app-network
    depends_on:
      - backend

networks:
  app-network:
    driver: bridge

volumes:
  postgres_data:
    driver: local
```

---

## 5. Environment Configuration

**Datei:** `.env.example`

```env
# Database Configuration
POSTGRES_DB=tododb
POSTGRES_USER=todouser
POSTGRES_PASSWORD=super_secret_password_change_in_production
DB_PORT=5432

# Backend Configuration
BACKEND_PORT=8080

# Frontend Configuration
FRONTEND_PORT=3000
FRONTEND_URL=http://localhost:3000

# Production URLs
PRODUCTION_BACKEND_URL=https://api.example.com
PRODUCTION_FRONTEND_URL=https://app.example.com
```

**Wichtig:**
- `.env.example` ins Repository committen
- `.env` zu `.gitignore` hinzufügen
- Für Production: `cp .env.example .env` und Werte anpassen

---

## 6. .dockerignore Files

### Backend `.dockerignore`

```
# Build Artifacts
target/
*.jar
*.war
*.ear

# IDE Files
.idea/
.vscode/
*.iml
.project
.classpath
.settings/

# OS Files
.DS_Store
Thumbs.db

# Git
.git/
.gitignore

# Env Files
.env
.env.local

# Logs
*.log
logs/

# Test
test-results/
coverage/
```

### Frontend `.dockerignore`

```
# Dependencies
node_modules/

# Build Output
build/
dist/

# Testing
coverage/

# IDE
.idea/
.vscode/

# OS
.DS_Store
Thumbs.db

# Git
.git/
.gitignore

# Env
.env
.env.local
.env.production

# Logs
npm-debug.log*
yarn-debug.log*
yarn-error.log*
```

---

## 7. Befehle & Workflow

### Initial Setup

```bash
# .env Datei erstellen
cp .env.example .env

# Secrets in .env anpassen (wichtig für Production!)
nano .env
```

### Development

```bash
# Alles starten
docker-compose -f docker-compose.dev.yml up --build

# Im Hintergrund starten
docker-compose -f docker-compose.dev.yml up -d --build

# Logs ansehen
docker-compose -f docker-compose.dev.yml logs -f

# Einzelnen Service neu starten
docker-compose -f docker-compose.dev.yml restart backend

# Alles stoppen und löschen
docker-compose -f docker-compose.dev.yml down

# Mit Volumes löschen (Datenbank zurücksetzen)
docker-compose -f docker-compose.dev.yml down -v
```

### Production

```bash
# Production Build & Start
docker-compose up -d --build

# Status prüfen
docker-compose ps

# Logs ansehen
docker-compose logs -f backend

# Stoppen
docker-compose down

# Update deployen
docker-compose pull
docker-compose up -d --build --force-recreate
```

### Nützliche Commands

```bash
# In Container einloggen
docker exec -it todo-backend sh

# Datenbank Backup
docker exec todo-db pg_dump -U todouser tododb > backup.sql

# Datenbank Restore
docker exec -i todo-db psql -U todouser tododb < backup.sql

# Container neu bauen (ohne Cache)
docker-compose build --no-cache

# Alle ungenutzten Images/Container löschen
docker system prune -a
```

---

## 8. Projektstruktur

```
project-root/
├── backend/
│   ├── src/
│   ├── pom.xml
│   ├── Dockerfile
│   └── .dockerignore
│
├── frontend/
│   ├── src/
│   ├── public/
│   ├── package.json
│   ├── Dockerfile
│   ├── nginx.conf
│   └── .dockerignore
│
├── docker-compose.yml          # Production
├── docker-compose.dev.yml      # Development
├── .env.example
├── .env                        # Nicht in Git!
├── .gitignore
└── README.md
```

---

## 9. Spring Boot Application Properties

### Backend: `application.properties`

```properties
# Server
server.port=${PORT:8080}

# Database
spring.datasource.url=jdbc:postgresql://${POSTGRES_HOST:localhost}:${POSTGRES_PORT:5432}/${POSTGRES_DB:tododb}
spring.datasource.username=${POSTGRES_USER:todouser}
spring.datasource.password=${POSTGRES_PASSWORD:secret}
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=${JPA_HIBERNATE_DDL_AUTO:validate}
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true

# Actuator (Health Checks)
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=when-authorized

# CORS
allowed.origin=${ALLOWED_ORIGIN:*}
```

### Backend: `application-dev.properties`

```properties
# Development-spezifische Settings
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# H2 Console (optional für Testing)
spring.h2.console.enabled=false

# Logging
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate=INFO
```

---

## 10. React Environment Variables

### Frontend: `.env.development`

```env
REACT_APP_API_URL=http://localhost:8080
REACT_APP_ENV=development
```

### Frontend: `.env.production`

```env
REACT_APP_API_URL=https://api.example.com
REACT_APP_ENV=production
```

### In React verwenden:

```javascript
// API Client
const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

export const fetchTodos = async () => {
  const response = await fetch(`${API_URL}/todos`);
  return response.json();
};
```

---

## 11. Networking Explained

### Container-zu-Container Kommunikation

```yaml
services:
  backend:
    environment:
      POSTGRES_HOST: db  # ← Service-Name als Hostname!
```

**Wichtig:**
- Container kommunizieren über **Service-Namen** (nicht localhost)
- `db` ist der Hostname für die PostgreSQL-Datenbank
- `backend` ist der Hostname für das Backend (vom Frontend aus)

### Port Mapping

```yaml
ports:
  - "8080:8080"  # HOST:CONTAINER
  - "3000:3000"
```

- Links: Port auf deinem Host (localhost:8080)
- Rechts: Port im Container

### expose vs ports

```yaml
expose:
  - "8080"  # Nur für andere Container sichtbar, NICHT von Host

ports:
  - "8080:8080"  # Für Host UND andere Container sichtbar
```

---

## 12. Volumes Explained

### Named Volumes (Persistent Data)

```yaml
volumes:
  postgres_data:/var/lib/postgresql/data  # Datenbank bleibt nach `down`

volumes:
  postgres_data:
    driver: local
```

### Bind Mounts (Hot Reload)

```yaml
volumes:
  - ./frontend/src:/app/src  # Lokale Änderungen sofort im Container
```

**Wann was:**
- **Named Volumes**: Datenbank-Daten, Production
- **Bind Mounts**: Development, Hot Reload

---

## 13. Health Checks

### Im Dockerfile

```dockerfile
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1
```

### In docker-compose.yml

```yaml
healthcheck:
  test: ["CMD-SHELL", "pg_isready -U todouser"]
  interval: 10s
  timeout: 5s
  retries: 5
```

### Abhängigkeiten mit Health Checks

```yaml
depends_on:
  db:
    condition: service_healthy  # Wartet bis DB healthy ist
```

---

## 14. Security Best Practices

✅ **Non-root User in Container**
```dockerfile
USER appuser
```

✅ **Secrets in .env, nicht hardcoded**
```yaml
environment:
  POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
```

✅ **Minimal Base Images**
```dockerfile
FROM eclipse-temurin:21-jre-alpine  # Statt jdk
```

✅ **Multi-Stage Builds**
```dockerfile
FROM build AS builder
FROM runtime  # Nur Runtime, keine Build-Tools
```

✅ **.dockerignore verwenden**
```
.git/
.env
node_modules/
```

---

## 15. Troubleshooting

### Container startet nicht

```bash
# Logs ansehen
docker-compose logs backend

# In Container einloggen (wenn er läuft)
docker exec -it todo-backend sh

# Container inspizieren
docker inspect todo-backend
```

### Datenbank Connection Fails

```bash
# DB erreichbar?
docker exec -it todo-backend ping db

# PostgreSQL Connection testen
docker exec -it todo-db psql -U todouser -d tododb -c "SELECT 1;"
```

### Ports bereits belegt

```bash
# Port 8080 checken
netstat -ano | findstr :8080  # Windows
lsof -i :8080                 # Mac/Linux

# Anderen Port verwenden
BACKEND_PORT=8081 docker-compose up
```

### Build Cache Probleme

```bash
# Ohne Cache neu bauen
docker-compose build --no-cache backend
```

---

## Checkliste für Production Deployment

- [ ] `.env` mit sicheren Passwörtern
- [ ] `ALLOWED_ORIGIN` auf Frontend-URL setzen
- [ ] `spring.jpa.hibernate.ddl-auto=validate` (nicht `update`!)
- [ ] Health Checks konfiguriert
- [ ] Volumes für Datenbank-Persistence
- [ ] Reverse Proxy (nginx/Traefik) vor Services
- [ ] SSL/TLS Zertifikate
- [ ] Logging & Monitoring (z.B. Prometheus)
- [ ] Backup-Strategie für Datenbank
- [ ] Resource Limits setzen (CPU/Memory)

---

## Nächste Schritte

1. Backend-Projekt mit Spring Boot erstellen
2. Frontend-Projekt mit `npx create-react-app` erstellen
3. Dockerfiles in jeweiligen Ordnern anlegen
4. docker-compose Files im Root erstellen
5. `.env.example` anlegen und zu `.env` kopieren
6. `docker-compose -f docker-compose.dev.yml up --build`
