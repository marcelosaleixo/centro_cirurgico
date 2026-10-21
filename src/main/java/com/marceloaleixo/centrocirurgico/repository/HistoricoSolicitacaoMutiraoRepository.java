package com.marceloaleixo.centrocirurgico.repository;

import com.marceloaleixo.centrocirurgico.entity.HistoricoSolicitacaoMutirao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HistoricoSolicitacaoMutiraoRepository extends JpaRepository<HistoricoSolicitacaoMutirao, Long> {
    List<HistoricoSolicitacaoMutirao> findBySolicitacaoIdOrderByOcorridoEmDescIdDesc(Long solicitacaoId);
}
