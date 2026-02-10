# Stage 1: Base
FROM node:22-alpine AS base
WORKDIR /app
COPY package*.json ./
RUN npm install --legacy-peer-deps

# Stage 2: Development
FROM base AS development
RUN npm install -g @angular/cli
COPY . .
CMD ["ng", "serve", "--host", "0.0.0.0", "--poll", "2000"]

# Stage 3: Build
FROM base AS build
COPY . .
RUN npm run build

# Stage 4: Production
FROM nginx:alpine AS production
# Angular 17+ application builder puts files in dist/<project-name>/browser
COPY --from=build /app/dist/rpg-client/browser /usr/share/nginx/html
# Add a custom nginx configuration to handle SPA routing if needed
# COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]