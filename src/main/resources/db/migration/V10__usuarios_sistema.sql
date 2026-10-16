CREATE TABLE usuarios_sistema (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    senha_hash VARCHAR(100) NOT NULL,
    perfil VARCHAR(30) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT ck_usuarios_sistema_perfil CHECK (perfil IN ('ADMIN_RH', 'RH', 'CONSULTA'))
);
