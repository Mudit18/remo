
CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    amount NUMERIC(19, 4) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    transaction_type VARCHAR(255) NOT NULL
);