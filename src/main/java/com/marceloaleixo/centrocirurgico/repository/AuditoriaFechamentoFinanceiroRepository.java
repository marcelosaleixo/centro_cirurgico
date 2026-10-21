package com.marceloaleixo.centrocirurgico.repository;

import com.marceloaleixo.centrocirurgico.entity.AuditoriaFechamentoFinanceiro;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AuditoriaFechamentoFinanceiroRepository extends JpaRepository<AuditoriaFechamentoFinanceiro, Long> {
    @EntityGraph(attributePaths = {"cirurgia", "cirurgia.paciente", "cirurgia.procedimento"})
    List<AuditoriaFechamentoFinanceiro> findTop100ByCirurgiaIdOrderByRegistradoEmDesc(Long cirurgiaId);
}
