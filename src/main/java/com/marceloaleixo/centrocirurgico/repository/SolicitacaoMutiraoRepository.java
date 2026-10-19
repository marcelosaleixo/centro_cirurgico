package com.marceloaleixo.centrocirurgico.repository;
import com.marceloaleixo.centrocirurgico.entity.SolicitacaoMutirao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List; import java.util.Optional;
public interface SolicitacaoMutiraoRepository extends JpaRepository<SolicitacaoMutirao,Long> {
 @EntityGraph(attributePaths={"dia","dia.evento","itens","itens.itemEstoque"})
 List<SolicitacaoMutirao> findAllByOrderByCriadoEmDesc();
 @EntityGraph(attributePaths={"dia","dia.evento","itens","itens.itemEstoque"}) Optional<SolicitacaoMutirao> findWithItensById(Long id);
 @EntityGraph(attributePaths={"dia","dia.evento"}) List<SolicitacaoMutirao> findByDiaIdOrderByCriadoEmDesc(Long diaId);
}
