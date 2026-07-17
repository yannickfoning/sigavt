# Rapport d'audit Front-end SIGAVT

## Analyse de l'état actuel du front-end

**Date d'audit** : 16 juillet 2026  
**Fichier analysé** : `src/main/resources/static/sigavt.html`  
**Type** : Maquette statique HTML/CSS/JS (2220 lignes)

---

## Synthèse globale

Le front-end actuel est une **maquette statique** avec :
- ✅ Navigation fonctionnelle entre les 10 modules
- ✅ Interface responsive (menu hamburger mobile)
- ✅ Design moderne et cohérent
- ✅ Quelques interactions visuelles (sélection de siège, boutons de paiement)
- ❌ **Aucune persistance de données** (tout est codé en dur)
- ❌ **Authentification simulée** (vérifie localStorage mais pas de vraie connexion)
- ❌ **La majorité des boutons n'ont aucun gestionnaire d'événement**
- ❌ **API calls présents mais backend incomplet**

---

## Audit détaillé par module

### Module 1 : Tableau de bord

| Élément | Comportement actuel constaté | Comportement attendu | Priorité |
|---------|----------------------------|---------------------|----------|
| Carte "Recettes du jour" | Valeur figée "1 245 000 FCFA" | Calculée depuis les vraies transactions du jour | Critique |
| Carte "Billets vendus" | Valeur figée "142" | Compteur réel des billets émis aujourd'hui | Critique |
| Carte "Colis en transit" | Valeur figée "38" | Compteur réel des colis en transit | Critique |
| Carte "Bus en service" | Valeur figée "11" | Compteur réel des bus avec statut OPERATIONNEL | Critique |
| Tableau "Départs du jour" | 6 lignes codées en dur | Liste dynamique des voyages du jour depuis la base | Critique |
| Graphique "Recettes / semaine" | 5 barres avec valeurs fixes | Agrégation réelle des transactions par jour | Critique |
| Section "Alertes" | 3 alertes figées (visite technique, colis, assurance) | Génération dynamique basée sur règles métier | Critique |
| Donut "État de la flotte" | SVG statique avec valeurs codées | Calcul réel depuis la table des bus | Critique |
| Bouton "Voir tout" (Départs) | Redirige vers page Billets | ✅ Fonctionnel | - |
| Bouton cloche notification | Ouvre modale avec 3 notifications fixes | Afficher les vraies alertes actives | Majeur |
| Bouton "+ Nouveau" (topbar) | handleCTA() ne gère que personnel | Ovrir action pertinente pour chaque module | Majeur |

**Note** : La fonction `loadDashboard()` existe et appelle `/api/dashboard` mais les données affichées restent les valeurs par défaut si l'API ne répond pas.

---

### Module 2 : Billets (vente de billets)

| Élément | Comportement actuel constaté | Comportement attendu | Priorité |
|---------|----------------------------|---------------------|----------|
| Sélecteur ville départ | Liste statique (5 villes) | Charger depuis les lignes disponibles | Majeur |
| Sélecteur ville arrivée | Liste statique (5 villes) | Charger depuis les lignes disponibles | Majeur |
| Sélecteur date | Valeur par défaut "2025-05-06" | Date du jour par défaut | Mineur |
| Compteur "Nombre de places" | Boutons −/+ décoratifs (valeur reste "1") | Modifier réellement le nombre de places | Critique |
| Grille "Horaires disponibles" | 3 créneaux fixes avec valeurs statiques | Charger les vrais voyages pour la ligne/date | Critique |
| Sélecteur "Type de tarif" | Liste statique (3 options) | Calculer prix selon le tarif choisi | Majeur |
| Sélecteur "Mode paiement" | Liste statique (3 options) | Enregistrer le mode choisi | Majeur |
| Récapitulatif trajet | Texte fixe "Ydé → Dba \| 06:00" | Se mettre à jour selon les sélections | Critique |
| Plan de bus (sièges) | Disposition statique avec sièges libres/occupés fixes | Charger la vraie disposition du voyage sélectionné | Critique |
| Clic sur siège libre | ✅ Change visuellement (bleu) | Verrouiller temporairement le siège | Critique |
| Bouton "Suivant →" | Aucun gestionnaire d'événement | Passer à l'étape Passager → Siège → Paiement | Critique |
| Étapes 2, 3, 4 | Non implémentées | Formulaire passager, confirmation siège, paiement | Critique |

