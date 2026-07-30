# SIGAVT - Système Intégré de Gestion d'Agence de Voyage Terrestre

Plateforme de gestion complète pour agences de voyage terrestre au Cameroun.

## 🚀 Technologies

- **Backend**: Java 17, Spring Boot 2.7.18
- **Base de données**: MySQL 8
- **Frontend**: HTML5, JavaScript (Vanilla), CSS3
- **Sécurité**: JWT (JSON Web Tokens)
- **Build**: Maven

## 📋 Prérequis

- Java 17 ou supérieur
- Maven 3.6+
- MySQL 8 (pour la production)
- Node.js 18+ (pour les outils de développement)

## 🎯 Développement sans MySQL (H2)

Pour le développement rapide sans installer MySQL, vous pouvez utiliser la base de données H2 intégrée.

### Configuration H2

Le fichier `application-h2.yml` configure automatiquement :
- Base de données H2 en mode fichier (compatibilité MySQL)
- Console H2 accessible sur `/h2-console`
- Flyway désactivé (DDL auto-update activé)
- Seed data désactivé par défaut

### Démarrage avec H2

```bash
# Activer le profil H2
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```

Ou modifiez `application.yml` pour définir `spring.profiles.active: h2`

### Accès à la console H2

Une fois l'application démarrée :
1. Ouvrez `http://localhost:8081/h2-console`
2. JDBC URL: `jdbc:h2:file:./sigavt`
3. User: `sa`
4. Password: (laisser vide)

⚠️ **Note**: H2 est destiné au développement uniquement. N'utilisez pas H2 en production.

## 🔧 Installation

### 1. Cloner le projet

```bash
git clone <repository-url>
cd sigavt
```

### 2. Configuration de la base de données

Créer une base de données MySQL :

```sql
CREATE DATABASE sigavt CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Variables d'environnement

**IMPORTANT**: Pour un déploiement en production, vous devez configurer les variables d'environnement suivantes. Ne jamais utiliser de valeurs par défaut en clair dans le code source.

#### Variables requises pour la production

```bash
# Base de données
DB_HOST=localhost
DB_PORT=3306
DB_NAME=sigavt
DB_USER=root
DB_PASSWORD=<votre_mot_de_passe_mysql>

# JWT Secret (OBLIGATOIRE en production)
# Générer avec: openssl rand -base64 64 (ou PowerShell: [Convert]::ToBase64String((1..64 | ForEach-Object {Get-Random -Maximum 256})))
JWT_SECRET=<votre_secret_jwt_64_caracteres_minimum>

