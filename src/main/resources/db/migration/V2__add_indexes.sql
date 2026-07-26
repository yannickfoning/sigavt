-- ══════════════════════════════════════════════════
-- SIGAVT — Index pour performances en production
-- ══════════════════════════════════════════════════

-- Billets : recherches fréquentes
CREATE INDEX IF NOT EXISTS idx_billets_voyage_id    ON billets(voyage_id);
CREATE INDEX IF NOT EXISTS idx_billets_statut        ON billets(statut);
CREATE INDEX IF NOT EXISTS idx_billets_passager_nom  ON billets(passager_nom);
CREATE INDEX IF NOT EXISTS idx_billets_created_at    ON billets(date_creation);
CREATE INDEX IF NOT EXISTS idx_billets_numero        ON billets(numero);

-- Voyages : dashboard et filtre par date
CREATE INDEX IF NOT EXISTS idx_voyages_date          ON voyages(date_voyage);
CREATE INDEX IF NOT EXISTS idx_voyages_statut        ON voyages(statut);
CREATE INDEX IF NOT EXISTS idx_voyages_ligne_id      ON voyages(ligne_id);
CREATE INDEX IF NOT EXISTS idx_voyages_date_statut   ON voyages(date_voyage, statut);

-- Colis : tracking et statut
CREATE INDEX IF NOT EXISTS idx_colis_tracking        ON colis(numero_tracking);
CREATE INDEX IF NOT EXISTS idx_colis_statut          ON colis(statut);
CREATE INDEX IF NOT EXISTS idx_colis_expediteur      ON colis(expediteur_nom);
CREATE INDEX IF NOT EXISTS idx_colis_created_at      ON colis(date_creation);

-- Personnel : filtre par poste et statut
CREATE INDEX IF NOT EXISTS idx_employes_poste        ON employes(poste);
CREATE INDEX IF NOT EXISTS idx_employes_statut       ON employes(statut);
CREATE INDEX IF NOT EXISTS idx_employes_agence       ON employes(agence_id);

-- Paie : filtre par période
CREATE INDEX IF NOT EXISTS idx_fiches_paie_periode   ON fiches_paie(periode_mois, periode_annee);
CREATE INDEX IF NOT EXISTS idx_fiches_paie_employe   ON fiches_paie(employe_id);

-- Écritures comptables : filtre par date et type
CREATE INDEX IF NOT EXISTS idx_ecritures_date        ON ecritures(date_ecriture);
CREATE INDEX IF NOT EXISTS idx_ecritures_type        ON ecritures(type);
CREATE INDEX IF NOT EXISTS idx_ecritures_categorie   ON ecritures(categorie);

-- Courriers : filtre par statut
CREATE INDEX IF NOT EXISTS idx_courriers_statut      ON courriers(statut);
CREATE INDEX IF NOT EXISTS idx_courriers_priorite    ON courriers(priorite);

-- Utilisateurs : login rapide
CREATE INDEX IF NOT EXISTS idx_users_email           ON utilisateurs(email);
CREATE INDEX IF NOT EXISTS idx_users_actif           ON utilisateurs(actif);
