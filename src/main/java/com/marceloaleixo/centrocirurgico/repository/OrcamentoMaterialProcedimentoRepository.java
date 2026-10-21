package com.marceloaleixo.centrocirurgico.repository;
import com.marceloaleixo.centrocirurgico.entity.OrcamentoMaterialProcedimento;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface OrcamentoMaterialProcedimentoRepository extends JpaRepository<OrcamentoMaterialProcedimento,Long> {
 @EntityGraph(attributePaths={"procedimento","item"}) List<OrcamentoMaterialProcedimento> findAllByOrderByProcedimentoNomeAscItemNomeAsc();
 boolean existsByProcedimentoIdAndItemId(Long procedimentoId, Long itemId);
}
