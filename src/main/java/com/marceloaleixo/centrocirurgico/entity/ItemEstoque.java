package com.marceloaleixo.centrocirurgico.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "item_estoque")
public class ItemEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 60)
    private String codigo;
    @Column(nullable = false, length = 160)
    private String nome;
    @Column(name = "unidade_medida", nullable = false, length = 20)
    private String unidadeMedida;
    @Column(name = "estoque_minimo", nullable = false, precision = 12, scale = 3)
    private BigDecimal estoqueMinimo = BigDecimal.ZERO;
    @Column(nullable = false)
    private boolean ativo = true;
    @OneToOne(mappedBy = "item", fetch = FetchType.LAZY)
    private EstoqueGeral estoqueGeral;

    public ItemEstoque() {
    }
    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }
    public String getCodigo() { return this.codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNome() { return this.nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getUnidadeMedida() { return this.unidadeMedida; }
    public void setUnidadeMedida(String unidadeMedida) { this.unidadeMedida = unidadeMedida; }
    public BigDecimal getEstoqueMinimo() { return this.estoqueMinimo; }
    public void setEstoqueMinimo(BigDecimal estoqueMinimo) { this.estoqueMinimo = estoqueMinimo; }
    public boolean isAtivo() { return this.ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public EstoqueGeral getEstoqueGeral() { return this.estoqueGeral; }
    public void setEstoqueGeral(EstoqueGeral estoqueGeral) { this.estoqueGeral = estoqueGeral; }
}
