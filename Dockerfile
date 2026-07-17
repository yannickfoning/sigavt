# ── Étape 1 : Build ──
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /build

# Copier uniquement pom.xml d'abord pour profiter du cache Docker
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Télécharger les dépendances (layer cachée si pom.xml ne change pas)
RUN ./mvnw dependency:go-offline -q

# Copier le source et compiler
COPY src ./src
RUN ./mvnw clean package -DskipTests -q

# ── Étape 2 : Image finale (légère) ──
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Sécurité : utilisateur non-root
RUN addgroup -S sigavt && adduser -S sigavt -G sigavt

# Créer le dossier de logs
RUN mkdir -p /var/log/sigavt && chown sigavt:sigavt /var/log/sigavt

# Copier le JAR depuis l'étape de build
COPY --from=builder /build/target/*.jar app.jar

# Timezone Africa/Douala
RUN apk add --no-cache tzdata curl && \
    cp /usr/share/zoneinfo/Africa/Douala /etc/localtime && \
    echo "Africa/Douala" > /etc/timezone && \
    apk del tzdata

USER sigavt

EXPOSE 8080

ENTRYPOINT ["java", \
  "-Xms256m", "-Xmx512m", \
  "-XX:+UseG1GC", \
  "-Dfile.encoding=UTF-8", \
  "-Duser.timezone=Africa/Douala", \
  "-jar", "app.jar"]
