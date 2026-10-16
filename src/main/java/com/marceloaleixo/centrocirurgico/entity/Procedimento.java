package com.marceloaleixo.centrocirurgico.entity;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "procedimento")
public class Procedimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 160)
    private String nome;
    @Column(columnDefinition = "text")
    private String descricao;
    @Column(nullable = false)
    private boolean ativo = true;
    @OneToMany(mappedBy = "procedimento")
    private List<Cirurgia> cirurgias = new ArrayList<>();

    public Procedimento() {
    }
    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return this.nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return this.descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public boolean isAtivo() { return this.ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public List<Cirurgia> getCirurgias() { return this.cirurgias; }
    public void setCirurgias(List<Cirurgia> cirurgias) { this.cirurgias = cirurgias; }
}
