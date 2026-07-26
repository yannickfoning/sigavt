-- Fix enum data mismatch in database
-- Update any NULL or invalid classe_bus values to CLASSIQUE
UPDATE bus SET classe_bus = 'CLASSIQUE' WHERE classe_bus IS NULL OR classe_bus NOT IN ('CLASSIQUE', 'VIP', 'VVIP');

-- Update any NULL or invalid classe values in sieges to CLASSIQUE
UPDATE sieges SET classe = 'CLASSIQUE' WHERE classe IS NULL OR classe NOT IN ('CLASSIQUE', 'VIP', 'VVIP');
