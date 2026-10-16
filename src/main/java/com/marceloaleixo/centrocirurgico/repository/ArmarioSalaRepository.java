package com.marceloaleixo.centrocirurgico.repository;
import com.marceloaleixo.centrocirurgico.entity.ArmarioSala;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface ArmarioSalaRepository extends JpaRepository<ArmarioSala, Long> {
    boolean existsBySalaCirurgicaIdAndCodigoIgnoreCase(Long salaId, String codigo);
    boolean existsBySalaCirurgicaIdAndCodigoIgnoreCaseAndIdNot(Long salaId, String codigo, Long id);
    @EntityGraph(attributePaths = {"salaCirurgica", "itens", "itens.item"})
    List<ArmarioSala> findAllByOrderBySalaCirurgicaNomeAscCodigoAsc();
    List<ArmarioSala> findByAtivoTrueOrderByCodigoAsc();
    @Override @EntityGraph(attributePaths = {"salaCirurgica", "itens", "itens.item"})
    Optional<ArmarioSala> findById(Long id);
}
