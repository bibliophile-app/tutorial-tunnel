FROM node:20

WORKDIR /app

COPY package*.json ./

RUN npm install --include=dev

COPY . .

EXPOSE 3000

ENV PATH=/app/node_modules/.bin:$PATH
CMD ["npm", "run", "dev"]
