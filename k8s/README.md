# K8s Deployment für Gruppe H Frontend
# Vue 3 Frontend mit Multi-Stage Docker Build

## Verwendung:

### 1. Docker Image bauen
```bash
# Lokal bauen für K8s (ohne push zu Registry)
docker build -f frontend/Dockerfile -t vs-group-h-frontend:latest .
```

### 2. K8s Manifeste deployen
```bash
# Einzelne Dateien
kubectl apply -f k8s/frontend-deployment.yaml
kubectl apply -f k8s/frontend-service.yaml

# Oder alle auf einmal
kubectl apply -f k8s/
```

### 3. Frontend testen
```bash
# Port-Forward für lokales Testing
kubectl port-forward svc/frontend-service 3000:80

# Im Browser: http://localhost:3000
```

## K8s Manifeste

### frontend-deployment.yaml
- **Image**: `vs-group-h-frontend:latest` (lokal gebaut)
- **Port**: 80 (Nginx)
- **Replicas**: 1
- **Environment**: VITE_API_URL zeigt auf Gruppe K Backend
- **Resources**: 128Mi/256Mi Memory, 100m/500m CPU
- **Health Checks**: Liveness & Readiness Probes

### frontend-service.yaml
- **Type**: NodePort (30080)
- **Selector**: app=frontend
- **Port**: 80 (HTTP)

## API-Integration

Das Frontend verbindet sich mit Gruppe K Backend:
```
Frontend (Port 30080) → Gruppe K Backend (http://gruppe-k-backend:8080)
```

Bei lokalem Testing mit Minikube/Docker Desktop:
```bash
kubectl set env deployment/frontend VITE_API_URL="http://host.docker.internal:8080"
```

## Skalierung
```bash
# Auf 3 Replicas skalieren
kubectl scale deployment frontend --replicas=3

# Auto-Scaling (benötigt Metrics Server)
kubectl autoscale deployment frontend --min=1 --max=3 --cpu-percent=80
```

## Logs anschauen
```bash
kubectl logs -f deployment/frontend
```
