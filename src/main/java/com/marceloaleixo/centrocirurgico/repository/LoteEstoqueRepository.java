package com.marceloaleixo.centrocirurgico.repository;

import com.marceloaleixo.centrocirurgico.entity.LoteEstoque;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface LoteEstoqueRepository extends JpaRepository<LoteEstoque, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = "item")
    @Query("select l from LoteEstoque l where l.item.id = :itemId and l.quantidadeAtual > 0 and l.dataValidade >= :hoje order by l.dataValidade asc, l.id asc")
    List<LoteEstoque> bloquearLotesValidosFefo(@Param("itemId") Long itemId, @Param("hoje") LocalDate hoje);

    @EntityGraph(attributePaths = "item")
    List<LoteEstoque> findByDataValidadeLessThanEqualAndQuantidadeAtualGreaterThanOrderByDataValidadeAsc(LocalDate limite, java.math.BigDecimal zero);
    @EntityGraph(attributePaths = "item")
    List<LoteEstoque> findByOrderByDataValidadeAsc();
}
