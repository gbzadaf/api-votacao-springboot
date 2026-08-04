CREATE TABLE opcoes_voto (
                             id                  UUID PRIMARY KEY,
                             enquete_id          UUID          NOT NULL,
                             texto               VARCHAR(200)  NOT NULL,
                             quantidade_votos    BIGINT        NOT NULL DEFAULT 0,

                             CONSTRAINT fk_opcoes_voto_enquete
                                 FOREIGN KEY (enquete_id) REFERENCES enquetes (id)
);

CREATE INDEX idx_opcoes_voto_enquete_id ON opcoes_voto (enquete_id);