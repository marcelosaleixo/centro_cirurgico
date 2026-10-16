package com.marceloaleixo.centrocirurgico.service;

import com.marceloaleixo.centrocirurgico.entity.Colaborador;
import com.marceloaleixo.centrocirurgico.entity.TrocaPlantao;
import com.marceloaleixo.centrocirurgico.enums.StatusTrocaPlantao;
import com.marceloaleixo.centrocirurgico.repository.ColaboradorRepository;
import com.marceloaleixo.centrocirurgico.repository.TrocaPlantaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class TrocaPlantaoService {
    private final TrocaPlantaoRepository trocaRepository;
    private final ColaboradorRepository colaboradorRepository;

    public TrocaPlantaoService(TrocaPlantaoRepository trocaRepository, ColaboradorRepository colaboradorRepository) {
        this.trocaRepository = trocaRepository;
        this.colaboradorRepository = colaboradorRepository;
    }

    @Transactional(readOnly = true)
    public List<TrocaPlantao> listarTodas() {
        return trocaRepository.findAllByOrderByDataSolicitacaoDesc();
    }

    @Transactional(readOnly = true)
    public List<TrocaPlantao> listarDoColaborador(Long colaboradorId) {
        return trocaRepository.findBySolicitanteIdOrAceitanteIdOrderByDataSolicitacaoDesc(colaboradorId, colaboradorId);
    }

    @Transactional(readOnly = true)
    public List<TrocaPlantao> listarPendentes() {
        return trocaRepository.findByStatusAprovacaoOrderByDataSolicitacaoAsc(StatusTrocaPlantao.PENDENTE);
    }

    @Transactional(readOnly = true)
    public TrocaPlantao buscarPorId(Long id) {
        return trocaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Troca de plantão não encontrada: " + id));
    }

    public TrocaPlantao solicitar(TrocaPlantao troca) {
        if (troca == null || troca.getSolicitante() == null || troca.getSolicitante().getId() == null
                || troca.getAceitante() == null || troca.getAceitante().getId() == null)
            throw new IllegalArgumentException("Informe solicitante e aceitante.");
        if (Objects.equals(troca.getSolicitante().getId(), troca.getAceitante().getId()))
            throw new IllegalArgumentException("O solicitante não pode aceitar a própria troca.");
        if (troca.getDataPlantaoOriginal() == null || troca.getMotivo() == null || troca.getMotivo().isBlank())
            throw new IllegalArgumentException("Data do plantão e motivo são obrigatórios.");
        Colaborador solicitante = colaboradorRepository.findById(troca.getSolicitante().getId())
                .orElseThrow(() -> new IllegalArgumentException("Solicitante não encontrado."));
        Colaborador aceitante = colaboradorRepository.findById(troca.getAceitante().getId())
                .orElseThrow(() -> new IllegalArgumentException("Aceitante não encontrado."));
        if (!solicitante.isAtivo() || !aceitante.isAtivo())
            throw new IllegalStateException("Solicitante e aceitante precisam estar ativos.");
        if (!solicitante.getFuncao().getId().equals(aceitante.getFuncao().getId()))
            throw new IllegalArgumentException("A troca deve ocorrer entre colaboradores da mesma função.");
        troca.setSolicitante(solicitante);
        troca.setAceitante(aceitante);
        troca.setStatusAprovacao(StatusTrocaPlantao.PENDENTE);
        troca.setAprovadoPor(null);
        troca.setDataDecisao(null);
        troca.setMotivo(troca.getMotivo().trim());
        return trocaRepository.save(troca);
    }

    public TrocaPlantao cancelar(Long id) {
        TrocaPlantao t = buscarPorId(id);
        if (t.getStatusAprovacao() != StatusTrocaPlantao.PENDENTE)
            throw new IllegalStateException("Somente trocas pendentes podem ser canceladas.");
        t.setStatusAprovacao(StatusTrocaPlantao.CANCELADA);
        return trocaRepository.save(t);
    }

    public TrocaPlantao aprovar(Long id, Long aprovadorId) {
        TrocaPlantao troca = buscarPorId(id);
        if (troca.getStatusAprovacao() != StatusTrocaPlantao.PENDENTE)
            throw new IllegalStateException("Somente trocas pendentes podem ser aprovadas.");
        Colaborador aprovador = colaboradorRepository.findById(aprovadorId)
                .orElseThrow(() -> new IllegalArgumentException("Responsável pela aprovação não encontrado."));
        if (!aprovador.isAtivo()) throw new IllegalStateException("O responsável pela aprovação está inativo.");
        troca.setStatusAprovacao(StatusTrocaPlantao.APROVADA);
        troca.setAprovadoPor(aprovador);
        troca.setDataDecisao(OffsetDateTime.now());
        return trocaRepository.save(troca);
    }

    public TrocaPlantao rejeitar(Long id, Long aprovadorId) {
        TrocaPlantao troca = buscarPorId(id);
        if (troca.getStatusAprovacao() != StatusTrocaPlantao.PENDENTE)
            throw new IllegalStateException("Somente trocas pendentes podem ser rejeitadas.");
        Colaborador aprovador = colaboradorRepository.findById(aprovadorId)
                .orElseThrow(() -> new IllegalArgumentException("Responsável pela decisão não encontrado."));
        troca.setStatusAprovacao(StatusTrocaPlantao.REJEITADA);
        troca.setAprovadoPor(aprovador);
        troca.setDataDecisao(OffsetDateTime.now());
        return trocaRepository.save(troca);
    }
}