**Note** : Les interactions visuelles (sélection de siège, boutons horaires) fonctionnent mais sans persistance.

---

### Module 3 : Colis

| Élément | Comportement actuel constaté | Comportement attendu | Priorité |
|---------|----------------------------|---------------------|----------|
| Champs expéditeur/destinataire | Valeurs pré-remplies statiques | Formulaire vide par défaut | Mineur |
| Sélecteur "Ligne" | 2 options fixes | Charger depuis les lignes disponibles | Majeur |
| Sélecteur "Voyage" | 2 options fixes | Charger les vrais voyages de la ligne | Critique |
| Champ "Poids (kg)" | Valeur par défaut "3.5" | Calculer le tarif en temps réel | Critique |
| Checkbox "Fragile" | ✅ Cliquable mais ne recalcule pas le tarif | Recalculer tarif dynamiquement (+300 FCFA) | Critique |
| Checkbox "Urgent" | ✅ Cliquable mais ne recalcule pas le tarif | Recalculer tarif dynamiquement (+800 FCFA) | Critique |
| Checkbox "Assuré" | ✅ Cliquable mais ne recalcule pas le tarif | Recalculer tarif dynamiquement (+500 FCFA) | Critique |
| Section "Calcul du tarif" | Valeurs fixes (1 200 + 300 = 1 500) | Calcul serveur selon poids + options | Critique |
| Boutons mode paiement | ✅ Toggle visuel fonctionnel | Enregistrer le mode choisi | Majeur |
| Bouton "Enregistrer le colis" | **Aucun gestionnaire d'événement** | Créer colis en base, générer tracking unique | Critique |
| Numéro de tracking | Valeur fixe "#COL-2025-00891" | Générer numéro unique après création | Critique |
| Champ recherche tracking | Valeur pré-remplie "VC-2026-00847302" | Champ vide par défaut | Mineur |
| Bouton "Rechercher" | **Aucun gestionnaire d'événement** | Interroger API par numéro de tracking | Critique |
| Carte GPS | SVG statique (illustration) | Clarifier avec commanditaire (GPS réel ou illustratif) | À clarifier |
| Timeline suivi | 5 événements fixes | Afficher l'historique réel du colis consulté | Critique |
| Bouton "SMS de suivi" | **Aucun gestionnaire d'événement** | Envoyer SMS réel via API fournisseur | Majeur |
| Bouton "Télécharger reçu" | **Aucun gestionnaire d'événement** | Générer PDF réel | Majeur |
| Bouton "Signaler" | **Aucun gestionnaire d'événement** | Créer ticket d'incident | Majeur |

**Note** : Le calcul de tarif est affiché mais n'est pas dynamique.

---

### Module 4 : Courriers

| Élément | Comportement actuel constaté | Comportement attendu | Priorité |
|---------|----------------------------|---------------------|----------|
| Page entière | **Placeholder** avec message "Module Courriers" | Concevoir module complet (liste, création, détails) | Critique |
| Badge "3" (menu) | Valeur fixe | Compteur réel de courriers non lus | Critique |
| Bouton "+ Courrier" | **Aucun gestionnaire d'événement** | Ouvrir formulaire de création | Critique |

**Note** : Ce module est entièrement à concevoir.

---

### Module 5 : Flotte (Bus)

| Élément | Comportement actuel constaté | Comportement attendu | Priorité |
|---------|----------------------------|---------------------|----------|
| Compteur "Total bus" | Valeur fixe "14" | Compter depuis la table des bus | Critique |
| Compteur "En service" | Valeur fixe "11" | Compter les bus avec statut OPERATIONNEL | Critique |
| Compteur "En maintenance" | Valeur fixe "2" | Compter les bus avec statut MAINTENANCE | Critique |
| Compteur "Hors service" | Valeur fixe "1" | Compter les bus avec statut HORS_SERVICE | Critique |
| Tableau "Parc de véhicules" | 6 lignes codées en dur | Charger depuis la base | Critique |
| Bouton "+ Bus" | **Aucun gestionnaire d'événement** | Ouvrir formulaire de création bus | Critique |
| Lignes cliquables | Non cliquables | Permettre modification/suppression | Majeur |

