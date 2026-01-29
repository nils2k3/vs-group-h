# GitHub Actions CI/CD Workflows

Zwei automatisierte Workflows für Continous Integration und Deployment:

## 1. **build.yml** - Build & Test

Läuft auf jedem **Push** und **Pull Request** auf `main` Branch

**Was passiert:**
- ✅ Node.js 20 einrichten
- ✅ Dependencies installieren (`npm ci`)
- ✅ Build ausführen (`npm run build`)
- ✅ Verifiziert dass `dist/` Ordner erstellt wurde

**Trigger:**
```
- Alle Pushes auf main
- Alle Pull Requests zu main
```

## 2. **docker-publish.yml** - Docker Build & Push

Läuft wenn ein **Tag** erstellt wird (z.B. `v1.0.0`)

**Was passiert:**
- ✅ Docker Image bauen mit Buildx
- ✅ Multi-stage Build cachen (GitHub Actions Cache)
- ✅ Push zu Docker Hub (nur bei Tags)
- ✅ Automatisch versioniert (z.B. `v1.0.0`, `1.0`, `1`)

**Trigger:**
```
- Tags: v* (z.B. v1.0.0)
- Pushes auf main (nur build, kein push)
```

**Benötigte GitHub Secrets:**
```
DOCKER_USERNAME    - Dein Docker Hub Username
DOCKER_PASSWORD    - Dein Docker Hub Token
```

### Secrets einrichten:

1. Gehe zu: **Repo Settings → Secrets and variables → Actions**
2. Klick "New repository secret"
3. Füge hinzu:
   ```
   Name: DOCKER_USERNAME
   Value: dein-docker-username
   ```
4. Wiederhole für `DOCKER_PASSWORD` (verwende Personal Access Token, nicht Passwort)

### Docker Hub Token erstellen:

1. https://hub.docker.com/settings/security
2. "New Access Token"
3. Als **DOCKER_PASSWORD** in GitHub Secrets eintragen

## Benutzung

### Build-Workflow testen:
```bash
# Einfach pushen
git push origin main

# Im Browser: GitHub Repo → Actions → build.yml
```

### Docker Image bauen und pushen:
```bash
# Tag erstellen
git tag v1.0.0

# Tag pushen
git push origin v1.0.0

# Dann:
# - Docker Image wird automatisch gebaut
# - Gepusht zu Docker Hub: docker.io/dein-username/todo-frontend:v1.0.0
```

## Weitere Workflows (Optional)

Falls später nötig:
- **lint.yml** - Code Quality (ESLint, Prettier)
- **deploy.yml** - Automatisches Deployment
- **security.yml** - Dependency Scanning

Für jetzt reichen diese 2 Workflows!
