package com.marceloaleixo.centrocirurgico.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="solicitacao_mutirao")
public class SolicitacaoMutirao {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="dia_mutirao_id", nullable=false) private DiaMutirao dia;
 @Column(nullable=false,length=24) private String status="RASCUNHO";
 @Column(name="solicitante",nullable=false,length=160) private String solicitante;
 @Column(name="aprovador",length=160) private String aprovador;
 @Column(name="data_aprovacao") private LocalDateTime dataAprovacao;
 @Column(name="responsavel_separacao",length=160) private String responsavelSeparacao;
 @Column(name="data_separacao") private LocalDateTime dataSeparacao;
 @Column(name="responsavel_entrega",length=160) private String responsavelEntrega;
 @Column(name="data_entrega") private LocalDateTime dataEntrega;
 @Column(name="observacoes",columnDefinition="text") private String observacoes;
 @Column(name="criado_em",nullable=false) private LocalDateTime criadoEm=LocalDateTime.now();
 @OneToMany(mappedBy="solicitacao",cascade=CascadeType.ALL,orphanRemoval=true) @OrderBy("id ASC") private List<ItemSolicitacaoMutirao> itens=new ArrayList<>();
 public Long getId(){return id;} public void setId(Long v){id=v;} public DiaMutirao getDia(){return dia;} public void setDia(DiaMutirao v){dia=v;} public String getStatus(){return status;} public void setStatus(String v){status=v;} public String getSolicitante(){return solicitante;} public void setSolicitante(String v){solicitante=v;} public String getAprovador(){return aprovador;} public void setAprovador(String v){aprovador=v;} public LocalDateTime getDataAprovacao(){return dataAprovacao;} public void setDataAprovacao(LocalDateTime v){dataAprovacao=v;} public String getResponsavelSeparacao(){return responsavelSeparacao;} public void setResponsavelSeparacao(String v){responsavelSeparacao=v;} public LocalDateTime getDataSeparacao(){return dataSeparacao;} public void setDataSeparacao(LocalDateTime v){dataSeparacao=v;} public String getResponsavelEntrega(){return responsavelEntrega;} public void setResponsavelEntrega(String v){responsavelEntrega=v;} public LocalDateTime getDataEntrega(){return dataEntrega;} public void setDataEntrega(LocalDateTime v){dataEntrega=v;} public String getObservacoes(){return observacoes;} public void setObservacoes(String v){observacoes=v;} public LocalDateTime getCriadoEm(){return criadoEm;} public void setCriadoEm(LocalDateTime v){criadoEm=v;} public List<ItemSolicitacaoMutirao> getItens(){return itens;} public void setItens(List<ItemSolicitacaoMutirao> v){itens=v;}
}
