package com.marceloaleixo.centrocirurgico.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="kit_cirurgico")
public class KitCirurgico {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=140) private String nome;
 @Column(nullable=false,length=100) private String especialidade;
 @Column(columnDefinition="text") private String descricao;
 @Column(nullable=false) private boolean ativo=true;
 @OneToMany(mappedBy="kit",cascade=CascadeType.ALL,orphanRemoval=true) @OrderBy("id ASC") private List<ItemKitCirurgico> itens=new ArrayList<>();
 public Long getId(){return id;} public void setId(Long v){id=v;}
 public String getNome(){return nome;} public void setNome(String v){nome=v;}
 public String getEspecialidade(){return especialidade;} public void setEspecialidade(String v){especialidade=v;}
 public String getDescricao(){return descricao;} public void setDescricao(String v){descricao=v;}
 public boolean isAtivo(){return ativo;} public void setAtivo(boolean v){ativo=v;}
 public List<ItemKitCirurgico> getItens(){return itens;} public void setItens(List<ItemKitCirurgico> v){itens=v;}
}
