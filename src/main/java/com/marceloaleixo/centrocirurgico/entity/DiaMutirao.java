package com.marceloaleixo.centrocirurgico.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="dia_mutirao", uniqueConstraints=@UniqueConstraint(name="uk_dia_mutirao_evento_data", columnNames={"evento_id","data"}))
public class DiaMutirao {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="evento_id",nullable=false) private EventoMutirao evento;
 @Column(nullable=false) private LocalDate data;
 @Column(length=120) private String descricao;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="sala_cirurgica_id") private SalaCirurgica salaCirurgica;
 @Column(nullable=false,length=20) private String status="PLANEJADO";
 @Column(columnDefinition="text") private String observacoes;
 @OneToMany(mappedBy="dia",cascade=CascadeType.ALL,orphanRemoval=true) private List<EquipeDiaMutirao> equipe=new ArrayList<>();
 public Long getId(){return id;} public void setId(Long v){id=v;}
 public EventoMutirao getEvento(){return evento;} public void setEvento(EventoMutirao v){evento=v;}
 public LocalDate getData(){return data;} public void setData(LocalDate v){data=v;}
 public String getDescricao(){return descricao;} public void setDescricao(String v){descricao=v;}
 public SalaCirurgica getSalaCirurgica(){return salaCirurgica;} public void setSalaCirurgica(SalaCirurgica v){salaCirurgica=v;}
 public String getStatus(){return status;} public void setStatus(String v){status=v;}
 public String getObservacoes(){return observacoes;} public void setObservacoes(String v){observacoes=v;}
 public List<EquipeDiaMutirao> getEquipe(){return equipe;} public void setEquipe(List<EquipeDiaMutirao> v){equipe=v;}
}
