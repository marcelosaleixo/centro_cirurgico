package com.marceloaleixo.centrocirurgico.entity;

import jakarta.persistence.*;
import java.time.*;
import java.util.*;
import com.marceloaleixo.centrocirurgico.enums.StatusMutirao;

@Entity
@Table(name = "mutirao")
public class Mutirao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 160)
    private String nome;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "procedimento_id", nullable = false)
    private Procedimento procedimento;
    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;
    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private StatusMutirao status = StatusMutirao.PLANEJADO;
    @Column(columnDefinition = "text")
    private String observacao;
    @OneToMany(mappedBy = "mutirao")
    private List<Cirurgia> cirurgias = new ArrayList<>();

    public Mutirao() {
    }
    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return this.nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Procedimento getProcedimento() { return this.procedimento; }
    public void setProcedimento(Procedimento procedimento) { this.procedimento = procedimento; }
    public LocalDate getDataInicio() { return this.dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }
    public LocalDate getDataFim() { return this.dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }
    public StatusMutirao getStatus() { return this.status; }
    public void setStatus(StatusMutirao status) { this.status = status; }
    public String getObservacao() { return this.observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
    public List<Cirurgia> getCirurgias() { return this.cirurgias; }
    public void setCirurgias(List<Cirurgia> cirurgias) { this.cirurgias = cirurgias; }
}
