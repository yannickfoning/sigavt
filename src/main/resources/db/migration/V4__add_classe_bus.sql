-- Add classe_bus column to bus table
ALTER TABLE bus ADD COLUMN classe_bus VARCHAR(20) NOT NULL DEFAULT 'CLASSIQUE' AFTER statut;
