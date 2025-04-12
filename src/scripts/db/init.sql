
CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    amount NUMERIC(19, 4) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    transaction_type VARCHAR(255) NOT NULL,
    createdAt TIMESTAMP NOT NULL DEFAULT NOW(),
    last_updated TIMESTAMP NOT NULL DEFAULT NOW(),
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE suspicious_transactions (
    id BIGSERIAL PRIMARY KEY,
    transaction_id BIGINT NOT NULL,
    type VARCHAR(255) NOT NULL,
    resolved BOOLEAN NOT NULL DEFAULT FALSE,
    flagged_at TIMESTAMP NOT NULL DEFAULT NOW(),
    last_updated TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE flagged_users (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID NOT NULL,
    flag VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    flagged_at TIMESTAMP NOT NULL DEFAULT NOW()
);
