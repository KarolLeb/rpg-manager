-- Add race column to characters table
ALTER TABLE characters ADD COLUMN race VARCHAR(255);

-- Update existing characters with some default race if necessary
-- For Geralt and Yennefer, we can set them specifically if they exist
UPDATE characters SET race = 'Witcher' WHERE name ILIKE '%Geralt%';
UPDATE characters SET race = 'Sorceress' WHERE name ILIKE '%Yennefer%';
UPDATE characters SET race = 'Human' WHERE race IS NULL;
