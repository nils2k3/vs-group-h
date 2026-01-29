# Vue Frontend Dockerfile - Development

FROM node:20-alpine

WORKDIR /app

# Package files kopieren
COPY package*.json ./

# Dependencies installieren
RUN npm install

# Source Code kopieren
COPY . .

# Port exponieren (Vite Dev Server)
EXPOSE 3000

# Development Server starten mit Host binding
CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0"]
