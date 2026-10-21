package com.marceloaleixo.centrocirurgico.service;

import com.marceloaleixo.centrocirurgico.repository.FechamentoFinanceiroCirurgiaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Regra central de negócio para impedir alterações em consumos de cirurgias aprovadas. */
@Service
public class BloqueioConsumoCirurgiaService {
    private final FechamentoFinanceiroCirurgiaRepository fechamentoRepository;

    public BloqueioConsumoCirurgiaService(FechamentoFinanceiroCirurgiaRepository fechamentoRepository) {
        this.fechamentoRepository = fechamentoRepository;
    }

    @Transactional(readOnly = true)
    public void validarConsumoPermitido(Long cirurgiaId) {
        if (cirurgiaId == null) throw new IllegalArgumentException("Informe a cirurgia para validar o fechamento financeiro.");
        fechamentoRepository.findByCirurgiaId(cirurgiaId)
            .filter(f -> "APROVADO".equals(f.getStatus()))
            .ifPresent(f -> { throw new IllegalStateException(
                "Operação bloqueada: o fechamento financeiro da cirurgia #" + cirurgiaId +
                " está APROVADO. Um usuário autorizado deve reabrir o fechamento com justificativa antes de alterar os consumos."); });
    }
}
