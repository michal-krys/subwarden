ALTER TABLE subscriptions
    ADD COLUMN user_id BIGINT NOT NULL,
    ADD CONSTRAINT fk_subscriptions_user FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX idx_subscriptions_user ON subscriptions (user_id);
