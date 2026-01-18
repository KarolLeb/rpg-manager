CREATE TABLE race_styles (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE,
    race_name VARCHAR(255) NOT NULL UNIQUE,
    css_content TEXT
);

-- Przykładowe style
INSERT INTO race_styles (uuid, race_name, css_content) VALUES 
('550e8400-e29b-41d4-a716-446655440000', 'ELF', ':root { --race-theme-color: #2ecc71; --race-bg-image: url("/assets/forest-bg.jpg"); }'),
('660e8400-e29b-41d4-a716-446655440001', 'ORC', ':root { --race-theme-color: #c0392b; --race-bg-image: url("/assets/horde-bg.jpg"); }'),
('770e8400-e29b-41d4-a716-446655440002', 'HUMAN', ':root { --race-theme-color: #3498db; --race-bg-image: url("/assets/castle-bg.jpg"); }');
