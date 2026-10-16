package com.marceloaleixo.centrocirurgico.entity;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "carrinho_anestesista")
public class CarrinhoAnestesista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 60)
    private String codigo;
    @Column(nullable = false, length = 120)
    private String nome;
    @Column(length = 160)
    private String localizacao;
    @Column(nullable = false)
    private boolean ativo = true;
    @OneToMany(mappedBy = "carrinho", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CarrinhoItem> itens = new ArrayList<>();

    public CarrinhoAnestesista() {
    }
    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }
    public String getCodigo() { return this.codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNome() { return this.nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getLocalizacao() { return this.localizacao; }
    public void setLocalizacao(String localizacao) { this.localizacao = localizacao; }
    public boolean isAtivo() { return this.ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public List<CarrinhoItem> getItens() { return this.itens; }
    public void setItens(List<CarrinhoItem> itens) { this.itens = itens; }
}
