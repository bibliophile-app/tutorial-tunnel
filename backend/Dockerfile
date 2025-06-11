# Usa a imagem oficial do OpenJDK 17 com Gradle
FROM gradle:8.5-jdk17 AS build

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia apenas os arquivos de configuração para aproveitar cache de dependências
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle ./gradle

# Baixa as dependências antes de copiar o restante do código
RUN gradle dependencies --no-daemon

# Copia o código-fonte para dentro do container
COPY . .

# Porta do Ktor
EXPOSE 8080