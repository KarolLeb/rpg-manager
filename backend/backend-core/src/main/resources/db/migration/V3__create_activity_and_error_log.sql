-- Enable pgvector extension
CREATE EXTENSION IF NOT EXISTS vector;

-- Activity Log table (RAG-enabled for agent consumption)
CREATE TABLE activity_log (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT REFERENCES sessions(id),
    campaign_id BIGINT REFERENCES campaigns(id),
    user_id BIGINT REFERENCES users(id),
    action_type VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    metadata JSONB,
    embedding vector(384),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_activity_log_session ON activity_log(session_id);
CREATE INDEX idx_activity_log_campaign ON activity_log(campaign_id);
CREATE INDEX idx_activity_log_user ON activity_log(user_id);
CREATE INDEX idx_activity_log_action_type ON activity_log(action_type);
CREATE INDEX idx_activity_log_created_at ON activity_log(created_at);

-- Error Log table (structured log for admin troubleshooting)
CREATE TABLE error_log (
    id BIGSERIAL PRIMARY KEY,
    service_name VARCHAR(100) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    message TEXT NOT NULL,
    stack_trace TEXT,
    correlation_id VARCHAR(64),
    user_id BIGINT REFERENCES users(id),
    request_path VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_error_log_severity ON error_log(severity);
CREATE INDEX idx_error_log_service ON error_log(service_name);
CREATE INDEX idx_error_log_created_at ON error_log(created_at);
CREATE INDEX idx_error_log_correlation ON error_log(correlation_id);
