package com.marceloaleixo.centrocirurgico.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="montagem_kit_mutirao")
public class MontagemKitMutirao {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="dia_mutirao_id",nullable=false) private DiaMutirao dia;
 @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="kit_id",nullable=false) private KitCirurgico kit;
 @Column(name="quantidade_kits",nullable=false) private Integer quantidadeKits=1;
 @Column(nullable=false,length=24) private String status="PENDENTE";
 @Column(name="responsavel_montagem",length=160) private String responsavelMontagem;
 @Column(name="observacoes",columnDefinition="text") private String observacoes;
 @Column(name="criado_em",nullable=false) private LocalDateTime criadoEm=LocalDateTime.now();
 @OneToMany(mappedBy="montagem",cascade=CascadeType.ALL,orphanRemoval=true) @OrderBy("id ASC") private List<ItemConferenciaKitMutirao> itens=new ArrayList<>();
 public Long getId(){return id;} public void setId(Long v){id=v;}
 public DiaMutirao getDia(){return dia;} public void setDia(DiaMutirao v){dia=v;}
 public KitCirurgico getKit(){return kit;} public void setKit(KitCirurgico v){kit=v;}
 public Integer getQuantidadeKits(){return quantidadeKits;} public void setQuantidadeKits(Integer v){quantidadeKits=v;}
 public String getStatus(){return status;} public void setStatus(String v){status=v;}
 public String getResponsavelMontagem(){return responsavelMontagem;} public void setResponsavelMontagem(String v){responsavelMontagem=v;}
 public String getObservacoes(){return observacoes;} public void setObservacoes(String v){observacoes=v;}
 public LocalDateTime getCriadoEm(){return criadoEm;} public void setCriadoEm(LocalDateTime v){criadoEm=v;}
 public List<ItemConferenciaKitMutirao> getItens(){return itens;} public void setItens(List<ItemConferenciaKitMutirao> v){itens=v;}
}
