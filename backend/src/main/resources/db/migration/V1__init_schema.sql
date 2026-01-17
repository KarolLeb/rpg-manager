-- Users Table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role VARCHAR(20) NOT NULL
);

-- Campaigns Table (Replaces independent Sessions as the main container)
CREATE TABLE campaigns (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    creation_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    game_master_id BIGINT REFERENCES users(id)
);

-- Sessions Table (Now belongs to a Campaign)
CREATE TABLE sessions (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    campaign_id BIGINT REFERENCES campaigns(id),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    session_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL
);

-- Characters Table (Belongs to a Campaign, not just a Session)
CREATE TABLE characters (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    character_class VARCHAR(255),
    level INTEGER NOT NULL,
    stats JSONB,
    campaign_id BIGINT REFERENCES campaigns(id),
    user_id BIGINT REFERENCES users(id), -- Owner
    controller_id BIGINT REFERENCES users(id), -- Current Controller
    character_type VARCHAR(20) DEFAULT 'PERMANENT'
);