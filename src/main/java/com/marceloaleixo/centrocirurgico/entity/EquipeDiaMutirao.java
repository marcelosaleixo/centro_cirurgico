package com.marceloaleixo.centrocirurgico.entity;

import jakarta.persistence.*;

@Entity
@Table(name="equipe_dia_mutirao", uniqueConstraints=@UniqueConstraint(name="uk_equipe_dia_colaborador_funcao", columnNames={"dia_id","colaborador_id","funcao_no_mutirao"}))
public class EquipeDiaMutirao {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="dia_id",nullable=false) private DiaMutirao dia;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="colaborador_id",nullable=false) private Colaborador colaborador;
 @Column(name="funcao_no_mutirao",nullable=false,length=100) private String funcaoNoMutirao;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="sala_cirurgica_id") private SalaCirurgica salaCirurgica;
 @Column(columnDefinition="text") private String observacoes;
 public Long getId(){return id;} public void setId(Long v){id=v;}
 public DiaMutirao getDia(){return dia;} public void setDia(DiaMutirao v){dia=v;}
 public Colaborador getColaborador(){return colaborador;} public void setColaborador(Colaborador v){colaborador=v;}
 public String getFuncaoNoMutirao(){return funcaoNoMutirao;} public void setFuncaoNoMutirao(String v){funcaoNoMutirao=v;}
 public SalaCirurgica getSalaCirurgica(){return salaCirurgica;} public void setSalaCirurgica(SalaCirurgica v){salaCirurgica=v;}
 public String getObservacoes(){return observacoes;} public void setObservacoes(String v){observacoes=v;}
}
