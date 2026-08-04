CREATE TABLE votos (
                       id            UUID       PRIMARY KEY,
                       enquete_id    UUID       NOT NULL,
                       opcao_id      UUID       NOT NULL,
                       usuario_id    UUID       NOT NULL,
                       data_voto     TIMESTAMP  NOT NULL,

                       CONSTRAINT fk_votos_enquete
                           FOREIGN KEY (enquete_id) REFERENCES enquetes (id),

                       CONSTRAINT fk_votos_opcao
                           FOREIGN KEY (opcao_id) REFERENCES opcoes_voto (id),

                       CONSTRAINT fk_votos_usuario
                           FOREIGN KEY (usuario_id) REFERENCES usuarios (id),

                       CONSTRAINT uk_voto_usuario_enquete
                           UNIQUE (usuario_id, enquete_id)
);

CREATE INDEX idx_votos_enquete_id ON votos (enquete_id);
CREATE INDEX idx_votos_opcao_id ON votos (opcao_id);
CREATE INDEX idx_votos_usuario_id ON votos (usuario_id);