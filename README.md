# SIGAVT — Système de Gestion d'Agence de Voyage Terrestre

Application web full-stack **Java 17 / Spring Boot 2.7.18** + **MySQL 8**, conçue pour la gestion
d'une agence de transport au Cameroun : billetterie, colis, flotte de bus, personnel, paie, comptabilité.

## 1. Stack technique

- Java 17
- Spring Boot 2.7.18
- Spring Web, Spring Data JPA, Spring Security + JWT
- MySQL 8 (développement et production)
- Lombok, Bean Validation
- Swagger / OpenAPI (`/swagger-ui.html`)
- HTML5, CSS3, JavaScript Vanilla (sans framework)

## 2. Architecture des modules

| Module        | Description                                                        |
|---------------|----------------------------------------------------------------------|
| Auth          | Connexion JWT, inscription d'utilisateurs (rôles)                   |
| Agences       | Gestion des agences                                                  |
| Lignes        | Lignes de transport (Yaoundé→Douala, etc.), tarifs, distance         |
| Bus / Flotte  | Parc de véhicules, statut, entretien, assurance                      |
| Personnel     | Employés (chauffeurs, billetterie, convoyeurs...), contrats          |
| Voyages       | Départs planifiés par ligne/bus/date/heure, génération auto des sièges|
| Billets       | Vente de billets, sélection de siège, tarifs (plein/demi/groupe)      |
| Colis         | Enregistrement, tarification au poids, suivi temps réel (timeline)   |
| Courriers     | Courriers internes/externes                                          |
| Paie          | Bulletins de paie avec calcul CNPS (4,2%/11,2%) et IRPP simplifié    |
| Comptabilité  | Journal des écritures, résumé mensuel (recettes/dépenses/marge)      |
| Paramètres    | Configuration de l'agence                                             |
| Dashboard     | Indicateurs et alertes (visite technique, assurance, colis non réclamés) |

⚠️ **Le calcul IRPP/CNPS est une approximation à but démonstratif.** Faites valider 
les taux et le barème exact (CGI, CNPS) par un expert-comptable camerounais avant toute 
utilisation réelle pour la paie.

## 3. Installation

### Prérequis

- Java 17 ou supérieur
- Maven 3.6+ (ou utiliser le Maven Wrapper inclus)
- MySQL 8.0+ (obligatoire pour le développement et la production)

### Compiler et lancer l'application (développement)

```bash
./mvnw clean install
./mvnw spring-boot:run
```

Pour Windows :
```bash
mvnw.cmd clean install
mvnw.cmd spring-boot:run
```

L'application utilise MySQL pour le développement et la production. Assurez-vous que MySQL est démarré
et que la base de données `sigavt` existe avant de lancer l'application.

Des données initiales (rôles, agence, compte admin) sont insérées automatiquement au démarrage via `DataLoader.java`.

L'API est disponible sur `http://localhost:8080/api`.
Documentation interactive : `http://localhost:8080/swagger-ui.html`.

### Configuration MySQL

Créez la base de données MySQL :

```sql
CREATE DATABASE sigavt CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Configurez les accès dans `src/main/resources/application-dev.yml` (par défaut : root sans mot de passe sur localhost:3306).

## 4. Accès à l'application

- **Interface principale :** http://localhost:8080/sigavt.html
- **Documentation API :** http://localhost:8080/swagger-ui.html

### Compte de démonstration

Les comptes suivants sont créés automatiquement au démarrage :

- **Administrateur** : `admin@sigavt.cm` / `admin123` (rôle ADMIN)
- **Gérant** : `gerant@sigavt.cm` / `gerant123` (rôle GERANT)
- **Billetterie** : `billetterie@sigavt.cm` / `billet123` (rôle BILLETTERIE)

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@sigavt.cm","motDePasse":"admin123"}'
```

## 5. Structure des fichiers

