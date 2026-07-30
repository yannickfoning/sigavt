# ECARTS_SCHEMA — Analyse des écarts entre schéma existant et besoins fonctionnels

**Date** : 21 juillet 2026  
**Version du schéma** : V1__init.sql + V2__add_indexes.sql (archivés)  
**Objectif** : Documenter les incohérences entre la base de données existante et les exigences fonctionnelles du projet SIGAVT.

---

## Résumé exécutif

Le schéma existant couvre **environ 85%** des besoins fonctionnels. Les écarts identifiés sont principalement :

1. **Table manquante** pour la configuration des tarifs de colis (BaremeTarifColis)
2. **Champs manquants** dans la table `colis` (ligne_id, voyage_id)
3. **Incohérences de nommage** dans les index (V2__add_indexes.sql)
4. **Champs redondants** dans `bulletins_paie`
5. **Absence de table de configuration** pour les seuils d'alerte et barèmes IRPP

---

## Écart #1 : Table BaremeTarifColis manquante

### Besoin fonctionnel
Le prompt exige un système de tarification des colis **configurable** depuis les Paramètres, avec :
- Tranches de poids (ex: 1–5 kg = 1 200 FCFA)
- Suppléments optionnels : Fragile (+300), Urgent (+800), Assuré (+500)
- Ces montants doivent être modifiables, pas codés en dur

### État actuel du schéma
Aucune table `bareme_tarif_colis` ou équivalent n'existe. Les tarifs seraient codés en dur dans le code métier.

### Impact
- **Critique** : Impossible de modifier les tarifs sans redéployer l'application
- Violation du principe de configuration centralisée

### Proposition de migration (Flyway)

```sql
-- V3__create_bareme_tarif_colis.sql
CREATE TABLE bareme_tarif_colis (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    poids_min DECIMAL(5,2) NOT NULL,
    poids_max DECIMAL(5,2) NOT NULL,
    tarif_base DECIMAL(10,2) NOT NULL,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification DATETIME,
    CONSTRAINT uq_tranche_poids UNIQUE (poids_min, poids_max, actif)
);

-- Table pour les suppléments configurables
CREATE TABLE supplément_colis (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type_supplement VARCHAR(20) NOT NULL UNIQUE, -- 'FRAGILE', 'URGENT', 'ASSURE'
    montant DECIMAL(10,2) NOT NULL,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Données initiales correspondant à la maquette
INSERT INTO bareme_tarif_colis (poids_min, poids_max, tarif_base) VALUES
(0.01, 5.00, 1200.00),
(5.01, 10.00, 2000.00),
(10.01, 20.00, 3500.00),
(20.01, 50.00, 6000.00);

INSERT INTO supplément_colis (type_supplement, montant) VALUES
('FRAGILE', 300.00),
('URGENT', 800.00),
('ASSURE', 500.00);
```

---

## Écart #2 : Champs manquants dans la table colis

### Besoin fonctionnel
Le prompt stipule que chaque colis doit être lié à une **ligne** et un **voyage** spécifiques, pour :
- Le suivi du trajet
- La génération d'écritures comptables associées au voyage
- L'affichage dans le dashboard des colis par voyage

### État actuel du schéma
```sql
CREATE TABLE colis (
    -- ... autres champs ...
    agence_id BIGINT,
    agent_id BIGINT,
    -- MANQUE : ligne_id BIGINT
    -- MANQUE : voyage_id BIGINT
);
```

### Impact
- **Moyen** : Impossible d'associer un colis à un voyage spécifique
- Difficulté pour le suivi et la comptabilité par voyage

### Proposition de migration

```sql
-- V4__add_colis_ligne_voyage.sql
ALTER TABLE colis 
ADD COLUMN ligne_id BIGINT,
ADD COLUMN voyage_id BIGINT,
ADD CONSTRAINT fk_colis_ligne FOREIGN KEY (ligne_id) REFERENCES lignes(id),
ADD CONSTRAINT fk_colis_voyage FOREIGN KEY (voyage_id) REFERENCES voyages(id);

-- Index pour performances
CREATE INDEX idx_colis_ligne ON colis(ligne_id);
CREATE INDEX idx_colis_voyage ON colis(voyage_id);
```

---

## Écart #3 : Incohérences de nommage dans les index (V2__add_indexes.sql)

### Problème
Le fichier `V2__add_indexes.sql` contient des références à des noms de table incorrects :

| Ligne | Référence dans V2 | Nom réel dans V1 | Statut |
|-------|------------------|------------------|--------|
| 25 | `employes` | `personnel` | **Erreur** |
| 30 | `fiches_paie` | `bulletins_paie` | **Erreur** |
| 34-36 | `ecritures` | `ecritures_comptables` | **Erreur** |

