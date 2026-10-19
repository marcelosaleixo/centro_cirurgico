package com.marceloaleixo.centrocirurgico.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name="item_kit_cirurgico")
public class ItemKitCirurgico {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="kit_id",nullable=false) private KitCirurgico kit;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="item_estoque_id") private ItemEstoque itemEstoque;
 @Column(nullable=false,length=160) private String descricao;
 @Column(nullable=false,length=20) private String categoria="MATERIAL";
 @Column(nullable=false,precision=12,scale=3) private BigDecimal quantidade=BigDecimal.ONE;
 @Column(nullable=false,length=30) private String unidade="UNIDADE";
 @Column(columnDefinition="text") private String observacao;
 public Long getId(){return id;} public void setId(Long v){id=v;}
 public KitCirurgico getKit(){return kit;} public void setKit(KitCirurgico v){kit=v;}
 public ItemEstoque getItemEstoque(){return itemEstoque;} public void setItemEstoque(ItemEstoque v){itemEstoque=v;}
 public String getDescricao(){return descricao;} public void setDescricao(String v){descricao=v;}
 public String getCategoria(){return categoria;} public void setCategoria(String v){categoria=v;}
 public BigDecimal getQuantidade(){return quantidade;} public void setQuantidade(BigDecimal v){quantidade=v;}
 public String getUnidade(){return unidade;} public void setUnidade(String v){unidade=v;}
 public String getObservacao(){return observacao;} public void setObservacao(String v){observacao=v;}
}
