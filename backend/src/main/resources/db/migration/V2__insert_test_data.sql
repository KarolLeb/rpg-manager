-- Users
INSERT INTO users (username, password, email, role) VALUES 
('gamemaster', '$2a$10$8.UnVuG9HHgffUDAlk8q4.0n.jK.g9Y0.g9Y0.g9Y0.g9Y0.g9Y0', 'gm@example.com', 'GM'),
('player1', '$2a$10$8.UnVuG9HHgffUDAlk8q4.0n.jK.g9Y0.g9Y0.g9Y0.g9Y0.g9Y0', 'player1@example.com', 'PLAYER'),
('player2', '$2a$10$8.UnVuG9HHgffUDAlk8q4.0n.jK.g9Y0.g9Y0.g9Y0.g9Y0.g9Y0', 'player2@example.com', 'PLAYER');

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