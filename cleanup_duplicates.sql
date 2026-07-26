-- Script de nettoyage des doublons dans la base de données
-- Ce script doit être exécuté via la console H2 ou intégré dans une migration

-- Nettoyage des agences doublons
-- Garder uniquement la première agence réelle et supprimer les "Agence Test" identiques
DELETE FROM agences WHERE id > 1 AND nom LIKE '%Test%';

-- Nettoyage des lignes doublons
-- Supprimer les lignes en double (même ville_depart et ville_arrivee)
-- Garder uniquement la première occurrence de chaque paire ville_depart/ville_arrivee
DELETE FROM lignes WHERE id NOT IN (
    SELECT MIN(id) FROM lignes 
    GROUP BY ville_depart, ville_arrivee
);

-- Vérification du résultat
SELECT 'Agences restantes: ' || COUNT(*) FROM agences;
SELECT 'Lignes restantes: ' || COUNT(*) FROM lignes;
