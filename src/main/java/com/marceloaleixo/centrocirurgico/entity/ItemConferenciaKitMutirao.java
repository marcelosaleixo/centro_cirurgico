package com.marceloaleixo.centrocirurgico.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name="item_conferencia_kit_mutirao")
public class ItemConferenciaKitMutirao {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="montagem_id",nullable=false) private MontagemKitMutirao montagem;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="item_kit_id") private ItemKitCirurgico itemModelo;
 @Column(nullable=false,length=160) private String descricao;
 @Column(nullable=false,length=20) private String categoria="MATERIAL";
 @Column(name="quantidade_prevista",nullable=false,precision=12,scale=3) private BigDecimal quantidadePrevista=BigDecimal.ONE;
 @Column(name="quantidade_conferida",precision=12,scale=3) private BigDecimal quantidadeConferida;
 @Column(nullable=false,length=30) private String unidade="UNIDADE";
 @Column(nullable=false) private boolean conferido=false;
 @Column(columnDefinition="text") private String observacao;
 public Long getId(){return id;} public void setId(Long v){id=v;}
 public MontagemKitMutirao getMontagem(){return montagem;} public void setMontagem(MontagemKitMutirao v){montagem=v;}
 public ItemKitCirurgico getItemModelo(){return itemModelo;} public void setItemModelo(ItemKitCirurgico v){itemModelo=v;}
 public String getDescricao(){return descricao;} public void setDescricao(String v){descricao=v;}
 public String getCategoria(){return categoria;} public void setCategoria(String v){categoria=v;}
 public BigDecimal getQuantidadePrevista(){return quantidadePrevista;} public void setQuantidadePrevista(BigDecimal v){quantidadePrevista=v;}
 public BigDecimal getQuantidadeConferida(){return quantidadeConferida;} public void setQuantidadeConferida(BigDecimal v){quantidadeConferida=v;}
 public String getUnidade(){return unidade;} public void setUnidade(String v){unidade=v;}
 public boolean isConferido(){return conferido;} public void setConferido(boolean v){conferido=v;}
 public String getObservacao(){return observacao;} public void setObservacao(String v){observacao=v;}
}
