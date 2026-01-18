-- Users
INSERT INTO users (username, password, email, role) VALUES 
('gamemaster', '$2a$10$CT4MLB0vgBtO0ikf4i0/6uxTP4DbGOJ1TtoQkOmKKfMCYg0AgfGCu', 'gm@example.com', 'GM'),
('player1', '$2a$10$CT4MLB0vgBtO0ikf4i0/6uxTP4DbGOJ1TtoQkOmKKfMCYg0AgfGCu', 'player1@example.com', 'PLAYER'),
('player2', '$2a$10$CT4MLB0vgBtO0ikf4i0/6uxTP4DbGOJ1TtoQkOmKKfMCYg0AgfGCu', 'player2@example.com', 'PLAYER');

-- Campaigns
INSERT INTO campaigns (name, description, status, game_master_id) VALUES 
('Kampania Smoczej Lancy', 'Epicka przygoda w świecie smoków.', 'ACTIVE', (SELECT id FROM users WHERE username = 'gamemaster'));

-- Sessions
INSERT INTO sessions (campaign_id, name, description, status) VALUES 
((SELECT id FROM campaigns WHERE name = 'Kampania Smoczej Lancy'), 'Sesja 1: Spotkanie w Karczmie', 'Pierwsze spotkanie drużyny.', 'FINISHED'),
((SELECT id FROM campaigns WHERE name = 'Kampania Smoczej Lancy'), 'Sesja 2: Atak Goblinów', 'Drużyna wyrusza w drogę.', 'ACTIVE');

-- Characters
INSERT INTO characters (name, character_class, level, stats, campaign_id, user_id, controller_id, character_type) VALUES 
('Geralt', 'Witcher', 10, '{"strength": 15, "dexterity": 18, "intelligence": 12}', (SELECT id FROM campaigns WHERE name = 'Kampania Smoczej Lancy'), (SELECT id FROM users WHERE username = 'player1'), NULL, 'PERMANENT'),
('Yennefer', 'Sorceress', 10, '{"strength": 8, "dexterity": 14, "intelligence": 20}', (SELECT id FROM campaigns WHERE name = 'Kampania Smoczej Lancy'), (SELECT id FROM users WHERE username = 'player2'), (SELECT id FROM users WHERE username = 'player1'), 'PERMANENT');

INSERT INTO race_styles (uuid, race_name, css_content) VALUES
('550e8400-e29b-41d4-a716-446655440000', 'ELF', ':root { --race-theme-color: #2ecc71; --race-bg-image: url("/assets/forest-bg.jpg"); }'),
('660e8400-e29b-41d4-a716-446655440001', 'ORC', ':root { --race-theme-color: #c0392b; --race-bg-image: url("/assets/horde-bg.jpg"); }'),
('770e8400-e29b-41d4-a716-446655440002', 'HUMAN', ':root { --race-theme-color: #3498db; --race-bg-image: url("/assets/castle-bg.jpg"); }');