**Note** : Les données sont entièrement statiques.

---

### Module 6 : Personnel

| Élément | Comportement actuel constaté | Comportement attendu | Priorité |
|---------|----------------------------|---------------------|----------|
| Champ recherche | Input présent mais non fonctionnel | Filtrer la liste par nom/téléphone | Majeur |
| Filtre "Tous postes" | Select présent mais non fonctionnel | Filtrer par poste | Majeur |
| Filtre "Actifs seuls" | Select présent mais non fonctionnel | Filtrer par statut | Majeur |
| Liste employés | 5 fiches codées en dur | Charger depuis la base | Critique |
| Bouton "+ Employé" | ✅ Ouvre la modale | - |
| Modale "Ajouter employé" | ✅ S'ouvre et se ferme | - |
| Bouton "Enregistrer l'employé" | **Aucun gestionnaire d'événement** | Créer employé en base | Critique |
| Boutons "Modifier" | Présents sur 2 fiches seulement | Présents sur toutes les fiches | Majeur |
| Mini bulletin paie | Valeurs fixes pour Nkoa Jean-Pierre | Cohérent avec le module Paie | Majeur |
| Mini état flotte | 3 bus fixes | Charger depuis le module Flotte | Majeur |

**Note** : La fonction `loadPersonnel()` existe et appelle `/api/personnel` mais les données affichées restent les valeurs par défaut.

---

### Module 7 : Paie

| Élément | Comportement actuel constaté | Comportement attendu | Priorité |
|---------|----------------------------|---------------------|----------|
| Compteur "Masse salariale brute" | Valeur fixe "3 620 000" | Calculer depuis les fiches du mois | Critique |
| Compteur "Net à payer total" | Valeur fixe "3 087 400" | Calculer depuis les fiches du mois | Critique |
| Compteur "Cotisations CNPS" | Valeur fixe "405 440" | Calculer depuis les fiches du mois | Critique |
| Compteur "Bulletins générés" | Valeur fixe "14/14" | Compter les fiches du mois | Critique |
| Tableau "Fiches de paie" | 5 lignes codées en dur | Charger depuis la base | Critique |
| Clic sur ligne (selectBulletin) | ✅ Change la classe CSS mais ne met pas à jour le détail | Mettre à jour le panneau détail avec les vraies données | Critique |
| Panneau "Bulletin détail" | Toujours Nkoa Jean-Pierre | Afficher l'employé cliqué | Critique |
| Calculs (brut, CNPS, IRPP, net) | Valeurs fixes | Calcul serveur avec taux paramétrables | Critique |
| Filtre "Tous postes" | Select présent mais non fonctionnel | Filtrer par poste | Majeur |
| Filtre "Tous statuts" | Select présent mais non fonctionnel | Filtrer par statut de paiement | Majeur |
| Champ recherche | Input présent mais non fonctionnel | Rechercher par nom | Majeur |
| Bouton "Exporter tout" | **Aucun gestionnaire d'événement** | Exporter CSV/Excel des fiches | Majeur |
| Bouton "+ Fiche de paie" | **Aucun gestionnaire d'événement** | Ouvrir nouveau formulaire de génération | Critique |
| Bouton "Imprimer PDF" | **Aucun gestionnaire d'événement** | Générer PDF réel | Majeur |
| Bouton "Envoyer par SMS" | **Aucun gestionnaire d'événement** | Envoyer SMS réel | Majeur |
| Boutons mode paiement | ✅ Toggle visuel fonctionnel | Enregistrer le mode choisi | Majeur |

**Note** : La fonction `selectBulletin()` est appelée mais n'est pas définie dans le JavaScript.

---

### Module 8 : Comptabilité