```
d:\sigavt\
├── pom.xml                    # Configuration Maven
├── mvnw, mvnw.cmd             # Maven Wrapper
├── docker-compose.yml         # Configuration MySQL (production)
├── src\
│   ├── main\
│   │   ├── java\com\sigavt\
│   │   │   ├── SigavtApplication.java
│   │   │   ├── config\
│   │   │   ├── controller\
│   │   │   ├── dto\
│   │   │   ├── entity\
│   │   │   ├── enums\
│   │   │   ├── exception\
│   │   │   ├── repository\
│   │   │   ├── security\
│   │   │   └── service\
│   │   └── resources\
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── db\migration\
│   │       └── static\
│   │           ├── sigavt.html
│   │           ├── login.html
│   │           └── style.css
```

## 6. Données initiales

L'application inclut un `DataLoader.java` qui insère automatiquement au démarrage (profil dev uniquement si la base est vide) :
- Rôles par défaut (ADMIN, GERANT, BILLETTERIE, CONVOYEUR, COMPTABLE, RESP_FLOTTE)
- Une agence par défaut (Yaoundé)
- Un compte administrateur (admin@sigavt.cm / admin123)

Ces données minimales permettent de démarrer l'application. Les données de production (lignes, bus, personnel, voyages) doivent être créées via l'interface d'administration.

## 8. Statut actuel et limitations

### Backend API
- ✅ Authentification JWT fonctionnelle
- ✅ CRUD complet pour la plupart des modules
- ✅ Gestion des erreurs avec messages en français
- ✅ Pagination et validation Bean
- ✅ Swagger UI disponible

### Frontend
- ⚠️ Interface statique partiellement connectée à l'API
- ⚠️ Certains boutons et formulaires ne sont pas encore câblés
- ⚠️ Les formulaires de création pour certains modules sont à compléter
- ✅ Navigation responsive entre les modules
- ✅ Authentification et déconnexion fonctionnelles

Voir `AUDIT_FRONTEND.md` pour un audit détaillé de l'état du frontend.

## 9. Sécurité — points à durcir avant production

- Changer `app.jwt.secret` dans `application.yml` (valeur par défaut encodée en base64)
- Changer les mots de passe de démonstration par défaut
- Restreindre `app.cors.allowed-origins` au(x) domaine(s) réel(s) du frontend
- Désactiver la console H2 en production
- Mettre les paiements Orange Money / MTN MoMo derrière une vraie intégration API
- Configurer HTTPS/TLS pour la production

## 10. Fonctionnalités principales

### Billetterie
- Vente de billets en 4 étapes (trajet → passager → siège → paiement)
- Plan de siège interactif
- Génération automatique du numéro de billet (BIL-YYYY-XXXXX)
- Annulation de billets

### Colis
- Enregistrement de colis avec tarification au poids
- Options : fragile, urgent, assuré
- Suivi en temps réel par numéro de tracking
- Timeline des événements

### Personnel
- Gestion des employés (chauffeurs, billetterie, convoyeurs, comptables)
- Contrats CDI/CDD
- Assignation aux bus et agences

### Paie
- Génération automatique des fiches de paie
- Calcul CNPS salarié (4,2%) et patronal (11,2%)
- Calcul IRPP progressif camerounais
- Marquage comme payé avec mode de paiement

### Flotte
- Gestion du parc de bus
- Suivi des visites techniques et assurances
- Statuts : opérationnel, maintenance, hors service

### Comptabilité
- Journal des écritures
- Bilan mensuel (recettes/dépenses/bénéfice/marge)
- Catégories : billets, colis, salaires, carburant, maintenance, assurance

### Courriers
- Courriers internes/externes
- Priorité normale/urgente
- Statut non-lu/lu/archivé

## 11. Rapports et audits

- `RAPPORT_AUDIT_END_TO_END.md` : Audit complet de l'application (juillet 2026)
- `AUDIT_FRONTEND.md` : Audit détaillé de l'interface frontend

Ces documents contiennent l'analyse de l'état actuel, les anomalies identifiées et les recommandations pour l'amélioration de la plateforme.
