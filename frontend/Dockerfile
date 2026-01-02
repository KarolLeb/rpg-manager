FROM node:20-alpine

WORKDIR /app

RUN npm install -g @angular/cli

COPY package.json package-lock.json ./
RUN npm install

COPY . .

CMD ["ng", "serve", "--host", "0.0.0.0", "--poll", "2000", "--disable-host-check"]
