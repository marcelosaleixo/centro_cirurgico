package com.marceloaleixo.centrocirurgico.repository;

import com.marceloaleixo.centrocirurgico.entity.ItemEstoque;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ItemEstoqueRepository extends JpaRepository<ItemEstoque, Long> {
    long countByAtivoTrue();
    boolean existsByCodigoIgnoreCase(String codigo);
    boolean existsByCodigoIgnoreCaseAndIdNot(String codigo, Long id);
    @EntityGraph(attributePaths = {"estoqueGeral"})
    List<ItemEstoque> findAllByOrderByNomeAsc();
    @EntityGraph(attributePaths = {"estoqueGeral"})
    List<ItemEstoque> findByAtivoTrueOrderByNomeAsc();
    @EntityGraph(attributePaths = {"estoqueGeral"})
    Optional<ItemEstoque> findWithEstoqueGeralById(Long id);
}
