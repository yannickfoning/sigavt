-- Script SQL de données de test complètes pour SIGAVT
-- Base de données: PostgreSQL
-- Date: 26 juillet 2026

-- Création de la base de données si elle n'existe pas
CREATE DATABASE IF NOT EXISTS sigavt;

\c sigavt;

-- Nettoyage des données existantes
TRUNCATE TABLE ecritures_comptables CASCADE;
TRUNCATE TABLE bulletins_paie CASCADE;
TRUNCATE TABLE colis CASCADE;
TRUNCATE TABLE billets CASCADE;
TRUNCATE TABLE voyages CASCADE;
TRUNCATE TABLE personnel CASCADE;
TRUNCATE TABLE bus CASCADE;
TRUNCATE TABLE lignes CASCADE;
TRUNCATE TABLE courriers CASCADE;
TRUNCATE TABLE utilisateurs CASCADE;
TRUNCATE TABLE agences CASCADE;
TRUNCATE TABLE parametres_agence CASCADE;
TRUNCATE TABLE roles CASCADE;

-- 1. Rôles
INSERT INTO roles (id, nom) VALUES 
(1, 'ADMIN'),
(2, 'GERANT'),
(3, 'BILLETTERIE'),
(4, 'CONVOYEUR'),
(5, 'COMPTABLE'),
(6, 'RESP_FLOTTE');

-- 2. Paramètres de l'agence
INSERT INTO parametres_agence (id, nom_agence, telephone, email, ville_principale, adresse, created_at, updated_at) 
VALUES (1, 'SIGAVT Voyage', '+237 699 123 456', 'contact@sigavt.cm', 'Yaoundé', 'Carrefour Nlongkak, Yaoundé', NOW(), NOW());

-- 3. Agences
INSERT INTO agences (id, nom, ville, adresse, telephone, email, created_at, updated_at) 
VALUES 
(1, 'Siège Yaoundé', 'Yaoundé', 'Carrefour Nlongkak', '+237 699 123 456', 'yaounde@sigavt.cm', NOW(), NOW()),
(2, 'Agence Douala', 'Douala', 'Boulevard de la Liberté', '+237 677 234 567', 'douala@sigavt.cm', NOW(), NOW()),
(3, 'Agence Bafoussam', 'Bafoussam', 'Quartier Administratif', '+237 677 345 678', 'bafoussam@sigavt.cm', NOW(), NOW());

-- 4. Lignes de transport
INSERT INTO lignes (id, ville_depart, ville_arrivee, distance_km, duree_minutes, tarif_base, frequence_journaliere, statut, created_at, updated_at) 
VALUES 
(1, 'Yaoundé', 'Douala', 240, 240, 8000, 8, 'ACTIVE', NOW(), NOW()),
(2, 'Yaoundé', 'Bafoussam', 230, 300, 7500, 4, 'ACTIVE', NOW(), NOW()),
(3, 'Yaoundé', 'Bamenda', 360, 420, 10000, 2, 'ACTIVE', NOW(), NOW()),
(4, 'Douala', 'Bafoussam', 280, 360, 9000, 3, 'ACTIVE', NOW(), NOW()),
(5, 'Douala', 'Kumba', 150, 180, 6000, 2, 'ACTIVE', NOW(), NOW()),
(6, 'Yaoundé', 'Ebolowa', 140, 180, 5500, 2, 'ACTIVE', NOW(), NOW()),
(7, 'Yaoundé', 'Kribi', 170, 210, 6000, 2, 'ACTIVE', NOW(), NOW()),
(8, 'Douala', 'Garoua', 850, 600, 18000, 1, 'ACTIVE', NOW(), NOW());

