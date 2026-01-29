# CI/CD Pipeline

## Übersicht

Dein Projekt hat einen einfachen GitHub Actions Workflow für Development und Testing:

### **build.yml** (Build & Test)
- **Trigger**: `push` zu `main`, `pull_request` zu `main`
- **Umgebung**: Development
- **API-URL**: `http://localhost:8080` (externer Backend-Service, falls vorhanden)
- **Schritte**:
  1. Node.js 20 Setup
  2. Dependencies installieren (`npm ci`)
  3. Build (`npm run build`)
  4. Dist-Folder speichern als Artifact (5 Tage)
  5. Docker Image bauen (vs-group-h-frontend:dev)
  6. **Trivy Security Scan** (Vulnerabilities scannen)
  7. Upload Scan-Ergebnisse zu GitHub Security

**Wann nutzen**: Automatisch bei jedem Push zu main oder Pull Request

---

## Verwendung

### Development: Automatisch bei Push
```bash
git commit -m "Feature: xyz"
git push origin main
# → build.yml triggert automatisch
```

Workflow Monitor: GitHub → Actions → Build & Test (Development)

---

## Environment Variable

Die API-URL wird beim Build automatisch eingebaut:

```javascript
// In Vue App via import.meta.env
const API_URL = import.meta.env.VITE_API_URL || "http://localhost:8080"
```

Setzen in: `.env.local` oder CI/CD automatisch (bei externem Backend)

---

## Sicherheit (Trivy Scanning)

Der Workflow scannt das Docker Image auf Vulnerabilities:
- ✅ Findet bekannte CVEs in Dependencies
- ✅ Zeigt die Ergebnisse in GitHub Security Tab
- ⚠️ Blockiert Build nicht (informativ)

Vulnerabilities beheben:
```bash
cd frontend
npm audit fix
```

---

## Artifacts

Der Workflow speichert die gebauten Dateien:

| Artifact | Aufbewahrt | Nutzen |
|----------|-----------|--------|
| frontend-build-dev | 5 Tage | Debugging, Download |

Download im GitHub UI unter: Actions → Build & Test → Artifact

---

## Docker Image

Lokales bauen für K8s:
```bash
docker build -f frontend/Dockerfile -t vs-group-h-frontend:latest .
```

Image wird auch in CI/CD gebaut und gescannt.

---

## Troubleshooting

### Build schlägt fehl
```bash
# Lokal testen
cd frontend
npm ci
npm run build
```

### Trivy zeigt Vulnerabilities
```bash
# Dependency Vulnerabilities fixen
npm audit fix
```

### Docker Image Build fehlgeschlagen
```bash
# Lokal testen
docker build -f frontend/Dockerfile -t vs-group-h-frontend:latest .
```

---

## Weitere Ressourcen

- [GitHub Actions Docs](https://docs.github.com/en/actions)
- [Trivy Scanner](https://aquasecurity.github.io/trivy/)
- [Vite Environment Variables](https://vitejs.dev/guide/env-and-mode.html)