| Élément | Comportement actuel constaté | Comportement attendu | Priorité |
|---------|----------------------------|---------------------|----------|
| Compteur "Recettes mai" | Valeur fixe "18 540 000" | Calculer depuis le journal du mois | Critique |
| Compteur "Dépenses mai" | Valeur fixe "12 380 000" | Calculer depuis le journal du mois | Critique |
| Compteur "Bénéfice net" | Valeur fixe "6 160 000" | Calculer depuis le journal du mois | Critique |
| Compteur "Marge" | Valeur fixe "33%" | Calculer depuis le journal du mois | Critique |
| Tableau "Journal comptable" | 5 lignes codées en dur | Charger depuis la base avec écritures automatiques | Critique |
| Écritures automatiques | Non implémentées | Générer automatiquement (vente billet → recette, paie → dépense, etc.) | Critique |
| Bouton "+ Écriture" (handleCTA) | Non géré pour comptabilité | Permettre ajout manuel d'écriture | Majeur |

**Note** : La fonction `loadComptabilite()` existe et appelle `/api/comptabilite/ecritures` mais les données affichées restent les valeurs par défaut.

---

### Module 9 : Lignes

| Élément | Comportement actuel constaté | Comportement attendu | Priorité |
|---------|----------------------------|---------------------|----------|
| Tableau "Gestion des Lignes" | 5 lignes codées en dur | Charger depuis la base | Critique |
| Taux de remplissage | Valeurs fixes (93%, 70%, 60%, 45%, 33%) | Calculer depuis les réservations réelles | Critique |
| Bouton "+ Ligne" | **Aucun gestionnaire d'événement** | Ouvrir formulaire de création ligne | Critique |
| Lignes cliquables | Non cliquables | Permettre modification/suppression | Majeur |

**Note** : La fonction `loadLignes()` existe et appelle `/api/lignes` mais les données affichées restent les valeurs par défaut.

---

### Module 10 : Paramètres

| Élément | Comportement actuel constaté | Comportement attendu | Priorité |
|---------|----------------------------|---------------------|----------|
| Champ "Nom de l'agence" | Valeur pré-remplie fixe | Charger depuis la base | Majeur |
| Champ "Téléphone" | Valeur pré-remplie fixe | Charger depuis la base | Majeur |
| Champ "Email" | Valeur pré-remplie fixe | Charger depuis la base | Majeur |
| Sélecteur "Ville principale" | 2 options fixes | Charger depuis la base | Majeur |
| Champ "Adresse" | Valeur pré-remplie fixe | Charger depuis la base | Majeur |
| Bouton "Annuler" | **Aucun gestionnaire d'événement** | Recharger les valeurs depuis la base | Mineur |
| Bouton "Sauvegarder" | **Aucun gestionnaire d'événement** | Persister en base | Critique |

**Note** : Aucune fonction `loadParametres()` n'existe dans le JavaScript.

---

## Audit transversal

### Authentification

| Élément | Comportement actuel constaté | Comportement attendu | Priorité |
|---------|----------------------------|---------------------|----------|
| Vérification token au chargement | ✅ Vérifie localStorage `sigavt_token` | ✅ Fonctionnel mais token non validé côté serveur | Majeur |
| Redirection si non connecté | ✅ Redirige vers `/login` | ✅ Fonctionnel | - |
| Affichage utilisateur | Charge depuis localStorage `sigavt_user` | ✅ Fonctionnel mais données non rafraîchies | Majeur |
| Bouton "Déconnexion" | ✅ Supprime token et user, redirige | ✅ Fonctionnel | - |
| Page de connexion | Fichier `login.html` existe mais non analysé | Vérifier implémentation complète | À vérifier |

**Note** : L'authentification est simulée côté client. Il faut implémenter une vraie validation JWT côté serveur.

---

### Cohérence des données

| Problème constaté | Impact | Priorité |
|-------------------|--------|----------|
| Bus codés en dur dans plusieurs modules (Personnel, Flotte, Billets) | Duplication de données, incohérences possibles | Critique |
| Employés codés en dur (Personnel, Paie) | Duplication de données | Critique |
| Lignes codées en dur (Billets, Colis, Lignes) | Duplication de données | Critique |
| Pas de source unique de vérité | Données incohérentes entre modules | Critique |

---

### États de chargement et validation

