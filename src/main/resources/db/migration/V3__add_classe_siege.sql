-- Add classe_siege column to sieges table
ALTER TABLE sieges ADD COLUMN classe VARCHAR(20) NOT NULL DEFAULT 'CLASSIQUE' AFTER statut;
