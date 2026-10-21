package com.marceloaleixo.centrocirurgico.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "lote_estoque", indexes = {
    @Index(name = "idx_lote_item_validade", columnList = "item_id, data_validade"),
    @Index(name = "idx_lote_validade", columnList = "data_validade")
})
public class LoteEstoque {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private ItemEstoque item;
    @Column(name = "numero_lote", nullable = false, length = 100)
    private String numeroLote;
    @Column(name = "data_validade", nullable = false)
    private LocalDate dataValidade;
    @Column(name = "quantidade_inicial", nullable = false, precision = 12, scale = 3)
    private BigDecimal quantidadeInicial;
    @Column(name = "quantidade_atual", nullable = false, precision = 12, scale = 3)
    private BigDecimal quantidadeAtual;
    @Column(name = "origem_compra_id")
    private Long origemCompraId;
    @Column(name = "custo_unitario", precision = 14, scale = 4)
    private BigDecimal custoUnitario = BigDecimal.ZERO;

    public Long getId(){return id;}
    public ItemEstoque getItem(){return item;} public void setItem(ItemEstoque v){item=v;}
    public String getNumeroLote(){return numeroLote;} public void setNumeroLote(String v){numeroLote=v;}
    public LocalDate getDataValidade(){return dataValidade;} public void setDataValidade(LocalDate v){dataValidade=v;}
    public BigDecimal getQuantidadeInicial(){return quantidadeInicial;} public void setQuantidadeInicial(BigDecimal v){quantidadeInicial=v;}
    public BigDecimal getQuantidadeAtual(){return quantidadeAtual;} public void setQuantidadeAtual(BigDecimal v){quantidadeAtual=v;}
    public Long getOrigemCompraId(){return origemCompraId;} public void setOrigemCompraId(Long v){origemCompraId=v;}
    public BigDecimal getCustoUnitario(){return custoUnitario;} public void setCustoUnitario(BigDecimal v){custoUnitario=v;}
}
