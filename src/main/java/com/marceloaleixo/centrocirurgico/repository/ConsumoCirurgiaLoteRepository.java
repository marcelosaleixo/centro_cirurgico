package com.marceloaleixo.centrocirurgico.repository;

import com.marceloaleixo.centrocirurgico.entity.ConsumoCirurgiaLote;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Consultas de consumo por cirurgia/lote.
 * Evita comparar parâmetros temporais nulos em JPQL, pois o PostgreSQL pode
 * não inferir o tipo JDBC de parâmetros usados em ":param is null".
 */
public interface ConsumoCirurgiaLoteRepository extends JpaRepository<ConsumoCirurgiaLote, Long> {
    String[] GRAFO_CONSUMO = {"cirurgia", "cirurgia.paciente", "cirurgia.procedimento", "item", "lote"};

    @EntityGraph(attributePaths = {"cirurgia", "cirurgia.paciente", "cirurgia.procedimento", "item", "lote"})
    List<ConsumoCirurgiaLote> findByCirurgiaIdOrderByRegistradoEmDesc(Long cirurgiaId);

    @EntityGraph(attributePaths = {"cirurgia", "cirurgia.paciente", "cirurgia.procedimento", "item", "lote"})
    List<ConsumoCirurgiaLote> findTop300ByOrderByRegistradoEmDesc();

    @EntityGraph(attributePaths = {"cirurgia", "cirurgia.paciente", "cirurgia.procedimento", "item", "lote"})
    List<ConsumoCirurgiaLote> findAllByOrderByRegistradoEmDesc();

    @EntityGraph(attributePaths = {"cirurgia", "cirurgia.paciente", "cirurgia.procedimento", "item", "lote"})
    List<ConsumoCirurgiaLote> findByRegistradoEmGreaterThanEqualOrderByRegistradoEmDesc(OffsetDateTime inicio);

    @EntityGraph(attributePaths = {"cirurgia", "cirurgia.paciente", "cirurgia.procedimento", "item", "lote"})
    List<ConsumoCirurgiaLote> findByRegistradoEmLessThanOrderByRegistradoEmDesc(OffsetDateTime fim);

    @EntityGraph(attributePaths = {"cirurgia", "cirurgia.paciente", "cirurgia.procedimento", "item", "lote"})
    List<ConsumoCirurgiaLote> findByRegistradoEmGreaterThanEqualAndRegistradoEmLessThanOrderByRegistradoEmDesc(OffsetDateTime inicio, OffsetDateTime fim);

    @EntityGraph(attributePaths = {"cirurgia", "cirurgia.paciente", "cirurgia.procedimento", "item", "lote"})
    List<ConsumoCirurgiaLote> findByCirurgia_IdAndRegistradoEmGreaterThanEqualAndRegistradoEmLessThanOrderByRegistradoEmDesc(Long cirurgiaId, OffsetDateTime inicio, OffsetDateTime fim);

    @EntityGraph(attributePaths = {"cirurgia", "cirurgia.paciente", "cirurgia.procedimento", "item", "lote"})
    List<ConsumoCirurgiaLote> findByCirurgia_IdAndRegistradoEmGreaterThanEqualOrderByRegistradoEmDesc(Long cirurgiaId, OffsetDateTime inicio);

    @EntityGraph(attributePaths = {"cirurgia", "cirurgia.paciente", "cirurgia.procedimento", "item", "lote"})
    List<ConsumoCirurgiaLote> findByCirurgia_IdAndRegistradoEmLessThanOrderByRegistradoEmDesc(Long cirurgiaId, OffsetDateTime fim);

    @EntityGraph(attributePaths = {"cirurgia", "cirurgia.paciente", "cirurgia.procedimento", "item", "lote"})
    List<ConsumoCirurgiaLote> findByCirurgia_IdOrderByRegistradoEmDesc(Long cirurgiaId);

    /** Mantém a assinatura utilizada pelos relatórios, escolhendo uma consulta tipada. */
    default List<ConsumoCirurgiaLote> relatorio(Long cirurgiaId, OffsetDateTime inicio, OffsetDateTime fim) {
        if (cirurgiaId != null) {
            if (inicio != null && fim != null) return findByCirurgia_IdAndRegistradoEmGreaterThanEqualAndRegistradoEmLessThanOrderByRegistradoEmDesc(cirurgiaId, inicio, fim);
            if (inicio != null) return findByCirurgia_IdAndRegistradoEmGreaterThanEqualOrderByRegistradoEmDesc(cirurgiaId, inicio);
            if (fim != null) return findByCirurgia_IdAndRegistradoEmLessThanOrderByRegistradoEmDesc(cirurgiaId, fim);
            return findByCirurgia_IdOrderByRegistradoEmDesc(cirurgiaId);
        }
        if (inicio != null && fim != null) return findByRegistradoEmGreaterThanEqualAndRegistradoEmLessThanOrderByRegistradoEmDesc(inicio, fim);
        if (inicio != null) return findByRegistradoEmGreaterThanEqualOrderByRegistradoEmDesc(inicio);
        if (fim != null) return findByRegistradoEmLessThanOrderByRegistradoEmDesc(fim);
        return findAllByOrderByRegistradoEmDesc();
    }
}