-- 5. Bus (Flotte)
INSERT INTO bus (id, immatriculation, modele, nombre_places, ligne_assignee_id, agence_id, statut, prochain_entretien, assurance_expiration, created_at, updated_at) 
VALUES 
(1, 'LT-234-AB', 'Toyota Coaster', 24, 1, 1, 'OPERATIONNEL', NOW() + INTERVAL '30 days', NOW() + INTERVAL '180 days', NOW(), NOW()),
(2, 'LT-567-CD', 'Hyundai County', 28, 1, 1, 'OPERATIONNEL', NOW() + INTERVAL '45 days', NOW() + INTERVAL '200 days', NOW(), NOW()),
(3, 'LT-890-EF', 'Toyota Coaster', 24, 2, 1, 'OPERATIONNEL', NOW() + INTERVAL '60 days', NOW() + INTERVAL '150 days', NOW(), NOW()),
(4, 'LT-123-GH', 'Mercedes-Benz Sprinter', 18, 3, 2, 'MAINTENANCE', NOW() - INTERVAL '5 days', NOW() + INTERVAL '90 days', NOW(), NOW()),
(5, 'LT-456-IJ', 'Toyota Coaster', 24, 4, 2, 'OPERATIONNEL', NOW() + INTERVAL '20 days', NOW() + INTERVAL '170 days', NOW(), NOW()),
(6, 'LT-789-KL', 'Hyundai County', 28, 5, 3, 'OPERATIONNEL', NOW() + INTERVAL '50 days', NOW() + INTERVAL '190 days', NOW(), NOW()),
(7, 'LT-012-MN', 'Toyota Coaster', 24, 6, 3, 'HORS_SERVICE', NOW() - INTERVAL '30 days', NOW() - INTERVAL '10 days', NOW(), NOW()),
(8, 'LT-345-OP', 'Mercedes-Benz Sprinter', 18, 7, 1, 'OPERATIONNEL', NOW() + INTERVAL '25 days', NOW() + INTERVAL '160 days', NOW(), NOW());

-- 6. Personnel
INSERT INTO personnel (id, nom_complet, telephone, email, poste, type_contrat, salaire_base, numero_cnps, numero_cni, permis_conduire, bus_assigne_id, agence_id, statut, date_embauche, created_at, updated_at) 
VALUES 
(1, 'Jean Pierre Mbarga', '+237 699 111 222', 'jp.mbarga@sigavt.cm', 'CHAUFFEUR', 'CDI', 180000, 'CNPS-001', 'CNI-1234567890', 'D', 1, 1, 'ACTIF', '2023-01-15', NOW(), NOW()),
(2, 'Paul Nkodo', '+237 677 333 444', 'p.nkodo@sigavt.cm', 'CHAUFFEUR', 'CDI', 175000, 'CNPS-002', 'CNI-0987654321', 'D', 2, 1, 'ACTIF', '2023-02-01', NOW(), NOW()),
(3, 'Marie Claire Atangana', '+237 677 555 666', 'mc.atangana@sigavt.cm', 'BILLETTERIE', 'CDI', 120000, 'CNPS-003', 'CNI-1122334455', NULL, NULL, 1, 'ACTIF', '2023-03-10', NOW(), NOW()),
(4, 'Emmanuel Fouda', '+237 699 777 888', 'e.fouda@sigavt.cm', 'CONVOYEUR', 'CDD', 95000, 'CNPS-004', 'CNI-5566778899', NULL, 1, 2, 'ACTIF', '2024-01-05', NOW(), NOW()),
(5, 'François Biya', '+237 677 999 000', 'f.biya@sigavt.cm', 'CHAUFFEUR', 'CDI', 185000, 'CNPS-005', 'CNI-9988776655', 'D', 3, 2, 'ACTIF', '2022-06-20', NOW(), NOW()),
(6, 'Anne Marie Mballa', '+237 699 111 333', 'am.mballa@sigavt.cm', 'COMPTABLE', 'CDI', 150000, 'CNPS-006', 'CNI-4455667788', NULL, NULL, 1, 'ACTIF', '2022-09-01', NOW(), NOW()),
(7, 'Charles Mengue', '+237 677 222 444', 'c.mengue@sigavt.cm', 'RESP_FLOTTE', 'CDI', 200000, 'CNPS-007', 'CNI-3344556677', NULL, NULL, 1, 'ACTIF', '2022-04-15', NOW(), NOW()),
(8, 'Céline Ngo', '+237 699 444 555', 'c.ngo@sigavt.cm', 'BILLETTERIE', 'CDI', 125000, 'CNPS-008', 'CNI-7788990011', NULL, NULL, 2, 'ACTIF', '2023-05-20', NOW(), NOW());

