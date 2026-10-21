package com.marceloaleixo.centrocirurgico.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste de concorrência real do PostgreSQL usando uma tabela isolada.
 * Demonstra que SELECT ... FOR UPDATE serializa duas baixas concorrentes
 * e impede saldo negativo. Não inicializa a aplicação Spring completa.
 */
class PostgreSqlConcorrenciaBaixaEstoqueIT {
    private static PostgreSQLContainer<?> postgres;

    @BeforeAll
    static void iniciarPostgres() throws Exception {
        postgres = new PostgreSQLContainer<>("postgres:16-alpine");
        postgres.start();
        try (Connection c = conexao(); Statement s = c.createStatement()) {
            s.execute("CREATE TABLE estoque_teste (id BIGINT PRIMARY KEY, quantidade NUMERIC(12,3) NOT NULL CHECK (quantidade >= 0))");
            s.execute("INSERT INTO estoque_teste (id, quantidade) VALUES (1, 5.000)");
        }
    }

    @AfterAll
    static void pararPostgres() {
        if (postgres != null) postgres.stop();
    }

    private static Connection conexao() throws Exception {
        return DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
    }

    @Test
    void duasBaixasSimultaneasNaoProduzemSaldoNegativoNemDuplaRetirada() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        CountDownLatch prontas = new CountDownLatch(2);
        CountDownLatch iniciar = new CountDownLatch(1);
        AtomicInteger sucesso = new AtomicInteger();
        AtomicInteger saldoInsuficiente = new AtomicInteger();
        try {
            Future<?> a = pool.submit(() -> baixarQuatro(prontas, iniciar, sucesso, saldoInsuficiente));
            Future<?> b = pool.submit(() -> baixarQuatro(prontas, iniciar, sucesso, saldoInsuficiente));
            assertTrue(prontas.await(10, TimeUnit.SECONDS), "As duas operações deveriam estar prontas");
            iniciar.countDown();
            a.get(20, TimeUnit.SECONDS);
            b.get(20, TimeUnit.SECONDS);

            assertEquals(1, sucesso.get(), "Somente uma baixa de 4 unidades pode ser concluída a partir de 5");
            assertEquals(1, saldoInsuficiente.get(), "A segunda operação deve detectar saldo insuficiente após obter o lock");
            try (Connection c = conexao(); Statement s = c.createStatement();
                 ResultSet rs = s.executeQuery("SELECT quantidade FROM estoque_teste WHERE id = 1")) {
                assertTrue(rs.next());
                assertEquals(0, new BigDecimal("1.000").compareTo(rs.getBigDecimal(1)));
                assertFalse(rs.next());
            }
        } finally {
            pool.shutdownNow();
        }
    }

    private void baixarQuatro(CountDownLatch prontas, CountDownLatch iniciar,
                              AtomicInteger sucesso, AtomicInteger insuficiente) {
        try (Connection c = conexao()) {
            c.setAutoCommit(false);
            prontas.countDown();
            if (!iniciar.await(10, TimeUnit.SECONDS)) throw new IllegalStateException("Timeout aguardando início");
            BigDecimal atual;
            try (PreparedStatement ps = c.prepareStatement("SELECT quantidade FROM estoque_teste WHERE id = ? FOR UPDATE")) {
                ps.setLong(1, 1L);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) throw new IllegalStateException("Saldo de teste não encontrado");
                    atual = rs.getBigDecimal(1);
                }
            }
            BigDecimal solicitada = new BigDecimal("4.000");
            if (atual.compareTo(solicitada) < 0) {
                c.rollback();
                insuficiente.incrementAndGet();
                return;
            }
            try (PreparedStatement ps = c.prepareStatement("UPDATE estoque_teste SET quantidade = quantidade - ? WHERE id = ?")) {
                ps.setBigDecimal(1, solicitada);
                ps.setLong(2, 1L);
                assertEquals(1, ps.executeUpdate());
            }
            c.commit();
            sucesso.incrementAndGet();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
