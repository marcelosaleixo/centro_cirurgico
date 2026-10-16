package com.marceloaleixo.centrocirurgico.web;

import java.math.BigDecimal;

public class ItemEstoqueForm {
    private Long id;
    private String codigo;
    private String nome;
    private String unidadeMedida;
    private BigDecimal estoqueMinimo = BigDecimal.ZERO;
    private BigDecimal quantidadeInicial = BigDecimal.ZERO;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getUnidadeMedida() { return unidadeMedida; }
    public void setUnidadeMedida(String unidadeMedida) { this.unidadeMedida = unidadeMedida; }
    public BigDecimal getEstoqueMinimo() { return estoqueMinimo; }
    public void setEstoqueMinimo(BigDecimal estoqueMinimo) { this.estoqueMinimo = estoqueMinimo; }
    public BigDecimal getQuantidadeInicial() { return quantidadeInicial; }
    public void setQuantidadeInicial(BigDecimal quantidadeInicial) { this.quantidadeInicial = quantidadeInicial; }
}
