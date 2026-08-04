CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE usuarios (
                          id             UUID PRIMARY KEY,
                          nome           VARCHAR(100)  NOT NULL,
                          email          VARCHAR(150)  NOT NULL,
                          senha          VARCHAR(255)  NOT NULL,
                          data_criacao   TIMESTAMP     NOT NULL,

                          CONSTRAINT uk_usuarios_email UNIQUE (email)
);