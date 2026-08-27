CREATE TABLE password_reset_tokens
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    token       VARCHAR(255)          NOT NULL UNIQUE,
    user_id     BIGINT UNSIGNED       NOT NULL UNIQUE,
    expiry_date DATETIME(6)           NOT NULL,
    CONSTRAINT pk_password_reset_tokens PRIMARY KEY (id),
    CONSTRAINT FK_PASSWORD_RESET_TOKENS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
