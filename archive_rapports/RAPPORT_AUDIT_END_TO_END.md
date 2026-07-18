# Rapport d'Audit End-to-End - SIGAVT

**Date d'audit** : 16 juillet 2026  
**Application** : SIGAVT (Système de Gestion d'Agence de Voyage Terrestre)  
**Version** : 1.0.0  
**Stack** : Java 17, Spring Boot 2.7.18, H2 Database (dev), Spring Security + JWT  
**Objectif** : Audit complet de l'application API + Frontend pour évaluer la fiabilité et l'état de production

---

## 1. Résumé Exécutif

L'application démarre correctement mais n'est **pas utilisable en production** aujourd'hui. Note globale de fiabilité : **3/10**.

**Justification** : Bien que l'infrastructure Spring Boot soit fonctionnelle (compilation OK, serveur HTTP opérationnel, authentification JWT basique fonctionnelle), de nombreux endpoints critiques retournent des erreurs 500 internes, empêchant toute opération métier réelle. Le frontend est une maquette statique non connectée à l'API. Les données de démonstration sont insuffisantes pour tester les scénarios métier complets.

---

## 2. Tableau des Anomalies

| Module | Description | Comportement observé vs attendu | Sévérité | Étapes de reproduction | Piste de correction |
|--------|-------------|----------------------------------|----------|------------------------|---------------------|
| **Environnement** | Configuration H2 au lieu de MySQL | application.yml configure H2 en mémoire, README mentionne MySQL 8 + Docker | Bloquant | Lecture de application.yml et README | Mettre à jour application.yml pour utiliser MySQL avec docker-compose |
| **Environnement** | Flyway désactivé | spring.flyway.enabled=false alors que migrations existent | Bloquant | Vérification application.yml | Activer Flyway et vérifier migrations MySQL |
| **Authentification** | Compte admin de démo inexistant | README mentionne admin@sigavt.cm/password mais utilisateur absent en base | Bloquant | Tentative de login avec ces identifiants | Créer utilisateur admin via DataInitializer ou SQL initial |
| **Authentification** | Inscription publique force rôle BILLETTERIE | Route publique /api/auth/inscription ignore le rôle demandé | Majeur | POST avec role="ADMIN" sur inscription publique | Documenter ce comportement ou corriger selon métier |
| **Lignes** | POST /api/lignes retourne 500 | Création de ligne échoue avec erreur interne | Bloquant | POST {"villeDepart":"Douala","villeArrivee":"Yaounde",...} | Vérifier logs pour NullPointerException, manquants DTO |
| **Bus** | POST /api/bus retourne 500 | Création de bus échoue avec erreur interne | Bloquant | POST {"immatriculation":"TEST-NEW",...} | Vérifier validation des champs, logs applicatifs |
| **Voyages** | GET /api/voyages retourne 500 + JSON corrompu | Liste des voyages échoue et réponse contient JSON invalide | Bloquant | GET /api/voyages avec token valide | Corriger sérialisation JSON dans VoyageController |
| **Voyages** | POST /api/voyages retourne 500 | Création de voyage impossible | Bloquant | POST avec ligneId, busId, chauffeurId valides | Vérifier VoyageService.creer() et relations |
| **Agences** | POST /api/agences retourne 500 | Création d'agence impossible | Bloquant | POST avec agence valide | Vérifier AgenceService et contraintes validation |
| **Agences** | GET /api/agences retourne vide | Liste vide malgré données attendues | Majeur | GET /api/agences | Vérifier si données initialisées en base |
| **Paie** | GET /api/paie/bulletins retourne 500 | Accès aux bulletins impossible | Bloquant | GET /api/paie/bulletins | Vérifier PaieService et requêtes JPA |
| **Comptabilité** | GET /api/comptabilite/ecritures retourne 500 | Accès aux écritures impossible | Bloquant | GET /api/comptabilite/ecritures | Vérifier ComptabiliteService |
| **Paramètres** | GET /api/parametres retourne 500 | Accès aux paramètres impossible | Bloquant | GET /api/parametres | Vérifier ParametresService |
| **Dashboard** | GET /api/dashboard/departs retourne 500 | Échec récupération départs du jour | Majeur | GET /api/dashboard/departs | Vérifier DashboardService.listerDeparts() |
| **Dashboard** | GET /api/dashboard/recettes-semaine retourne 500 | Échec récupération recettes | Majeur | GET /api/dashboard/recettes-semaine | Vérifier agrégation comptable |
| **Dashboard** | GET /api/dashboard/top-lignes retourne 500 | Échec récupération lignes populaires | Majeur | GET /api/dashboard/top-lignes | Vérifier requêtes d'agrégation |
| **Dashboard** | GET /api/dashboard/alertes retourne 500 | Échec récupération alertes métier | Majeur | GET /api/dashboard/alertes | Vérifier logique d'alertes (visite technique, assurance) |
| **Colis** | POST /api/colis validation incomplète | Champs villeDepart/villeArrivee requis mais non documentés | Majeur | POST sans ces champs | Corriger DTO ColisRequest ou documentation |
| **Colis** | POST /api/colis retourne 500 | Même avec champs valides, création échoue | Bloquant | POST avec tous les champs requis | Vérifier ColisService.creer() |
| **Personnel** | Données présentes mais CRUD non testé | GET fonctionne, POST/PUT/DELETE non testés | Majeur | Tests CRUD sur /api/personnel | Compléter tests POST/PUT/DELETE |
| **CORS** | Configuration CORS trop permissive | allowedOrigins inclut http://localhost:* avec credentials | Majeur | Lecture SecurityConfig.java | Restreindre aux origines spécifiques en production |
| **Données de démo** | Absence de DataInitializer | Aucune donnée de démonstration insérée au démarrage | Bloquant | Vérification base vide sauf rôles | Créer DataInitializer avec scénarios de test |

---

## 3. Ce qui fonctionne bien

1. **Infrastructure Spring Boot** : Compilation réussie, serveur Tomcat démarré sur port 8080
2. **Authentification JWT basique** : Login fonctionnel, token généré et valide, protection des routes par @PreAuthorize
3. **Gestion des erreurs HTTP** : Messages d'erreur en français cohérents, pas de stack traces exposées
4. **Sécurité Spring Security** : Configuration JWTFilter fonctionnelle, rejet des tokens invalides (401)
5. **Endpoints GET de base** : Lignes, Bus, Personnel, Dashboard principal, Billets (vides mais fonctionnels)
6. **Swagger UI** : Accessible sur /swagger-ui.html, documentation disponible
7. **Pagination** : Implémentation Spring Data fonctionnelle sur les endpoints paginés
8. **Validation** : Bean Validation active, erreurs 400 avec messages en français
9. **Frontend statique** : Interface HTML/CSS/JS responsive accessible, navigation entre modules
10. **H2 Console** : Disponible pour développement (à désactiver en production)

---

## 4. Risques de Sécurité

| Risque | Gravité | Description | Recommandation |
|--------|---------|-------------|----------------|
| **CORS permissif** | Élevée | Configuration允许 localhost:* avec credentials | Restreindre aux domaines spécifiques en production |
| **Secret JWT par défaut** | Élevée | Secret JWT encodé en base64 dans application.yml | Générer secret fort via variable d'environnement |
| **Mot de passe admin absent** | Moyenne | Pas de compte admin par défaut, impossible de tester | Créer compte admin sécurisé via DataInitializer |
| **H2 Console exposée** | Moyenne | /h2-console accessible publiquement | Désactiver en production, restreindre par IP |
| **Pas de HTTPS** | Moyenne | Communication HTTP en clair | Configurer HTTPS/TLS en production |
| **Validation d'entrée insuffisante** | Moyenne | Certains endpoints retournent 500 sur payload valide | Renforcer validation et error handling |
| **Rate limiting absent** | Faible | Pas de protection contre brute force | Implémenter rate limiting sur /api/auth/login |

---

## 5. Écart avec le README

| Fonctionnalité annoncée | État réel | Détails |
|------------------------|-----------|---------|
| **MySQL 8 + Docker** | ❌ Non configuré | Application utilise H2 en mémoire, Docker non testé |
| **Données de démonstration** | ❌ Absentes | Aucune donnée insérée automatiquement, base quasi vide |
| **Compte admin@sigavt.cm** | ❌ Inexistant | Création manuelle nécessaire pour tester |
| **Billetterie complète** | ❌ Non fonctionnelle | POST voyages/billets échoue, impossible de vendre |
| **Colis avec tarification** | ❌ Non fonctionnelle | POST colis échoue, tarification non testable |
| **Paie avec calculs CNPS/IRPP** | ❌ Non testable | GET bulletins échoue |
| **Comptabilité bilan** | ❌ Non fonctionnelle | GET écritures/bilan échoue |
| **Dashboard dynamique** | ⚠️ Partiel | /api/dashboard fonctionne mais sous-endpoints échouent |
| **Frontend connecté API** | ❌ Maquette statique | AUDIT_FRONTEND.md confirme aucune intégration API |

---

## 6. Recommandations Priorisées

### Priorité 1 - Bloquants (avant toute utilisation)

1. **Corriger les erreurs 500 internes** sur les endpoints critiques :
   - Voyages (POST/GET)
   - Agences (POST)
   - Bus (POST)
   - Colis (POST)
   - Paie (GET)
   - Comptabilité (GET)
   - Paramètres (GET)
   - Dashboard sous-endpoints

2. **Implémenter les données de démonstration** via DataInitializer ou SQL :
   - Utilisateur admin fonctionnel
   - Lignes de transport
   - Bus avec statuts variés
   - Personnel avec différents postes
   - Agences
   - Voyages planifiés

3. **Activer et configurer MySQL** :
   - Mettre à jour application.yml pour MySQL
   - Activer Flyway
   - Tester avec docker-compose up

### Priorité 2 - Majeurs (fiabilité et sécurité)

4. **Corriger la configuration CORS** pour production
5. **Sécuriser le secret JWT** via variables d'environnement
6. **Désactiver H2 Console** en production
7. **Compléter les tests CRUD** sur tous les modules
8. **Implémenter les validations métier** manquantes

### Priorité 3 - Améliorations (expérience utilisateur)

9. **Connecter le frontend à l'API** (selon AUDIT_FRONTEND.md)
10. **Implémenter les scénarios métier complets** (vente billet, suivi colis)
11. **Ajouter tests unitaires et d'intégration**
12. **Configuration HTTPS/TLS**
13. **Monitoring et logging structuré**

---

## 7. Conclusion Technique

L'application SIGAVT possède une **architecture Spring Boot solide** mais souffre de **problèmes de configuration et d'implémentation** qui la rendent inutilisable en l'état. Les fondations sont bonnes (sécurité, validation, structuration) mais de nombreux services de métier sont incomplets ou défaillants.

**Estimation de travail** : 2-3 semaines de développement pour corriger les bloquants et rendre l'application fonctionnelle pour un environnement de pré-production.

---

**Auditeur** : Devin AI  
**Méthodologie** : Tests end-to-end via curl/PowerShell, analyse de code, vérification configuration  
**Périmètre** : API REST + Frontend statique + Configuration Spring Boot