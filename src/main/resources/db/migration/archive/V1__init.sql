-- =========================================================
-- SIGAVT - Schema initial
-- =========================================================

CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE agences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(150) NOT NULL,
    ville VARCHAR(80) NOT NULL,
    adresse VARCHAR(255),
    telephone VARCHAR(30),
    email VARCHAR(120)
);

CREATE TABLE utilisateurs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom_complet VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(255) NOT NULL,
    telephone VARCHAR(30),
    role_id BIGINT NOT NULL,
    agence_id BIGINT,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    date_creation DATETIME,
    CONSTRAINT fk_util_role FOREIGN KEY (role_id) REFERENCES roles(id),
    CONSTRAINT fk_util_agence FOREIGN KEY (agence_id) REFERENCES agences(id)
);

CREATE TABLE lignes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ville_depart VARCHAR(80) NOT NULL,
    ville_arrivee VARCHAR(80) NOT NULL,
    distance_km INT,
    duree_minutes INT,
    tarif_base DECIMAL(10,2) NOT NULL,
    frequence_jour INT,
    statut VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
);

CREATE TABLE bus (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    immatriculation VARCHAR(20) NOT NULL UNIQUE,
    modele VARCHAR(100),
    nombre_places INT NOT NULL,
    ligne_assignee_id BIGINT,
    prochain_entretien DATE,
    assurance_expiration DATE,
    statut VARCHAR(20) NOT NULL DEFAULT 'OPERATIONNEL',
    CONSTRAINT fk_bus_ligne FOREIGN KEY (ligne_assignee_id) REFERENCES lignes(id)
);

CREATE TABLE personnel (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom_complet VARCHAR(150) NOT NULL,
    telephone VARCHAR(30),
    poste VARCHAR(20) NOT NULL,
    type_contrat VARCHAR(10),
    date_fin_contrat DATE,
    salaire_base DECIMAL(10,2),
    numero_cnps VARCHAR(30),
    numero_cni VARCHAR(30),
    permis_conduire VARCHAR(10),
    bus_assigne_id BIGINT,
    agence_id BIGINT,
    statut VARCHAR(20) NOT NULL DEFAULT 'ACTIF',
    date_embauche DATE,
    CONSTRAINT fk_pers_bus FOREIGN KEY (bus_assigne_id) REFERENCES bus(id),
    CONSTRAINT fk_pers_agence FOREIGN KEY (agence_id) REFERENCES agences(id)
);

CREATE TABLE voyages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ligne_id BIGINT NOT NULL,
    bus_id BIGINT,
    chauffeur_id BIGINT,
    date_voyage DATE NOT NULL,
    heure_depart TIME NOT NULL,
    places_disponibles INT NOT NULL,
    statut VARCHAR(20) NOT NULL DEFAULT 'PLANIFIE',
    CONSTRAINT fk_voyage_ligne FOREIGN KEY (ligne_id) REFERENCES lignes(id),
    CONSTRAINT fk_voyage_bus FOREIGN KEY (bus_id) REFERENCES bus(id),
    CONSTRAINT fk_voyage_chauffeur FOREIGN KEY (chauffeur_id) REFERENCES personnel(id)
);

CREATE TABLE sieges (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    voyage_id BIGINT NOT NULL,
    numero VARCHAR(5) NOT NULL,
    statut VARCHAR(15) NOT NULL DEFAULT 'LIBRE',
    CONSTRAINT fk_siege_voyage FOREIGN KEY (voyage_id) REFERENCES voyages(id),
    CONSTRAINT uq_siege_voyage_numero UNIQUE (voyage_id, numero)
);

CREATE TABLE billets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_billet VARCHAR(30) NOT NULL UNIQUE,
    voyage_id BIGINT NOT NULL,
    siege_id BIGINT,
    passager_nom VARCHAR(150) NOT NULL,
    passager_telephone VARCHAR(30),
    type_tarif VARCHAR(30) NOT NULL,
    prix DECIMAL(10,2) NOT NULL,
    mode_paiement VARCHAR(20) NOT NULL,
    statut VARCHAR(20) NOT NULL DEFAULT 'VALIDE',
    agent_vente_id BIGINT,
    date_emission DATETIME NOT NULL,
    CONSTRAINT fk_billet_voyage FOREIGN KEY (voyage_id) REFERENCES voyages(id),
    CONSTRAINT fk_billet_siege FOREIGN KEY (siege_id) REFERENCES sieges(id),
    CONSTRAINT fk_billet_agent FOREIGN KEY (agent_vente_id) REFERENCES utilisateurs(id)
);

