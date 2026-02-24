-- Setup repeated literals using a DO block
DO $$
DECLARE
  test_pass TEXT := '$2a$10$CT4MLB0vgBtO0ikf4i0/6uxTP4DbGOJ1TtoQkOmKKfMCYg0AgfGCu';
  main_camp TEXT := 'Kampania Smoczej Lancy';
BEGIN
  -- Users
  INSERT INTO users (username, password, email, role) VALUES 
  ('gamemaster', test_pass, 'gm@example.com', 'GM'),
  ('player1', test_pass, 'player1@example.com', 'PLAYER'),
  ('player2', test_pass, 'player2@example.com', 'PLAYER');

  -- Campaigns
  INSERT INTO campaigns (name, description, status, game_master_id) VALUES 
  (main_camp, 'Epicka przygoda w świecie smoków.', 'ACTIVE', (SELECT id FROM users WHERE username = 'gamemaster'));

  -- Sessions
  INSERT INTO sessions (campaign_id, name, description, status) VALUES 
  ((SELECT id FROM campaigns WHERE name = main_camp), 'Sesja 1: Spotkanie w Karczmie', 'Pierwsze spotkanie drużyny.', 'FINISHED'),
  ((SELECT id FROM campaigns WHERE name = main_camp), 'Sesja 2: Atak Goblinów', 'Drużyna wyrusza w drogę.', 'ACTIVE');

  -- Characters
  INSERT INTO characters (name, character_class, level, stats, campaign_id, user_id, controller_id, character_type) VALUES 
  ('Geralt', 'Witcher', 10, '{"strength": {"val": 15, "skills": []}, "constitution": {"val": 14, "skills": []}, "dexterity": {"val": 18, "skills": []}, "agility": {"val": 16, "skills": []}, "perception": {"val": 15, "skills": []}, "empathy": {"val": 8, "skills": []}, "charisma": {"val": 10, "skills": []}, "intelligence": {"val": 12, "skills": []}, "knowledge": {"val": 10, "skills": []}, "willpower": {"val": 14, "skills": []}}', (SELECT id FROM campaigns WHERE name = main_camp), (SELECT id FROM users WHERE username = 'player1'), NULL, 'PERMANENT'),
  ('Yennefer', 'Sorceress', 10, '{"strength": {"val": 8, "skills": []}, "constitution": {"val": 10, "skills": []}, "dexterity": {"val": 14, "skills": []}, "agility": {"val": 12, "skills": []}, "perception": {"val": 14, "skills": []}, "empathy": {"val": 12, "skills": []}, "charisma": {"val": 16, "skills": []}, "intelligence": {"val": 20, "skills": []}, "knowledge": {"val": 18, "skills": []}, "willpower": {"val": 18, "skills": []}}', (SELECT id FROM campaigns WHERE name = main_camp), (SELECT id FROM users WHERE username = 'player2'), (SELECT id FROM users WHERE username = 'player1'), 'PERMANENT');

  INSERT INTO race_styles (race_name, css_content) VALUES
  ('ELF', ':root { --race-theme-color: #2ecc71; --race-bg-image: url("/assets/forest-bg.jpg"); }'),
  ('ORC', ':root { --race-theme-color: #c0392b; --race-bg-image: url("/assets/horde-bg.jpg"); }'),
  ('HUMAN', ':root { --race-theme-color: #3498db; --race-bg-image: url("/assets/castle-bg.jpg"); }');
END $$;
