-- Insert Test Users
WITH test_user_password(pass) AS (
  VALUES ('$2a$10$CT4MLB0vgBtO0ikf4i0/6uxTP4DbGOJ1TtoQkOmKKfMCYg0AgfGCu')
)
INSERT INTO users (username, password, email, role)
SELECT username, pass, email, role
FROM (VALUES
  ('gamemaster', 'gm@example.com', 'GM'),
  ('player1', 'player1@example.com', 'PLAYER'),
  ('player2', 'player2@example.com', 'PLAYER'),
  ('admin', 'admin@example.com', 'ADMIN')
) AS u(username, email, role), test_user_password;