CREATE TABLE colis (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_tracking VARCHAR(30) NOT NULL UNIQUE,
    expediteur_nom VARCHAR(150) NOT NULL,
    expediteur_tel VARCHAR(30),
    destinataire_nom VARCHAR(150) NOT NULL,
    destinataire_tel VARCHAR(30),
    poids_kg DECIMAL(5,2),
    type_colis VARCHAR(20),
    description VARCHAR(255),
    fragile BOOLEAN NOT NULL DEFAULT FALSE,
    urgent BOOLEAN NOT NULL DEFAULT FALSE,
    assure BOOLEAN NOT NULL DEFAULT FALSE,
    montant DECIMAL(10,2),
    mode_paiement VARCHAR(20),
    statut VARCHAR(20) NOT NULL DEFAULT 'ENREGISTRE',
    agence_id BIGINT,
    agent_id BIGINT,
    date_creation DATETIME NOT NULL,
    date_livraison DATETIME,
    CONSTRAINT fk_colis_agence FOREIGN KEY (agence_id) REFERENCES agences(id),
    CONSTRAINT fk_colis_agent FOREIGN KEY (agent_id) REFERENCES utilisateurs(id)
);

CREATE TABLE suivi_colis (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    colis_id BIGINT NOT NULL,
    statut VARCHAR(20) NOT NULL,
    evenement VARCHAR(255) NOT NULL,
    localisation VARCHAR(255),
    date_evenement DATETIME NOT NULL,
    agent_id BIGINT,
    CONSTRAINT fk_suivi_colis FOREIGN KEY (colis_id) REFERENCES colis(id),
    CONSTRAINT fk_suivi_agent FOREIGN KEY (agent_id) REFERENCES utilisateurs(id)
);

CREATE TABLE courriers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(10) NOT NULL,
    objet VARCHAR(200) NOT NULL,
    expediteur VARCHAR(150),
    destinataire VARCHAR(150),
    contenu TEXT,
    statut VARCHAR(15) NOT NULL DEFAULT 'NON_LU',
    date_creation DATETIME NOT NULL
);

CREATE TABLE bulletins_paie (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    personnel_id BIGINT NOT NULL,
    periode VARCHAR(7) NOT NULL,
    salaire_base DECIMAL(10,2) NOT NULL,
    indemnite_transport DECIMAL(10,2) DEFAULT 0,
    prime_rendement DECIMAL(10,2) DEFAULT 0,
    heures_supplementaires DECIMAL(10,2) DEFAULT 0,
    montant_heures_supp DECIMAL(10,2) DEFAULT 0,
    salaire_brut DECIMAL(10,2) NOT NULL,
    cotisation_cnps DECIMAL(10,2) NOT NULL,
    retenue_irpp DECIMAL(10,2) NOT NULL,
    avance_salaire DECIMAL(10,2) DEFAULT 0,
    net_a_payer DECIMAL(10,2) NOT NULL,
    charges_patronales DECIMAL(10,2) NOT NULL,
    cout_employeur DECIMAL(10,2) NOT NULL,
    mode_reglement VARCHAR(20),
    statut_paiement VARCHAR(15) NOT NULL DEFAULT 'EN_ATTENTE',
    date_paiement DATE,
    CONSTRAINT fk_bulletin_personnel FOREIGN KEY (personnel_id) REFERENCES personnel(id),
    CONSTRAINT uq_bulletin_personnel_periode UNIQUE (personnel_id, periode)
);

CREATE TABLE ecritures_comptables (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date_ecriture DATE NOT NULL,
    description VARCHAR(255) NOT NULL,
    categorie VARCHAR(30) NOT NULL,
    debit DECIMAL(12,2) DEFAULT 0,
    credit DECIMAL(12,2) DEFAULT 0,
    reference VARCHAR(50)
);

CREATE TABLE parametres_agence (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom_agence VARCHAR(150) NOT NULL,
    telephone VARCHAR(30),
    email VARCHAR(120),
    ville_principale VARCHAR(80),
    adresse VARCHAR(255)
);

-- =========================================================
-- Donnees de reference (roles obligatoires)
-- =========================================================

INSERT INTO roles (nom) VALUES
 ('ADMIN'), ('GERANT'), ('BILLETTERIE'), ('CONVOYEUR'), ('CHAUFFEUR'), ('COMPTABLE'), ('RESP_FLOTTE');