-- 7. Utilisateurs (avec mots de passe hashés - admin123)
INSERT INTO utilisateurs (id, nom_complet, email, mot_de_passe, role_id, telephone, actif, date_creation, created_at, updated_at) 
VALUES 
(1, 'Administrateur SIGAVT', 'admin@sigavt.cm', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 1, '+237 699 000 000', true, NOW(), NOW(), NOW()),
(2, 'Gérant Yaoundé', 'gerant.yaounde@sigavt.cm', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 2, '+237 699 111 000', true, NOW(), NOW(), NOW()),
(3, 'Agent Billetterie', 'billetterie@sigavt.cm', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 3, '+237 677 222 000', true, NOW(), NOW(), NOW()),
(4, 'Comptable', 'comptable@sigavt.cm', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 5, '+237 699 333 000', true, NOW(), NOW(), NOW());

-- 8. Voyages (pour les 7 prochains jours)
INSERT INTO voyages (id, ligne_id, bus_id, chauffeur_id, date_voyage, heure_depart, statut, places_disponibles, created_at, updated_at) 
VALUES 
-- Aujourd'hui
(1, 1, 1, 1, CURRENT_DATE, '06:00:00', 'PLANIFIE', 24, NOW(), NOW()),
(2, 1, 2, 2, CURRENT_DATE, '08:00:00', 'PLANIFIE', 28, NOW(), NOW()),
(3, 2, 3, 1, CURRENT_DATE, '07:00:00', 'PLANIFIE', 24, NOW(), NOW()),
-- Demain
(4, 1, 1, 1, CURRENT_DATE + INTERVAL '1 day', '06:00:00', 'PLANIFIE', 24, NOW(), NOW()),
(5, 3, 5, 5, CURRENT_DATE + INTERVAL '1 day', '05:30:00', 'PLANIFIE', 18, NOW(), NOW()),
(6, 4, 5, 5, CURRENT_DATE + INTERVAL '1 day', '07:30:00', 'PLANIFIE', 18, NOW(), NOW()),
-- Dans 2 jours
(7, 1, 2, 2, CURRENT_DATE + INTERVAL '2 days', '08:00:00', 'PLANIFIE', 28, NOW(), NOW()),
(8, 5, 6, 2, CURRENT_DATE + INTERVAL '2 days', '09:00:00', 'PLANIFIE', 28, NOW(), NOW()),
-- Dans 3 jours
(9, 6, 8, 1, CURRENT_DATE + INTERVAL '3 days', '07:00:00', 'PLANIFIE', 24, NOW(), NOW()),
(10, 7, 8, 1, CURRENT_DATE + INTERVAL '3 days', '08:30:00', 'PLANIFIE', 24, NOW(), NOW()),
-- Dans 4 jours
(11, 1, 1, 1, CURRENT_DATE + INTERVAL '4 days', '06:00:00', 'PLANIFIE', 24, NOW(), NOW()),
(12, 2, 3, 1, CURRENT_DATE + INTERVAL '4 days', '07:00:00', 'PLANIFIE', 24, NOW(), NOW()),
-- Dans 5 jours
(13, 3, 5, 5, CURRENT_DATE + INTERVAL '5 days', '05:30:00', 'PLANIFIE', 18, NOW(), NOW()),
(14, 4, 5, 5, CURRENT_DATE + INTERVAL '5 days', '07:30:00', 'PLANIFIE', 18, NOW(), NOW()),
-- Dans 6 jours
(15, 8, 6, 2, CURRENT_DATE + INTERVAL '6 days', '04:00:00', 'PLANIFIE', 28, NOW(), NOW());

