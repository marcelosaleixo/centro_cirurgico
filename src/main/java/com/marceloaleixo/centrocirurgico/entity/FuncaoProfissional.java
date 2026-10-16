package com.marceloaleixo.centrocirurgico.entity;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "funcao_profissional")
public class FuncaoProfissional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 100)
    private String nome;
    @Column(length = 255)
    private String descricao;
    @Column(nullable = false)
    private boolean ativo = true;
    @OneToMany(mappedBy = "funcao")
    private List<Colaborador> colaboradores = new ArrayList<>();

    public FuncaoProfissional() {
    }
    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return this.nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return this.descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public boolean isAtivo() { return this.ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public List<Colaborador> getColaboradores() { return this.colaboradores; }
    public void setColaboradores(List<Colaborador> colaboradores) { this.colaboradores = colaboradores; }
}
