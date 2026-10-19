package com.marceloaleixo.centrocirurgico.repository;
import com.marceloaleixo.centrocirurgico.entity.MontagemKitMutirao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;
import java.util.Optional;
public interface MontagemKitMutiraoRepository extends JpaRepository<MontagemKitMutirao,Long> {
 @EntityGraph(attributePaths={"dia","dia.evento","dia.salaCirurgica","kit"}) List<MontagemKitMutirao> findByDiaIdOrderByCriadoEmDesc(Long diaId);
 @EntityGraph(attributePaths={"dia","dia.evento","kit","itens","itens.itemModelo"}) Optional<MontagemKitMutirao> findWithItensById(Long id);
}