-- 9. Billets (ventes récentes)
INSERT INTO billets (id, numero_billet, voyage_id, nom_passager, telephone_passager, siege, type_tarif, prix, statut, date_vente, mode_paiement, created_at, updated_at) 
VALUES 
(1, 'BIL-2026-00001', 1, 'Kamga Paul', '+237 677 111 222', 'A1', 'PLEIN_TARIF', 8000, 'VALIDE', NOW(), 'ESPECES', NOW(), NOW()),
(2, 'BIL-2026-00002', 1, 'Mbezo Sophie', '+237 699 333 444', 'A2', 'PLEIN_TARIF', 8000, 'VALIDE', NOW(), 'ORANGE_MONEY', NOW(), NOW()),
(3, 'BIL-2026-00003', 2, 'Nkodo Jean', '+237 677 555 666', 'B1', 'DEMI_TARIF', 4000, 'VALIDE', NOW(), 'ESPECES', NOW(), NOW()),
(4, 'BIL-2026-00004', 3, 'Fouda Marie', '+237 699 777 888', 'C1', 'GROUPE', 6000, 'VALIDE', NOW(), 'MTN_MOMO', NOW(), NOW()),
(5, 'BIL-2026-00005', 1, 'Atangana Pierre', '+237 677 999 000', 'A3', 'PLEIN_TARIF', 8000, 'ANNULE', NOW() - INTERVAL '1 day', 'ESPECES', NOW(), NOW());

-- 10. Colis
INSERT INTO colis (id, numero_tracking, expediteur, telephone_expediteur, destinataire, telephone_destinataire, ville_depart, ville_arrivee, poids_kg, type_colis, description, fragile, urgent, assure, prix, statut, date_enregistrement, mode_paiement, created_at, updated_at) 
VALUES 
(1, 'COL-2026-TRK001', 'Moussa Ibrahim', '+237 677 111 333', 'Fatou Bensouda', '+237 699 222 444', 'Yaoundé', 'Douala', 5.5, 'MARCHANDISE', 'Échantillons textiles', false, false, false, 11000, 'EN_TRANSIT', NOW(), 'ESPECES', NOW(), NOW()),
(2, 'COL-2026-TRK002', 'Ahmadou Tidjani', '+237 699 333 555', 'Aisha Moussa', '+237 677 444 666', 'Douala', 'Bafoussam', 2.3, 'DOCUMENT', 'Dossiers administratifs', true, true, false, 8000, 'LIVRE', NOW() - INTERVAL '1 day', 'ORANGE_MONEY', NOW(), NOW()),
(3, 'COL-2026-TRK003', 'Comptoire SARL', '+237 677 555 777', 'Entreprise ABC', '+237 699 666 888', 'Yaoundé', 'Bamenda', 15.0, 'MARCHANDISE', 'Pièces détachées', false, false, true, 35000, 'EN_ATTENTE', NOW(), 'MTN_MOMO', NOW(), NOW()),
(4, 'COL-2026-TRK004', 'Marie Claire', '+237 699 777 999', 'Paul Emmanuel', '+237 677 888 000', 'Douala', 'Kumba', 0.8, 'DOCUMENT', 'Contrats signés', true, false, false, 5000, 'EN_TRANSIT', NOW(), 'ESPECES', NOW(), NOW()),
(5, 'COL-2026-TRK005', 'Jean Pierre', '+237 677 999 111', 'Sophie Anne', '+237 699 000 222', 'Yaoundé', 'Ebolowa', 3.2, 'MARCHANDISE', 'Livres scolaires', false, true, false, 9000, 'RECLAME', NOW() - INTERVAL '2 days', 'ESPECES', NOW(), NOW());

