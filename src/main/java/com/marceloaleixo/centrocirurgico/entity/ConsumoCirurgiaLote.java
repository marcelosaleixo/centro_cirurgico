package com.marceloaleixo.centrocirurgico.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "consumo_cirurgia_lote", indexes = {
    @Index(name = "idx_consumo_cirurgia_data", columnList = "cirurgia_id,registrado_em"),
    @Index(name = "idx_consumo_lote", columnList = "lote_id")
})
public class ConsumoCirurgiaLote {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cirurgia_id", nullable = false)
    private Cirurgia cirurgia;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private ItemEstoque item;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lote_id", nullable = false)
    private LoteEstoque lote;
    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal quantidade;
    @Column(nullable = false, length = 500)
    private String observacao;
    @Column(name = "registrado_em", nullable = false, insertable = false, updatable = false)
    private OffsetDateTime registradoEm;
    public Long getId(){return id;}
    public Cirurgia getCirurgia(){return cirurgia;} public void setCirurgia(Cirurgia v){cirurgia=v;}
    public ItemEstoque getItem(){return item;} public void setItem(ItemEstoque v){item=v;}
    public LoteEstoque getLote(){return lote;} public void setLote(LoteEstoque v){lote=v;}
    public BigDecimal getQuantidade(){return quantidade;} public void setQuantidade(BigDecimal v){quantidade=v;}
    public String getObservacao(){return observacao;} public void setObservacao(String v){observacao=v;}
    public OffsetDateTime getRegistradoEm(){return registradoEm;}
}
