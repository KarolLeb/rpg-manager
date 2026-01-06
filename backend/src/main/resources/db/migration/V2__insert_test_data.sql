INSERT INTO characters (name, character_class, level, stats)
VALUES
('Geralt z Rivii', 'Wiedźmin', 35, '{
  "strength": {"val": 18, "skills": [["Miecz stalowy", 10, 28], ["Miecz srebrny", 10, 28], ["Zastraszanie", 8, 26]]},
  "constitution": {"val": 15, "skills": [["Odporność", 10, 25], ["Mocna głowa", 10, 25]]},
  "dexterity": {"val": 20, "skills": [["Refleks", 10, 30], ["Parowanie", 10, 30]]},
  "agility": {"val": 18, "skills": [["Uniki", 10, 28]]},
  "perception": {"val": 16, "skills": [["Wiedźmińskie zmysły", 10, 26]]},
  "empathy": {"val": 8, "skills": [["Perswazja", 2, 10]]},
  "charisma": {"val": 10, "skills": [["Aktorstwo", 5, 15]]},
  "intelligence": {"val": 14, "skills": [["Znaki", 10, 24]]},
  "knowledge": {"val": 18, "skills": [["Potwory", 10, 28], ["Alchemia", 10, 28]]},
  "willpower": {"val": 16, "skills": [["Medytacja", 10, 26]]}
}'::jsonb),
('Yennefer', 'Czarodziejka', 32, '{"intelligence": 22, "chaos_control": 95}'::jsonb),
('Jaskier', 'Bard', 15, '{"charisma": 25, "luck": -10}'::jsonb);
