package com.marceloaleixo.centrocirurgico.repository;
import com.marceloaleixo.centrocirurgico.entity.DiaMutirao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;
public interface DiaMutiraoRepository extends JpaRepository<DiaMutirao,Long> {
 @EntityGraph(attributePaths={"evento","salaCirurgica","equipe","equipe.colaborador","equipe.colaborador.funcao"})
 List<DiaMutirao> findByEventoIdOrderByDataAsc(Long eventoId);
 @EntityGraph(attributePaths={"evento"})
 List<DiaMutirao> findAllWithEventoByOrderByDataDesc();
 boolean existsByEventoIdAndData(Long eventoId, java.time.LocalDate data);
}
