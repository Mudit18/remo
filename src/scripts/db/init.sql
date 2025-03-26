
CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    amount NUMERIC(19, 4) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    transaction_type VARCHAR(255) NOT NULL,
    last_updated TIMESTAMP NOT NULL DEFAULT NOW()
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE suspicious_transactions (
    id BIGSERIAL PRIMARY KEY,
    transaction_id BIGINT NOT NULL,
    type VARCHAR(255) NOT NULL,
    resolved BOOLEAN NOT NULL DEFAULT FALSE,
    last_updated TIMESTAMP NOT NULL DEFAULT NOW()
);