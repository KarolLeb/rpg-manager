CREATE TABLE characters (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    character_class VARCHAR(255),
    level INTEGER NOT NULL,
    stats TEXT
);