### Impact
- **Faible** : Les index ne seront pas créés correctement
- Performances dégradées en production

### Proposition de correction

```sql
-- V2__add_indexes.sql (version corrigée)
-- Remplacer les références incorrectes :

-- Personnel (pas employes)
CREATE INDEX IF NOT EXISTS idx_personnel_poste ON personnel(poste);
CREATE INDEX IF NOT EXISTS idx_personnel_statut ON personnel(statut);
CREATE INDEX IF NOT EXISTS idx_personnel_agence ON personnel(agence_id);

-- Bulletins de paie (pas fiches_paie)
CREATE INDEX IF NOT EXISTS idx_bulletins_paie_periode ON bulletins_paie(periode);
CREATE INDEX IF NOT EXISTS idx_bulletins_paie_personnel ON bulletins_paie(personnel_id);

-- Écritures comptables (pas ecritures)
CREATE INDEX IF NOT EXISTS idx_ecritures_comptables_date ON ecritures_comptables(date_ecriture);
CREATE INDEX IF NOT EXISTS idx_ecritures_comptables_categorie ON ecritures_comptables(categorie);
```

---

## Écart #4 : Champs redondants dans bulletins_paie

### Problème
La table `bulletins_paie` contient des champs redondants ou en double :

```sql
-- Champs en double ou redondants :
heures_sup DECIMAL(10,2) DEFAULT 0,           -- Ligne 31
heures_supplementaires DECIMAL(10,2) DEFAULT 0, -- Ligne 61 (DOUBLON)

cnps_salarie DECIMAL(10,2),                   -- Ligne 43
cotisation_cnps DECIMAL(10,2),                -- Ligne 70 (DOUBLON)

cnps_patronal DECIMAL(10,2),                  -- Ligne 46
cnps_patronal (absent mais charges_patronales existe) -- Ligne 79

statut_paiement VARCHAR(20),                  -- Ligne 93
statut StatutPaiement (enum)                  -- Ligne 91 (DOUBLON)
```

### Impact
- **Moyen** : Confusion sur quel champ utiliser
- Risque d'incohérence entre les doublons
- Occupation inutile d'espace

### Proposition de nettoyage

```sql
-- V5__cleanup_bulletins_paie.sql
-- Supprimer les doublons après migration des données
ALTER TABLE bulletins_paie 
DROP COLUMN heures_sup,  -- Garder heures_supplementaires
DROP COLUMN cnps_salarie, -- Garder cotisation_cnps
DROP COLUMN statut_paiement; -- Garder l'enum statut

-- Ajouter un champ manquant : lien avec l'écriture comptable générée
ALTER TABLE bulletins_paie
ADD COLUMN ecriture_comptable_id BIGINT,
ADD CONSTRAINT fk_bulletin_ecriture FOREIGN KEY (ecriture_comptable_id) REFERENCES ecritures_comptables(id);
```

---

## Écart #5 : Absence de table de configuration pour les seuils et barèmes

