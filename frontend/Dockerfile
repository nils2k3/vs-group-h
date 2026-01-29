# Build stage
FROM node:20-alpine AS build

WORKDIR /app

# Package files kopieren
COPY package*.json ./

# Dependencies installieren
RUN npm install

# Source Code kopieren
COPY . .

# Vite Build
RUN npm run build

# Production stage
FROM nginx:alpine

# Nginx Config kopieren (optional für besseres Routing)
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Build Artefakte aus Build-Stage kopieren
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