-- 11. Courriers
INSERT INTO courriers (id, type, objet, expediteur, destinataire, contenu, statut, priorite, date_reception, date_traitement, created_at, updated_at) 
VALUES 
(1, 'ENTRANT', 'Demande de partenariat', 'Ministère des Transports', 'Direction SIGAVT', 'Suite à notre réunion du 15 juillet, nous souhaitons formaliser notre partenariat pour le transport des fonctionnaires.', 'TRAITE', 'NORMALE', NOW() - INTERVAL '5 days', NOW() - INTERVAL '3 days', NOW(), NOW()),
(2, 'SORTANT', 'Facture mensuelle', 'Service Comptabilité', 'Entreprise Fuel CM', 'Facture N°2026-07-001 pour le carburant du mois de juillet.', 'ENVOYE', 'NORMALE', NOW() - INTERVAL '2 days', NULL, NOW(), NOW()),
(3, 'ENTRANT', 'Réclamation client', 'M. Kamga Paul', 'Service Client', 'Je souhaite signaler un retard de 2h sur le voyage Yaoundé-Douala du 20 juillet.', 'NON_LU', 'URGENTE', NOW() - INTERVAL '1 day', NULL, NOW(), NOW()),
(4, 'INTERNE', 'Note de service', 'Direction Générale', 'Tout le personnel', 'Rappel des procédures de sécurité pour les chauffeurs. Point obligatoire lundi 28 juillet à 8h.', 'NON_LU', 'URGENTE', NOW(), NULL, NOW(), NOW()),
(5, 'ENTRANT', 'Demande de devis', 'Société Transport Express', 'Service Commercial', 'Nous souhaitons un devis pour le transport régulier de nos marchandises entre Douala et Yaoundé.', 'NON_LU', 'NORMALE', NOW(), NULL, NOW(), NOW());

-- 12. Bulletins de paie (juillet 2026)
INSERT INTO bulletins_paie (id, personnel_id, periode, salaire_base, indemnites_transport, prime_anciennete, prime_performance, heures_supplementaires, taux_horaire_sup, cnps_salarie, cnps_patronal, irpp, autres_retenues, net_a_payer, statut, date_paiement, mode_paiement, created_at, updated_at) 
VALUES 
(1, 1, '2026-07', 180000, 15000, 5000, 10000, 8, 2500, 7560, 20160, 8500, 0, 179440, 'PAYE', '2026-07-25', 'VIREMENT', NOW(), NOW()),
(2, 2, '2026-07', 175000, 15000, 4500, 8000, 6, 2500, 7350, 19600, 7200, 0, 176950, 'PAYE', '2026-07-25', 'VIREMENT', NOW(), NOW()),
(3, 3, '2026-07', 120000, 15000, 3000, 5000, 0, 2500, 5040, 13440, 3200, 0, 131760, 'PAYE', '2026-07-25', 'ESPECES', NOW(), NOW()),
(4, 4, '2026-07', 95000, 15000, 2000, 3000, 4, 2500, 3990, 10640, 1800, 0, 109210, 'PAYE', '2026-07-25', 'ESPECES', NOW(), NOW()),
(5, 5, '2026-07', 185000, 15000, 6000, 12000, 10, 2500, 7770, 20720, 9200, 0, 186530, 'PAYE', '2026-07-25', 'VIREMENT', NOW(), NOW()),
(6, 6, '2026-07', 150000, 15000, 4000, 7000, 0, 2500, 6300, 16800, 5500, 0, 164200, 'PAYE', '2026-07-25', 'VIREMENT', NOW(), NOW()),
(7, 7, '2026-07', 200000, 15000, 8000, 15000, 0, 2500, 8400, 22400, 12000, 0, 221600, 'PAYE', '2026-07-25', 'VIREMENT', NOW(), NOW()),
(8, 8, '2026-07', 125000, 15000, 3500, 6000, 0, 2500, 5250, 14000, 3800, 0, 137450, 'PAYE', '2026-07-25', 'ESPECES', NOW(), NOW());

