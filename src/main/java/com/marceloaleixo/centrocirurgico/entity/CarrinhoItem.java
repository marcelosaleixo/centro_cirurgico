package com.marceloaleixo.centrocirurgico.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "carrinho_item", uniqueConstraints = @UniqueConstraint(name = "uq_carrinho_item", columnNames = {"carrinho_id", "item_id"}))
public class CarrinhoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "carrinho_id", nullable = false)
    private CarrinhoAnestesista carrinho;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private ItemEstoque item;
    @Column(name = "quantidade_atual", nullable = false, precision = 12, scale = 3)
    private BigDecimal quantidadeAtual = BigDecimal.ZERO;
    @Column(name = "quantidade_minima", nullable = false, precision = 12, scale = 3)
    private BigDecimal quantidadeMinima = BigDecimal.ZERO;

    public CarrinhoItem() {
    }
    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }
    public CarrinhoAnestesista getCarrinho() { return this.carrinho; }
    public void setCarrinho(CarrinhoAnestesista carrinho) { this.carrinho = carrinho; }
    public ItemEstoque getItem() { return this.item; }
    public void setItem(ItemEstoque item) { this.item = item; }
    public BigDecimal getQuantidadeAtual() { return this.quantidadeAtual; }
    public void setQuantidadeAtual(BigDecimal quantidadeAtual) { this.quantidadeAtual = quantidadeAtual; }
    public BigDecimal getQuantidadeMinima() { return this.quantidadeMinima; }
    public void setQuantidadeMinima(BigDecimal quantidadeMinima) { this.quantidadeMinima = quantidadeMinima; }
}
