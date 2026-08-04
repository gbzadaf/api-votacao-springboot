CREATE TABLE enquetes (
                          id                  UUID PRIMARY KEY,
                          titulo              VARCHAR(150)  NOT NULL,
                          pergunta            VARCHAR(500)  NOT NULL,
                          status              VARCHAR(20)   NOT NULL,
                          data_criacao        TIMESTAMP     NOT NULL,
                          data_encerramento   TIMESTAMP,
                          usuario_id          UUID          NOT NULL,

                          CONSTRAINT fk_enquetes_usuario
                              FOREIGN KEY (usuario_id) REFERENCES usuarios (id),

                          CONSTRAINT ck_enquetes_status
                              CHECK (status IN ('ABERTA', 'ENCERRADA', 'CANCELADA'))
);

CREATE INDEX idx_enquetes_usuario_id ON enquetes (usuario_id);
CREATE INDEX idx_enquetes_status ON enquetes (status);