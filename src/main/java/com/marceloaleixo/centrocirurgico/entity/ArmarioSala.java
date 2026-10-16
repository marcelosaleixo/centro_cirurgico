package com.marceloaleixo.centrocirurgico.entity;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "armario_sala", uniqueConstraints = @UniqueConstraint(name = "uq_armario_sala_codigo", columnNames = {"sala_cirurgica_id", "codigo"}))
public class ArmarioSala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sala_cirurgica_id", nullable = false)
    private SalaCirurgica salaCirurgica;
    @Column(nullable = false, length = 60)
    private String codigo;
    @Column(nullable = false, length = 120)
    private String nome;
    @Column(nullable = false)
    private boolean ativo = true;
    @OneToMany(mappedBy = "armario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArmarioItem> itens = new ArrayList<>();

    public ArmarioSala() {
    }
    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }
    public SalaCirurgica getSalaCirurgica() { return this.salaCirurgica; }
    public void setSalaCirurgica(SalaCirurgica salaCirurgica) { this.salaCirurgica = salaCirurgica; }
    public String getCodigo() { return this.codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNome() { return this.nome; }
    public void setNome(String nome) { this.nome = nome; }
    public boolean isAtivo() { return this.ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public List<ArmarioItem> getItens() { return this.itens; }
    public void setItens(List<ArmarioItem> itens) { this.itens = itens; }
}
