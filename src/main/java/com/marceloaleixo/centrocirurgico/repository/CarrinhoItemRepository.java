package com.marceloaleixo.centrocirurgico.repository;
import com.marceloaleixo.centrocirurgico.entity.CarrinhoItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface CarrinhoItemRepository extends JpaRepository<CarrinhoItem, Long> {
    Optional<CarrinhoItem> findByCarrinhoIdAndItemId(Long carrinhoId, Long itemId);
    @EntityGraph(attributePaths = {"carrinho", "item"})
    List<CarrinhoItem> findByCarrinhoIdOrderByItemNomeAsc(Long carrinhoId);
}
