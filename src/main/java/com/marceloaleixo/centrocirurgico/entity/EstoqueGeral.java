package com.marceloaleixo.centrocirurgico.entity;

import jakarta.persistence.*;
import java.time.*;
import java.math.BigDecimal;

@Entity
@Table(name = "estoque_geral")
public class EstoqueGeral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false, unique = true)
    private ItemEstoque item;
    @Column(name = "quantidade_atual", nullable = false, precision = 12, scale = 3)
    private BigDecimal quantidadeAtual = BigDecimal.ZERO;
    @Column(name = "atualizado_em", insertable = false, updatable = false)
    private OffsetDateTime atualizadoEm;

    public EstoqueGeral() {
    }
    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }
    public ItemEstoque getItem() { return this.item; }
    public void setItem(ItemEstoque item) { this.item = item; }
    public BigDecimal getQuantidadeAtual() { return this.quantidadeAtual; }
    public void setQuantidadeAtual(BigDecimal quantidadeAtual) { this.quantidadeAtual = quantidadeAtual; }
    public OffsetDateTime getAtualizadoEm() { return this.atualizadoEm; }
    public void setAtualizadoEm(OffsetDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }
}
