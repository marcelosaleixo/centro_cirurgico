package com.marceloaleixo.centrocirurgico.repository;
import com.marceloaleixo.centrocirurgico.entity.ArmarioItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface ArmarioItemRepository extends JpaRepository<ArmarioItem, Long> {
    Optional<ArmarioItem> findByArmarioIdAndItemId(Long armarioId, Long itemId);
    @EntityGraph(attributePaths = {"armario", "item"})
    List<ArmarioItem> findByArmarioIdOrderByItemNomeAsc(Long armarioId);
}
