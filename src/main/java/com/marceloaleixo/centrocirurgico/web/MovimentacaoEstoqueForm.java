package com.marceloaleixo.centrocirurgico.web;

import com.marceloaleixo.centrocirurgico.enums.LocalizacaoEstoque;
import com.marceloaleixo.centrocirurgico.enums.TipoMovimentacao;
import java.math.BigDecimal;

public class MovimentacaoEstoqueForm {
    private Long itemId;
    private TipoMovimentacao tipo = TipoMovimentacao.ENTRADA;
    private BigDecimal quantidade;
    private LocalizacaoEstoque origemTipo;
    private Long origemId;
    private LocalizacaoEstoque destinoTipo = LocalizacaoEstoque.ESTOQUE_GERAL;
    private Long destinoId;
    private Long colaboradorId;
    private String motivo;

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public TipoMovimentacao getTipo() { return tipo; }
    public void setTipo(TipoMovimentacao tipo) { this.tipo = tipo; }
    public BigDecimal getQuantidade() { return quantidade; }
    public void setQuantidade(BigDecimal quantidade) { this.quantidade = quantidade; }
    public LocalizacaoEstoque getOrigemTipo() { return origemTipo; }
    public void setOrigemTipo(LocalizacaoEstoque origemTipo) { this.origemTipo = origemTipo; }
    public Long getOrigemId() { return origemId; }
    public void setOrigemId(Long origemId) { this.origemId = origemId; }
    public LocalizacaoEstoque getDestinoTipo() { return destinoTipo; }
    public void setDestinoTipo(LocalizacaoEstoque destinoTipo) { this.destinoTipo = destinoTipo; }
    public Long getDestinoId() { return destinoId; }
    public void setDestinoId(Long destinoId) { this.destinoId = destinoId; }
    public Long getColaboradorId() { return colaboradorId; }
    public void setColaboradorId(Long colaboradorId) { this.colaboradorId = colaboradorId; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
