package com.marceloaleixo.centrocirurgico.repository;

import com.marceloaleixo.centrocirurgico.entity.MovimentacaoEstoque;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.OffsetDateTime;
import java.util.List;

public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {
    @EntityGraph(attributePaths = {"item", "colaborador"})
    List<MovimentacaoEstoque> findTop100ByOrderByDataMovimentacaoDesc();
    long countByDataMovimentacaoBetween(OffsetDateTime inicio, OffsetDateTime fim);
}