| Problème constaté | Impact | Priorité |
|-------------------|--------|----------|
| Aucun état de chargement (spinners, skeletons) | Mauvaise UX pendant les appels API | Majeur |
| Aucune gestion d'erreur visible | Utilisateur ne sait pas quand une action échoue | Critique |
| Validation des champs obligatoires (marqués *) | Non implémentée | Critique |
| Pas de feedback visuel succès/échec | Utilisateur ne sait pas si l'action a réussi | Critique |

---

### Responsive

| Élément | État | Priorité |
|---------|------|----------|
| Menu hamburger mobile | ✅ Fonctionnel | - |
| Grilles empilées sur mobile | ✅ Fonctionnel | CSS existant |
| Indicateur d'étapes (steps) | Masqué sur mobile (display: none) | ✅ Acceptable pour maquette |

---

## Fonctions JavaScript existantes

### Fonctions API (loaders)

- ✅ `api(endpoint, options)` - Client API générique avec gestion token
- ✅ `loadDashboard()` - Appelle `/api/dashboard`
- ✅ `loadBillets()` - Appelle `/api/billets`
- ✅ `loadColis()` - Appelle `/api/colis`
- ✅ `loadPersonnel()` - Appelle `/api/personnel`
- ✅ `loadPaie()` - Appelle `/api/paie/bulletins?periode=...`
- ✅ `loadFlotte()` - Appelle `/api/bus`
- ✅ `loadComptabilite()` - Appelle `/api/comptabilite/ecritures` et `/api/comptabilite/resume`
- ✅ `loadLignes()` - Appelle `/api/lignes`
- ✅ `loadCourriers()` - Appelle `/api/courriers`
- ❌ `loadParametres()` - **Non existante**

### Fonctions utilitaires

- ✅ `formatFCFA(amount)` - Formatage monétaire
- ✅ `formatDate(isoDate)` - Formatage date
- ✅ `getStatutBadge(statut)` - Mapping statut → classe CSS
- ✅ `navigate(pageId)` - Navigation entre pages
- ✅ `handleCTA()` - Gère bouton "+ Nouveau" (personnel seulement)
- ✅ `showNotifs()` - Ouvre modale notifications
- ✅ `openSidebar()` / `closeSidebar()` - Menu mobile
- ✅ `logout()` - Déconnexion
- ✅ `checkAuth()` - Vérification authentification
- ✅ `updateUserInfo()` - Mise à jour affichage utilisateur

### Fonctions manquantes ou non implémentées

- ❌ `selectBulletin(id)` - Appelée mais non définie
- ❌ `showFicheDetail(id)` - Appelée mais non définie
- ❌ Gestionnaires d'événement pour les formulaires (création employé, bus, ligne, etc.)
- ❌ Gestionnaires pour les boutons d'action (enregistrer, modifier, supprimer, exporter, imprimer)

---

## Points à clarifier avec le commanditaire

1. **Stack technique backend** : Spring Boot 2.7.18 déjà en place - à confirmer
2. **Intégration Orange Money/MTN MoMo** : API réelle ou simple enregistrement du mode ?
3. **Taux CNPS/IRPP** : Confirmer les taux légaux actuels et les rendre configurables
4. **Fournisseur SMS** : Quel API pour l'envoi de SMS ?
5. **Suivi GPS colis** : Géolocalisation réelle ou statut textuel avec carte illustrative ?
6. **Rôles et permissions** : Définir précisément les droits de chaque rôle

---

## Conclusion

Le front-end est une **maquette visuelle de qualité** avec une structure solide, mais **aucune fonctionnalité métier n'est réellement opérationnelle**. Toutes les données sont codées en dur et la majorité des actions utilisateurs ne sont pas connectées à un backend.

**Statut global** : 0% fonctionnel en termes de persistance et logique métier.

**Prochaines étapes** :
1. Compléter le backend Spring Boot (contrôleurs manquants déjà créés)
2. Implémenter la logique métier dans les services
3. Configurer la base de données et les migrations Flyway
4. Connecter le front-end aux API endpoints
5. Implémenter les gestionnaires d'événements manquants
6. Ajouter la validation des formulaires
7. Ajouter les états de chargement et gestion d'erreur
