-- Annuler les 7 voyages de test (IDs 1-7)
-- Ce script contourne la validation métier de l'API (ligneId obligatoire)
-- en modifiant directement la base de données.

USE sigavt;

-- Vérifier l'état actuel
SELECT id, dateVoyage, heureDepart, statut 
FROM voyages 
WHERE id IN (1,2,3,4,5,6,7);

-- Mettre à jour le statut
UPDATE voyages 
SET statut = 'ANNULE' 
WHERE id IN (1,2,3,4,5,6,7);

-- Vérifier après mise à jour
SELECT id, dateVoyage, heureDepart, statut 
FROM voyages 
WHERE id IN (1,2,3,4,5,6,7);
