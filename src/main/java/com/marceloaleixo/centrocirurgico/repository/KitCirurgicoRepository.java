package com.marceloaleixo.centrocirurgico.repository;
import com.marceloaleixo.centrocirurgico.entity.KitCirurgico;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface KitCirurgicoRepository extends JpaRepository<KitCirurgico,Long> {
 @EntityGraph(attributePaths={"itens","itens.itemEstoque"}) List<KitCirurgico> findAllByOrderByNomeAsc();
 @EntityGraph(attributePaths={"itens","itens.itemEstoque"}) Optional<KitCirurgico> findWithItensById(Long id);
}
