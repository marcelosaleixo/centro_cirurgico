package com.marceloaleixo.centrocirurgico.repository;
import com.marceloaleixo.centrocirurgico.entity.CarrinhoAnestesista;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface CarrinhoAnestesistaRepository extends JpaRepository<CarrinhoAnestesista, Long> {
    boolean existsByCodigoIgnoreCase(String codigo);
    boolean existsByCodigoIgnoreCaseAndIdNot(String codigo, Long id);
    @EntityGraph(attributePaths = {"itens", "itens.item"})
    List<CarrinhoAnestesista> findAllByOrderByNomeAsc();
    List<CarrinhoAnestesista> findByAtivoTrueOrderByNomeAsc();
    @Override @EntityGraph(attributePaths = {"itens", "itens.item"})
    Optional<CarrinhoAnestesista> findById(Long id);
}