### Besoin fonctionnel
Le prompt exige que les éléments suivants soient **configurables** (pas codés en dur) :
- Seuils d'alerte flotte (visite technique < 15 jours, assurance expirée)
- Seuils de taux de remplissage (vert ≥ 70%, orange 45–69%, rouge < 45%)
- Barème IRPP camerounais (tranches d'imposition)
- Taux CNPS (4.2% salarié, 11.2% employeur)

### État actuel
La table `parametres_agence` n'existe que pour les infos de base de l'agence (nom, téléphone, email...). Aucune configuration métier.

### Impact
- **Moyen** : Modification des seuils nécessite une modification de code
- Difficile à adapter aux changements réglementaires (IRPP, CNPS)

### Proposition de migration

```sql
-- V6__create_configuration_metier.sql
CREATE TABLE configuration_metier (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cle VARCHAR(50) NOT NULL UNIQUE,
    valeur VARCHAR(255) NOT NULL,
    description TEXT,
    categorie VARCHAR(30) NOT NULL, -- 'ALERTES', 'TARIFS', 'PAIE', 'REMPLISSAGE'
    date_modification DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Valeurs par défaut correspondant aux exigences du prompt
INSERT INTO configuration_metier (cle, valeur, description, categorie) VALUES
-- Alertes flotte
('ALERTE_VISITE_TECHNIQUE_JOURS', '15', 'Jours avant expiration pour alerte visite technique', 'ALERTES'),
('ALERTE_ASSURANCE_JOURS', '0', 'Jours avant expiration pour alerte assurance (0 = déjà expirée)', 'ALERTES'),
('ALERTE_COLIS_NON_RECLAMES_JOURS', '7', 'Jours avant alerte colis non réclamé', 'ALERTES'),

-- Taux de remplissage
('SEUIL_REMPLISSAGE_VERT_PCT', '70', 'Seuil minimum pour taux de remplissage vert (%)', 'REMPLISSAGE'),
('SEUIL_REMPLISSAGE_ORANGE_PCT', '45', 'Seuil minimum pour taux de remplissage orange (%)', 'REMPLISSAGE'),

-- Paie - CNPS
('TAUX_CNPS_SALARIE_PCT', '4.2', 'Taux cotisation CNPS salarié (%)', 'PAIE'),
('TAUX_CNPS_PATRONAL_PCT', '11.2', 'Taux cotisation CNPS employeur (%)', 'PAIE'),

-- Paie - IRPP (tranches camerounaises - exemple à vérifier)
('IRPP_TRANCHE1_MAX', '2000000', 'Plafond tranche 1 IRPP (FCFA)', 'PAIE'),
('IRPP_TRANCHE1_TAUX', '10', 'Taux tranche 1 IRPP (%)', 'PAIE'),
('IRPP_TRANCHE2_MAX', '3000000', 'Plafond tranche 2 IRPP (FCFA)', 'PAIE'),
('IRPP_TRANCHE2_TAUX', '15', 'Taux tranche 2 IRPP (%)', 'PAIE'),
('IRPP_TRANCHE3_MAX', '5000000', 'Plafond tranche 3 IRPP (FCFA)', 'PAIE'),
('IRPP_TRANCHE3_TAUX', '25', 'Taux tranche 3 IRPP (%)', 'PAIE'),
('IRPP_TRANCHE4_TAUX', '35', 'Taux tranche 4 IRPP (au-delà) (%)', 'PAIE');
```

---

## Écart #6 : Table utilisateurs vs Personnel

### Observation
Le schéma a deux tables distinctes :
- `utilisateurs` (pour l'authentification Spring Security)
- `personnel` (données RH)

### Besoin fonctionnel
Le prompt exige : "`Utilisateur` lié à un `Employe`"

### État actuel
La table `utilisateurs` a un champ `agence_id` mais **pas de champ `personnel_id`**.

### Impact
- **Faible** : Lien utilisateur ↔ employé non explicite en base
- Peut être géré au niveau applicatif mais moins robuste

### Proposition de migration

```sql
-- V7__link_user_personnel.sql
ALTER TABLE utilisateurs
ADD COLUMN personnel_id BIGINT,
ADD CONSTRAINT fk_user_personnel FOREIGN KEY (personnel_id) REFERENCES personnel(id),
ADD INDEX idx_user_personnel (personnel_id);
```

---

## Écart #7 : Colis - Champ date_creation vs date_enregistrement

### Problème
Dans l'entity `Colis.java`, il y a deux champs :
```java
@Column(name = "date_creation", nullable = false)
private LocalDateTime dateCreation = LocalDateTime.now();

@Column(name = "date_enregistrement", nullable = false)
private LocalDateTime dateEnregistrement = LocalDateTime.now();
```

Mais dans le schéma SQL, seul `date_creation` existe :
```sql
date_creation DATETIME NOT NULL,
date_livraison DATETIME,
```

### Impact
- **Faible** : Incohérence entre entity et schéma
- L'entity ne correspondra pas à la table

### Proposition

Soit supprimer `date_enregistrement` de l'entity, soit l'ajouter au schéma. Recommandation : **supprimer de l'entity** (redondant avec `date_creation`).

---

## Tableau récapitulatif des migrations nécessaires

| ID Migration | Description | Priorité | Type |
|--------------|-------------|----------|------|
| V2_corrected | Corriger les noms de table dans V2__add_indexes.sql | Haute | Correction |
| V3 | Créer table bareme_tarif_colis et supplément_colis | Haute | Ajout |
| V4 | Ajouter ligne_id et voyage_id à colis | Haute | Ajout |
| V5 | Nettoyer les doublons dans bulletins_paie | Moyenne | Nettoyage |
| V6 | Créer table configuration_metier | Moyenne | Ajout |
| V7 | Lier utilisateurs à personnel | Faible | Ajout |

---

## Recommandations d'implémentation

1. **Appliquer les migrations dans l'ordre** : V2_corrected → V3 → V4 → V5 → V6 → V7
2. **Tester chaque migration** sur une base de développement avant production
3. **Sauvegarder la base** avant toute migration
4. **Mettre à jour les entités JPA** pour refléter les changements de schéma
5. **Adapter les services métier** pour utiliser les nouvelles tables de configuration

---

## Notes importantes

- Les migrations proposées sont **non destructives** (pas de DROP TABLE, seulement ADD/ALTER)
- Les données existantes ne seront pas perdues
- Les valeurs par défaut correspondent aux exemples visibles dans la maquette HTML
- Les contraintes d'unicité et les clés étrangères préservent l'intégrité référentielle
