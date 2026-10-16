ALTER TABLE usuarios_sistema
    ADD COLUMN tentativas_falhas INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN bloqueado_ate TIMESTAMP WITH TIME ZONE;

ALTER TABLE usuarios_sistema
    ADD CONSTRAINT ck_usuarios_tentativas_falhas CHECK (tentativas_falhas >= 0);
