package com.marceloaleixo.centrocirurgico.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Teste de integração focado no comportamento transacional do PostgreSQL.
 * Requer Docker disponível para iniciar o container PostgreSQL isolado.
 */
@Testcontainers
class PostgreSqlRollbackIntegridadeEstoqueIT {
    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("estoque_test")
            .withUsername("test")
            .withPassword("test");

    @BeforeEach
    void prepararEsquema() throws SQLException {
        try (Connection c = conectar(); Statement st = c.createStatement()) {
            st.execute("DROP TABLE IF EXISTS teste_movimentacao_estoque");
            st.execute("DROP TABLE IF EXISTS teste_lote_estoque");
            st.execute("DROP TABLE IF EXISTS teste_estoque_geral");
            st.execute("CREATE TABLE teste_estoque_geral (item_id BIGINT PRIMARY KEY, quantidade NUMERIC(12,3) NOT NULL)");
            st.execute("CREATE TABLE teste_lote_estoque (lote_id BIGINT PRIMARY KEY, quantidade NUMERIC(12,3) NOT NULL)");
            st.execute("CREATE TABLE teste_movimentacao_estoque (id BIGSERIAL PRIMARY KEY, lote_id BIGINT NOT NULL, quantidade NUMERIC(12,3) NOT NULL)");
            st.execute("INSERT INTO teste_estoque_geral(item_id, quantidade) VALUES (1, 10.000)");
            st.execute("INSERT INTO teste_lote_estoque(lote_id, quantidade) VALUES (101, 10.000)");
        }
    }

    @AfterEach
    void limparEsquema() throws SQLException {
        try (Connection c = conectar(); Statement st = c.createStatement()) {
            st.execute("DROP TABLE IF EXISTS teste_movimentacao_estoque");
            st.execute("DROP TABLE IF EXISTS teste_lote_estoque");
            st.execute("DROP TABLE IF EXISTS teste_estoque_geral");
        }
    }

    @Test
    void rollbackRestauraLoteEstoqueGeralEMovimentacaoQuandoOperacaoFalha() throws SQLException {
        assertThrows(SQLException.class, () -> {
            try (Connection c = conectar()) {
                c.setAutoCommit(false);
                try (Statement st = c.createStatement()) {
                    st.executeUpdate("UPDATE teste_lote_estoque SET quantidade = quantidade - 3 WHERE lote_id = 101");
                    st.executeUpdate("UPDATE teste_estoque_geral SET quantidade = quantidade - 3 WHERE item_id = 1");
                    st.executeUpdate("INSERT INTO teste_movimentacao_estoque(lote_id, quantidade) VALUES (101, 3)");
                    // Simula falha tardia durante o processamento da baixa.
                    st.executeUpdate("INSERT INTO tabela_inexistente(id) VALUES (1)");
                    c.commit();
                } catch (SQLException e) {
                    c.rollback();
                    throw e;
                }
            }
        });

        try (Connection c = conectar(); Statement st = c.createStatement()) {
            try (var rs = st.executeQuery("SELECT quantidade FROM teste_lote_estoque WHERE lote_id = 101")) {
                rs.next();
                assertEquals(0, new BigDecimal("10.000").compareTo(rs.getBigDecimal(1)), "saldo do lote deve ser restaurado");
            }
            try (var rs = st.executeQuery("SELECT quantidade FROM teste_estoque_geral WHERE item_id = 1")) {
                rs.next();
                assertEquals(0, new BigDecimal("10.000").compareTo(rs.getBigDecimal(1)), "saldo geral deve ser restaurado");
            }
            try (var rs = st.executeQuery("SELECT COUNT(*) FROM teste_movimentacao_estoque")) {
                rs.next();
                assertEquals(0, rs.getInt(1), "movimentação não deve persistir após rollback");
            }
        }
    }

    @Test
    void commitPersisteBaixaESaidaQuandoTodasAsOperacoesConcluem() throws SQLException {
        try (Connection c = conectar()) {
            c.setAutoCommit(false);
            try (Statement st = c.createStatement()) {
                st.executeUpdate("UPDATE teste_lote_estoque SET quantidade = quantidade - 2 WHERE lote_id = 101");
                st.executeUpdate("UPDATE teste_estoque_geral SET quantidade = quantidade - 2 WHERE item_id = 1");
                st.executeUpdate("INSERT INTO teste_movimentacao_estoque(lote_id, quantidade) VALUES (101, 2)");
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        }
        try (Connection c = conectar(); Statement st = c.createStatement()) {
            try (var rs = st.executeQuery("SELECT quantidade FROM teste_lote_estoque WHERE lote_id = 101")) {
                rs.next(); assertEquals(0, new BigDecimal("8.000").compareTo(rs.getBigDecimal(1)));
            }
            try (var rs = st.executeQuery("SELECT quantidade FROM teste_estoque_geral WHERE item_id = 1")) {
                rs.next(); assertEquals(0, new BigDecimal("8.000").compareTo(rs.getBigDecimal(1)));
            }
            try (var rs = st.executeQuery("SELECT COUNT(*) FROM teste_movimentacao_estoque")) {
                rs.next(); assertEquals(1, rs.getInt(1));
            }
        }
    }

    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword());
    }
}
