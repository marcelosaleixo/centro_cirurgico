package com.marceloaleixo.centrocirurgico.repository;

import com.marceloaleixo.centrocirurgico.entity.AuditoriaItemSolicitacaoMutirao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AuditoriaItemSolicitacaoMutiraoRepository extends JpaRepository<AuditoriaItemSolicitacaoMutirao, Long> {
    List<AuditoriaItemSolicitacaoMutirao> findBySolicitacaoIdOrderByOcorridoEmDescIdDesc(Long solicitacaoId);
}