-- 13. Écritures comptables
INSERT INTO ecritures_comptables (id, date_ecriture, type_ecriture, categorie, description, montant, reference, created_at, updated_at) 
VALUES 
-- Recettes billetterie
(1, CURRENT_DATE, 'RECETTE', 'BILLETTERIE', 'Vente billet BIL-2026-00001', 8000, 'BIL-2026-00001', NOW(), NOW()),
(2, CURRENT_DATE, 'RECETTE', 'BILLETTERIE', 'Vente billet BIL-2026-00002', 8000, 'BIL-2026-00002', NOW(), NOW()),
(3, CURRENT_DATE, 'RECETTE', 'BILLETTERIE', 'Vente billet BIL-2026-00003', 4000, 'BIL-2026-00003', NOW(), NOW()),
(4, CURRENT_DATE, 'RECETTE', 'BILLETTERIE', 'Vente billet BIL-2026-00004', 6000, 'BIL-2026-00004', NOW(), NOW()),
-- Recettes colis
(5, CURRENT_DATE, 'RECETTE', 'COLIS', 'Enregistrement colis COL-2026-TRK001', 11000, 'COL-2026-TRK001', NOW(), NOW()),
(6, NOW() - INTERVAL '1 day', 'RECETTE', 'COLIS', 'Enregistrement colis COL-2026-TRK002', 8000, 'COL-2026-TRK002', NOW(), NOW()),
(7, CURRENT_DATE, 'RECETTE', 'COLIS', 'Enregistrement colis COL-2026-TRK003', 35000, 'COL-2026-TRK003', NOW(), NOW()),
(8, CURRENT_DATE, 'RECETTE', 'COLIS', 'Enregistrement colis COL-2026-TRK004', 5000, 'COL-2026-TRK004', NOW(), NOW()),
-- Dépenses salaires
(9, '2026-07-25', 'DEPENSE', 'SALAIRE', 'Paie juillet - Jean Pierre Mbarga', 179440, 'BUL-2026-07-001', NOW(), NOW()),
(10, '2026-07-25', 'DEPENSE', 'SALAIRE', 'Paie juillet - Paul Nkodo', 176950, 'BUL-2026-07-002', NOW(), NOW()),
(11, '2026-07-25', 'DEPENSE', 'SALAIRE', 'Paie juillet - Marie Claire Atangana', 131760, 'BUL-2026-07-003', NOW(), NOW()),
(12, '2026-07-25', 'DEPENSE', 'SALAIRE', 'Paie juillet - Emmanuel Fouda', 109210, 'BUL-2026-07-004', NOW(), NOW()),
-- Dépenses carburant (estimées)
(13, NOW() - INTERVAL '3 days', 'DEPENSE', 'CARBURANT', 'Carburant bus LT-234-AB', 150000, 'CARB-2026-07-001', NOW(), NOW()),
(14, NOW() - INTERVAL '3 days', 'DEPENSE', 'CARBURANT', 'Carburant bus LT-567-CD', 145000, 'CARB-2026-07-002', NOW(), NOW()),
-- Dépenses maintenance
(15, NOW() - INTERVAL '7 days', 'DEPENSE', 'MAINTENANCE', 'Maintenance bus LT-123-GH', 85000, 'MAINT-2026-07-001', NOW(), NOW()),
-- Dépenses assurance
(16, NOW() - INTERVAL '15 days', 'DEPENSE', 'ASSURANCE', 'Assurance flotte mensuelle', 250000, 'ASSUR-2026-07-001', NOW(), NOW());

-- Confirmation des données
SELECT 'Données de test insérées avec succès!' AS message;
SELECT COUNT(*) AS total_agences FROM agences;
SELECT COUNT(*) AS total_lignes FROM lignes;
SELECT COUNT(*) AS total_bus FROM bus;
SELECT COUNT(*) AS total_personnel FROM personnel;
SELECT COUNT(*) AS total_voyages FROM voyages;
SELECT COUNT(*) AS total_billets FROM billets;
SELECT COUNT(*) AS total_colis FROM colis;
SELECT COUNT(*) AS total_courriers FROM courriers;
SELECT COUNT(*) AS total_bulletins FROM bulletins_paie;
SELECT COUNT(*) AS total_ecritures FROM ecritures_comptables;
