package com.marceloaleixo.centrocirurgico.service;

import com.marceloaleixo.centrocirurgico.entity.*;
import com.marceloaleixo.centrocirurgico.enums.LocalizacaoEstoque;
import com.marceloaleixo.centrocirurgico.enums.TipoMovimentacao;
import com.marceloaleixo.centrocirurgico.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class RastreabilidadeCirurgiaService {
    private final CirurgiaRepository cirurgiaRepository;
    private final ItemEstoqueRepository itemRepository;
    private final EstoqueGeralRepository estoqueRepository;
    private final LoteEstoqueRepository loteRepository;
    private final ConsumoCirurgiaLoteRepository consumoRepository;
    private final MovimentacaoEstoqueRepository movimentacaoRepository;
    private final FechamentoFinanceiroCirurgiaRepository fechamentoRepository;
    private final BloqueioConsumoCirurgiaService bloqueioConsumoService;

    public RastreabilidadeCirurgiaService(CirurgiaRepository cirurgiaRepository, ItemEstoqueRepository itemRepository,
            EstoqueGeralRepository estoqueRepository, LoteEstoqueRepository loteRepository,
            ConsumoCirurgiaLoteRepository consumoRepository, MovimentacaoEstoqueRepository movimentacaoRepository, FechamentoFinanceiroCirurgiaRepository fechamentoRepository, BloqueioConsumoCirurgiaService bloqueioConsumoService) {
        this.cirurgiaRepository = cirurgiaRepository; this.itemRepository = itemRepository;
        this.estoqueRepository = estoqueRepository; this.loteRepository = loteRepository;
        this.consumoRepository = consumoRepository; this.movimentacaoRepository = movimentacaoRepository; this.fechamentoRepository = fechamentoRepository; this.bloqueioConsumoService = bloqueioConsumoService;
    }

    @Transactional(readOnly = true)
    public List<ConsumoCirurgiaLote> listarConsumos(Long cirurgiaId) {
        if (cirurgiaId == null) return consumoRepository.findTop300ByOrderByRegistradoEmDesc();
        return consumoRepository.findByCirurgiaIdOrderByRegistradoEmDesc(cirurgiaId);
    }

    @Transactional
    public void registrar(Long cirurgiaId, Long itemId, BigDecimal quantidade, String observacao) {
        if (cirurgiaId == null) throw new IllegalArgumentException("Selecione a cirurgia.");
        if (itemId == null) throw new IllegalArgumentException("Selecione o material.");
        if (quantidade == null || quantidade.signum() <= 0) throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        if (observacao == null || observacao.isBlank()) throw new IllegalArgumentException("Informe a finalidade ou observação do consumo.");
        Cirurgia cirurgia = cirurgiaRepository.findById(cirurgiaId).orElseThrow(() -> new IllegalArgumentException("Cirurgia não encontrada."));
        bloqueioConsumoService.validarConsumoPermitido(cirurgiaId);
        ItemEstoque item = itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("Material não encontrado."));
        if (!item.isAtivo()) throw new IllegalArgumentException("Material inativo não pode ser consumido.");
        EstoqueGeral saldo = estoqueRepository.bloquearPorItemId(itemId).orElseThrow(() -> new IllegalStateException("Saldo do estoque geral não encontrado."));
        if (saldo.getQuantidadeAtual().compareTo(quantidade) < 0) throw new IllegalStateException("Saldo geral insuficiente.");
        List<LoteEstoque> lotes = loteRepository.bloquearLotesValidosFefo(itemId, LocalDate.now());
        BigDecimal disponivel = lotes.stream().map(LoteEstoque::getQuantidadeAtual).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (disponivel.compareTo(quantidade) < 0) throw new IllegalStateException("Não há quantidade suficiente em lotes válidos. Confira vencimentos e saldos dos lotes.");
        BigDecimal restante = quantidade;
        for (LoteEstoque lote : lotes) {
            if (restante.signum() <= 0) break;
            BigDecimal usada = lote.getQuantidadeAtual().min(restante);
            lote.setQuantidadeAtual(lote.getQuantidadeAtual().subtract(usada));
            loteRepository.save(lote);
            ConsumoCirurgiaLote consumo = new ConsumoCirurgiaLote();
            consumo.setCirurgia(cirurgia); consumo.setItem(item); consumo.setLote(lote);
            consumo.setQuantidade(usada); consumo.setObservacao(observacao.trim()); consumoRepository.save(consumo);
            MovimentacaoEstoque mov = new MovimentacaoEstoque();
            mov.setItem(item); mov.setTipo(TipoMovimentacao.SAIDA); mov.setQuantidade(usada);
            mov.setOrigemTipo(LocalizacaoEstoque.ESTOQUE_GERAL);
            mov.setMotivo("Consumo cirurgia #" + cirurgia.getId() + " | lote " + lote.getNumeroLote() + " | " + observacao.trim());
            movimentacaoRepository.save(mov);
            restante = restante.subtract(usada);
        }
        saldo.setQuantidadeAtual(saldo.getQuantidadeAtual().subtract(quantidade));
        estoqueRepository.save(saldo);
    }
}