# CORS
CORS_ALLOWED_ORIGINS=https://votre-domaine.com
```

#### Fichier .env.example

Un fichier `.env.example` est fourni comme modèle. Copiez-le et adaptez-le :

```bash
cp .env.example .env
# Éditez .env avec vos valeurs de production
```

**Génération du JWT_SECRET (PowerShell)** :

```powershell
[Convert]::ToBase64String((1..64 | ForEach-Object {Get-Random -Maximum 256}))
```

**Génération du JWT_SECRET (OpenSSL)** :

```bash
openssl rand -base64 64
```

### 4. Démarrage

#### Avec Maven

```bash
mvn clean install
mvn spring-boot:run
```

#### Avec Docker Compose (recommandé pour la production)

```bash
docker-compose up -d
```

#### Script dédié (Windows)

```bash
start.bat
```

## 🔐 Sécurité

### Configuration JWT

Le secret JWT est obligatoire en production. Sans cette variable, l'application ne démarrera pas correctement en mode production.

- **Développement**: Le secret peut être vide (non recommandé)
- **Production**: Le secret doit être une chaîne aléatoire d'au moins 64 caractères

### Rôles utilisateurs

- **ADMIN**: Accès complet
- **GERANT**: Gestion de l'agence
- **BILLETTERIE**: Vente de billets
- **CONVOYEUR**: Gestion des colis
- **COMPTABLE**: Gestion de la paie
- **RESP_FLOTTE**: Gestion de la flotte

### Compte par défaut

- **Email**: admin@sigavt.cm
- **Mot de passe**: admin123

⚠️ **IMPORTANT**: Changer ce mot de passe dès le premier login en production.

## 📚 Modules

- **Authentification**: Gestion des utilisateurs et rôles
- **Billetterie**: Vente et gestion des billets
- **Colis**: Expédition et suivi des colis
- **Flotte**: Gestion des bus et lignes
- **Personnel**: Gestion des employés
- **Paie**: Gestion des bulletins de paie
- **Comptabilité**: Écritures comptables
- **Courriers**: Gestion de la correspondance
- **Paramètres**: Configuration de l'agence

## � Structure du projet

```
sigavt/
├── src/
│   ├── main/
│   │   ├── java/com/sigavt/       # Code source Java
│   │   │   ├── controller/        # Contrôleurs REST
│   │   │   ├── service/           # Logique métier
│   │   │   ├── repository/        # Accès données (JPA)
│   │   │   ├── security/          # Sécurité JWT
│   │   │   └── model/             # Entités JPA
│   │   └── resources/
│   │       ├── application.yml    # Configuration principale
│   │       ├── application-h2.yml # Configuration H2 (dev)
│   │       └── db/migration/      # Scripts Flyway
│   └── test/                      # Tests unitaires
├── scripts/                       # Scripts utilitaires et tests
│   ├── test_*.py                  # Tests API Python
│   ├── test_*.ps1                 # Tests API PowerShell
│   ├── simulation_*.py            # Simulations
│   ├── cleanup_*.py                # Nettoyage données
│   ├── audit_*.py                 # Audit système
│   ├── donnees_*.sql              # Données de test
│   └── *.png                      # Captures d'écran
├── archive_rapports/               # Rapports archivés
├── pom.xml                        # Configuration Maven
├── docker-compose.yml              # Docker Compose
└── README.md                      # Documentation
```

### Dossier scripts/

Le dossier `scripts/` contient tous les utilitaires de développement et de test :

- **Tests API**: `test_api.py`, `test_api_complet.py`, `test_boutons.py`
- **Simulations**: `simulation_complete.py`, `simulation_api.ps1`
- **Nettoyage**: `cleanup_data.py`, `cleanup_database.ps1`
- **Audit**: `audit_api_endpoints.py`, `audit_complet_v2.py`
- **Données de test**: `donnees_test.sql`, `donnees_test_h2.sql`
- **Rapports**: `ECARTS_SCHEMA.md`, `RAPPORT_*.md`

## �🔌 API Endpoints

### Authentification
- `POST /api/auth/login` - Connexion
- `POST /api/auth/inscription` - Inscription

### Agences
- `GET /api/agences` - Liste des agences
- `POST /api/agences` - Créer une agence
- `PUT /api/agences/{id}` - Modifier une agence
- `DELETE /api/agences/{id}` - Supprimer une agence

### Bus
- `GET /api/bus` - Liste des bus
- `POST /api/bus` - Créer un bus
- `PUT /api/bus/{id}` - Modifier un bus
- `DELETE /api/bus/{id}` - Supprimer un bus

### Lignes
- `GET /api/lignes` - Liste des lignes
- `POST /api/lignes` - Créer une ligne
- `PUT /api/lignes/{id}` - Modifier une ligne
- `DELETE /api/lignes/{id}` - Supprimer une ligne

### Personnel
- `GET /api/personnel` - Liste du personnel
- `POST /api/personnel` - Créer un employé
- `PUT /api/personnel/{id}` - Modifier un employé
- `DELETE /api/personnel/{id}` - Supprimer un employé

### Voyages
- `GET /api/voyages` - Liste des voyages
- `POST /api/voyages` - Créer un voyage
- `PUT /api/voyages/{id}` - Modifier un voyage
- `DELETE /api/voyages/{id}` - Supprimer un voyage

### Billets
- `GET /api/billets` - Liste des billets
- `POST /api/billets` - Créer un billet

### Colis
- `GET /api/colis` - Liste des colis
- `POST /api/colis` - Créer un colis
- `GET /api/colis/tracking/{numero}` - Suivre un colis

### Paie
- `GET /api/paie` - Liste des bulletins
- `POST /api/paie/bulletins` - Générer un bulletin
- `GET /api/paie/bulletins/{id}` - Détails d'un bulletin
- `POST /api/paie/bulletins/{id}/payer` - Marquer comme payé

### Utilisateurs
- `GET /api/utilisateurs` - Liste des utilisateurs (pagination)
- `GET /api/utilisateurs/{id}` - Détails d'un utilisateur
- `GET /api/utilisateurs/agence/{agenceId}` - Utilisateurs par agence
- `POST /api/auth/inscription` - Inscription publique
- `POST /api/auth/utilisateurs` - Créer utilisateur (admin)

## 🐛 Dépannage

### Erreur "JWT invalide"
- Vérifiez que la variable d'environnement `JWT_SECRET` est définie
- Assurez-vous que le secret fait au moins 64 caractères

### Erreur de connexion à la base de données
- Vérifiez les variables `DB_HOST`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`
- Assurez-vous que MySQL est en cours d'exécution

### Port déjà utilisé
- Le serveur utilise par défaut le port 8081
- Modifiez `server.port` dans `application.yml` si nécessaire

## 📝 Scripts utilitaires

Tous les scripts utilitaires sont organisés dans le dossier `scripts/` :

### Tests
- `test_api.py` - Tests de base de l'API
- `test_api_complet.py` - Tests complets
- `test_boutons.py` - Tests interface boutons
- `test_connexion.py` - Tests authentification
- `test_export.py` - Tests export données

### Simulations
- `simulation_complete.py` - Simulation complète du système
- `simulation_api.ps1` - Simulation API PowerShell

### Nettoyage et maintenance
- `cleanup_data.py` - Nettoyage des données incohérentes
- `cleanup_database.ps1` - Nettoyage base de données
- `verifier_corrections.py` - Vérification des corrections

### Audit
- `audit_api_endpoints.py` - Audit des endpoints API
- `audit_complet_v2.py` - Audit complet système

### Données de test
- `donnees_test.sql` - Données de test MySQL
- `donnees_test_h2.sql` - Données de test H2
- `donnees_test_postgresql.sql` - Données de test PostgreSQL

## 📄 Licence

Propriétaire : SIGAVT

## 👥 Équipe

Projet SIGAVT - Gestion d'Agence de Voyage Terrestre
