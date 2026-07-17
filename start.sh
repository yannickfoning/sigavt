#!/bin/bash
# ══════════════════════════════════════════════════════════════
# SIGAVT — Script de démarrage universel
# Usage:
#   ./start.sh           → dev (MySQL local, port 8080)
#   ./start.sh prod      → production (variables d'env requises)
#   ./start.sh docker    → tout via Docker Compose
#   ./start.sh stop      → arrêter tout
# ══════════════════════════════════════════════════════════════

set -e
PROFILE="${1:-dev}"
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

print_banner() {
  echo "════════════════════════════════════════"
  echo "  🚌 SIGAVT — Démarrage ($PROFILE)"
  echo "════════════════════════════════════════"
}

check_java() {
  if ! command -v java &>/dev/null; then
    echo "❌ Java non trouvé. Installer Java 17."
    exit 1
  fi
  JAVA_VER=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d. -f1)
  if [ "$JAVA_VER" -lt 17 ]; then
    echo "❌ Java 17 minimum requis (trouvé: Java $JAVA_VER)"
    exit 1
  fi
  echo "✅ Java $JAVA_VER détecté"
}

check_mysql() {
  echo -n "⏳ Attente MySQL (localhost:3306)..."
  for i in $(seq 1 30); do
    if mysqladmin ping -h 127.0.0.1 -u "${DB_USER:-sigavt_user}" \
       -p"${DB_PASSWORD:-sigavt_dev_2025}" --silent 2>/dev/null; then
      echo " ✅"
      return 0
    fi
    echo -n "."
    sleep 2
  done
  echo " ❌"
  echo "MySQL non disponible après 60 secondes."
  return 1
}

build_jar() {
  echo "🔨 Compilation..."
  MVNW="$SCRIPT_DIR/mvnw"
  if [ ! -f "$MVNW" ]; then
    MVNW="mvn"
  fi
  $MVNW clean package -DskipTests -q
  JAR=$(ls target/*.jar 2>/dev/null | head -1)
  if [ -z "$JAR" ]; then
    echo "❌ JAR non trouvé après compilation"
    exit 1
  fi
  echo "✅ JAR : $JAR"
  echo "$JAR"
}

case "$PROFILE" in
  docker)
    print_banner
    echo "🐳 Démarrage via Docker Compose..."
    docker compose up -d
    echo "⏳ Attente du health check..."
    for i in $(seq 1 30); do
      sleep 3
      STATUS=$(curl -s http://localhost:8080/actuator/health 2>/dev/null | python3 -c "import sys,json; print(json.load(sys.stdin).get('status',''))" 2>/dev/null)
      if [ "$STATUS" = "UP" ]; then
        echo "✅ Application démarrée → http://localhost:8080"
        exit 0
      fi
      echo "  ... attente ($((i*3))s)"
    done
    echo "⚠️  Vérifier les logs: docker compose logs app"
    ;;

  prod)
    print_banner
    check_java
    # Vérifier les variables d'environnement requises
    for VAR in DB_USER DB_PASSWORD JWT_SECRET; do
      if [ -z "${!VAR}" ]; then
        echo "❌ Variable d'environnement manquante : $VAR"
        exit 1
      fi
    done
    check_mysql
    JAR=$(ls target/*.jar 2>/dev/null | head -1)
    if [ -z "$JAR" ]; then JAR=$(build_jar); fi
    echo "🚀 Démarrage en mode production..."
    java -Xms256m -Xmx512m \
         -XX:+UseG1GC \
         -Dspring.profiles.active=prod \
         -Dfile.encoding=UTF-8 \
         -Duser.timezone=Africa/Douala \
         -jar "$JAR" &
    APP_PID=$!
    echo "   PID: $APP_PID"
    echo $APP_PID > /tmp/sigavt.pid
    ;;

  stop)
    echo "🛑 Arrêt de SIGAVT..."
    if [ -f /tmp/sigavt.pid ]; then
      kill $(cat /tmp/sigavt.pid) 2>/dev/null && echo "✅ Application arrêtée"
      rm /tmp/sigavt.pid
    fi
    docker compose down 2>/dev/null && echo "✅ Docker Compose arrêté"
    ;;

  dev|*)
    print_banner
    check_java
    # Charger .env si présent
    [ -f .env ] && export $(grep -v '^#' .env | xargs)
    MVNW="$SCRIPT_DIR/mvnw"
    [ ! -f "$MVNW" ] && MVNW="mvn"
    echo "🚀 Démarrage en mode développement..."
    $MVNW spring-boot:run -Dspring-boot.run.profiles=dev
    ;;
esac
