-- Insert Test Users
INSERT INTO users (username, password, email, role) VALUES 
('gamemaster', '$2a$10$CT4MLB0vgBtO0ikf4i0/6uxTP4DbGOJ1TtoQkOmKKfMCYg0AgfGCu', 'gm@example.com', 'GM'),
('player1', '$2a$10$CT4MLB0vgBtO0ikf4i0/6uxTP4DbGOJ1TtoQkOmKKfMCYg0AgfGCu', 'player1@example.com', 'PLAYER'),
('player2', '$2a$10$CT4MLB0vgBtO0ikf4i0/6uxTP4DbGOJ1TtoQkOmKKfMCYg0AgfGCu', 'player2@example.com', 'PLAYER'),
('admin', '$2a$10$CT4MLB0vgBtO0ikf4i0/6uxTP4DbGOJ1TtoQkOmKKfMCYg0AgfGCu', 'admin@example.com', 'ADMIN');
