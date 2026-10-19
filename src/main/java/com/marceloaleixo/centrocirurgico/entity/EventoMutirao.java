package com.marceloaleixo.centrocirurgico.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "evento_mutirao")
public class EventoMutirao {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, length=160) private String nome;
    @Column(nullable=false, length=100) private String especialidade;
    @Column(name="data_inicio", nullable=false) private LocalDate dataInicio;
    @Column(name="data_fim", nullable=false) private LocalDate dataFim;
    @Column(name="responsavel", length=160) private String responsavel;
    @Column(name="observacoes", columnDefinition="text") private String observacoes;
    @Column(nullable=false, length=20) private String status = "PLANEJADO";
    @OneToMany(mappedBy="evento", cascade=CascadeType.ALL, orphanRemoval=true) @OrderBy("data ASC")
    private List<DiaMutirao> dias = new ArrayList<>();
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getNome(){return nome;} public void setNome(String v){nome=v;}
    public String getEspecialidade(){return especialidade;} public void setEspecialidade(String v){especialidade=v;}
    public LocalDate getDataInicio(){return dataInicio;} public void setDataInicio(LocalDate v){dataInicio=v;}
    public LocalDate getDataFim(){return dataFim;} public void setDataFim(LocalDate v){dataFim=v;}
    public String getResponsavel(){return responsavel;} public void setResponsavel(String v){responsavel=v;}
    public String getObservacoes(){return observacoes;} public void setObservacoes(String v){observacoes=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;}
    public List<DiaMutirao> getDias(){return dias;} public void setDias(List<DiaMutirao> v){dias=v;}
}
