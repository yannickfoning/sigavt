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
- MySQL 8
- Node.js 18+ (pour les outils de développement)

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

## 🔌 API Endpoints

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

- `donnees_amour_mezam.py` - Insertion de données de test
- `cleanup_data.py` - Nettoyage des données incohérentes
- `audit_api_endpoints.py` - Audit des endpoints API
- `dump_agences.py` - Sauvegarde de la base de données

## 📄 Licence

Propriétaire : SIGAVT

## 👥 Équipe

Projet SIGAVT - Gestion d'Agence de Voyage Terrestre
