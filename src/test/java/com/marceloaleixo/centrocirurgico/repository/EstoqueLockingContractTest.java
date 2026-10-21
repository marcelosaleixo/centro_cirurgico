package com.marceloaleixo.centrocirurgico.repository;

import jakarta.persistence.LockModeType;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/** Regression tests: stock and lot reads must keep pessimistic locks to serialize concurrent withdrawals. */
class EstoqueLockingContractTest {

    @Test
    void saldoGeralDeveManterBloqueioPessimistaDeEscrita() throws Exception {
        Method metodo = EstoqueGeralRepository.class.getMethod("bloquearPorItemId", Long.class);
        Lock lock = metodo.getAnnotation(Lock.class);
        assertNotNull(lock, "A consulta do saldo deve possuir @Lock");
        assertEquals(LockModeType.PESSIMISTIC_WRITE, lock.value());
        assertTrue(metodo.getAnnotation(Query.class).value().contains("e.item.id = :itemId"));
    }

    @Test
    void lotesFefoDevemSerBloqueadosEOrdenadosPorValidadeEId() throws Exception {
        Method metodo = LoteEstoqueRepository.class.getMethod("bloquearLotesValidosFefo", Long.class, java.time.LocalDate.class);
        Lock lock = metodo.getAnnotation(Lock.class);
        assertNotNull(lock, "A consulta dos lotes deve possuir @Lock");
        assertEquals(LockModeType.PESSIMISTIC_WRITE, lock.value());
        String jpql = metodo.getAnnotation(Query.class).value().toLowerCase();
        assertTrue(jpql.contains("l.datavalidade >= :hoje"), "Lotes vencidos não devem ser selecionados");
        assertTrue(jpql.contains("order by l.datavalidade asc, l.id asc"), "FEFO deve ter ordenação estável");
        assertTrue(jpql.contains("l.quantidadeatual > 0"));
    }
}
