CREATE TABLE agents (
    id            VARCHAR(64)  PRIMARY KEY,
    name          VARCHAR(64)  NOT NULL,
    owner_user_id VARCHAR(128) NOT NULL,
    role          VARCHAR(64),
    status        VARCHAR(16)  NOT NULL,
    created_at    TIMESTAMPTZ  NOT NULL,
    CONSTRAINT uq_agents_name UNIQUE (name),
    CONSTRAINT ck_agents_status CHECK (status IN ('ACTIVE', 'SUSPENDED', 'REVOKED'))
);

CREATE INDEX idx_agents_owner ON agents (owner_user_id);