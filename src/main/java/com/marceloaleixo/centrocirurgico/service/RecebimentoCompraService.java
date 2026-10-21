package com.marceloaleixo.centrocirurgico.service;

import com.marceloaleixo.centrocirurgico.entity.EstoqueGeral;
import com.marceloaleixo.centrocirurgico.entity.MovimentacaoEstoque;
import com.marceloaleixo.centrocirurgico.entity.SolicitacaoCompra;
import com.marceloaleixo.centrocirurgico.enums.LocalizacaoEstoque;
import com.marceloaleixo.centrocirurgico.enums.TipoMovimentacao;
import com.marceloaleixo.centrocirurgico.repository.EstoqueGeralRepository;
import com.marceloaleixo.centrocirurgico.repository.MovimentacaoEstoqueRepository;
import com.marceloaleixo.centrocirurgico.repository.SolicitacaoCompraRepository;
import com.marceloaleixo.centrocirurgico.repository.LoteEstoqueRepository;
import com.marceloaleixo.centrocirurgico.entity.LoteEstoque;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class RecebimentoCompraService {
    private final SolicitacaoCompraRepository compraRepository;
    private final EstoqueGeralRepository estoqueRepository;
    private final MovimentacaoEstoqueRepository movimentacaoRepository;
    private final LoteEstoqueRepository loteRepository;

    public RecebimentoCompraService(SolicitacaoCompraRepository compraRepository,
            EstoqueGeralRepository estoqueRepository, MovimentacaoEstoqueRepository movimentacaoRepository, LoteEstoqueRepository loteRepository) {
        this.compraRepository = compraRepository;
        this.estoqueRepository = estoqueRepository;
        this.movimentacaoRepository = movimentacaoRepository;
        this.loteRepository = loteRepository;
    }

    @Transactional
    public void receber(Long solicitacaoId, BigDecimal quantidade, String responsavel, String numeroLote, LocalDate dataValidade, BigDecimal custoUnitario) {
        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("A quantidade recebida deve ser maior que zero.");
        if (responsavel == null || responsavel.isBlank())
            throw new IllegalArgumentException("Informe o responsável pelo recebimento.");
        if (numeroLote == null || numeroLote.isBlank())
            throw new IllegalArgumentException("Informe o número do lote.");
        if (dataValidade == null || !dataValidade.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("A validade do lote deve ser uma data futura.");
        if (custoUnitario == null || custoUnitario.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Informe um custo unitário válido (zero ou maior).");

        SolicitacaoCompra compra = compraRepository.bloquearPorId(solicitacaoId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitação de compra não encontrada."));
        if (compra.getStatus() != SolicitacaoCompra.StatusCompra.PEDIDO_REALIZADO)
            throw new IllegalStateException("Somente pedidos realizados podem ter recebimento registrado.");

        BigDecimal jaRecebida = compra.getQuantidadeRecebida() == null ? BigDecimal.ZERO : compra.getQuantidadeRecebida();
        BigDecimal total = jaRecebida.add(quantidade);
        if (total.compareTo(compra.getQuantidadeSolicitada()) > 0)
            throw new IllegalArgumentException("A quantidade acumulada recebida não pode superar a quantidade solicitada.");

        EstoqueGeral estoque = estoqueRepository.bloquearPorItemId(compra.getItem().getId())
                .orElseThrow(() -> new IllegalStateException("Não existe saldo cadastrado para este item. Cadastre o saldo antes de receber a compra."));
        estoque.setQuantidadeAtual(estoque.getQuantidadeAtual().add(quantidade));
        estoqueRepository.save(estoque);

        LoteEstoque lote = new LoteEstoque();
        lote.setItem(compra.getItem());
        lote.setNumeroLote(numeroLote.trim());
        lote.setDataValidade(dataValidade);
        lote.setQuantidadeInicial(quantidade);
        lote.setQuantidadeAtual(quantidade);
        lote.setOrigemCompraId(solicitacaoId);
        lote.setCustoUnitario(custoUnitario);
        loteRepository.save(lote);

        MovimentacaoEstoque mov = new MovimentacaoEstoque();
        mov.setItem(compra.getItem());
        mov.setTipo(TipoMovimentacao.ENTRADA);
        mov.setQuantidade(quantidade);
        mov.setDestinoTipo(LocalizacaoEstoque.ESTOQUE_GERAL);
        mov.setMotivo("Recebimento da solicitação de compra #" + solicitacaoId + " — responsável: " + responsavel.trim());
        movimentacaoRepository.save(mov);

        compra.setQuantidadeRecebida(total);
        compra.setResponsavelRecebimento(responsavel.trim());
        compra.setRecebidoEm(LocalDateTime.now());
        if (total.compareTo(compra.getQuantidadeSolicitada()) == 0)
            compra.setStatus(SolicitacaoCompra.StatusCompra.RECEBIDA);
        compraRepository.save(compra);
    }
}
