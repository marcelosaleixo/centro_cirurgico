package com.marceloaleixo.centrocirurgico.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "armario_item", uniqueConstraints = @UniqueConstraint(name = "uq_armario_item", columnNames = {"armario_id", "item_id"}))
public class ArmarioItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "armario_id", nullable = false)
    private ArmarioSala armario;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private ItemEstoque item;
    @Column(name = "quantidade_atual", nullable = false, precision = 12, scale = 3)
    private BigDecimal quantidadeAtual = BigDecimal.ZERO;
    @Column(name = "quantidade_minima", nullable = false, precision = 12, scale = 3)
    private BigDecimal quantidadeMinima = BigDecimal.ZERO;

    public ArmarioItem() {
    }
    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }
    public ArmarioSala getArmario() { return this.armario; }
    public void setArmario(ArmarioSala armario) { this.armario = armario; }
    public ItemEstoque getItem() { return this.item; }
    public void setItem(ItemEstoque item) { this.item = item; }
    public BigDecimal getQuantidadeAtual() { return this.quantidadeAtual; }
    public void setQuantidadeAtual(BigDecimal quantidadeAtual) { this.quantidadeAtual = quantidadeAtual; }
    public BigDecimal getQuantidadeMinima() { return this.quantidadeMinima; }
    public void setQuantidadeMinima(BigDecimal quantidadeMinima) { this.quantidadeMinima = quantidadeMinima; }
}
