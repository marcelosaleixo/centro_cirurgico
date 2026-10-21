package com.marceloaleixo.centrocirurgico.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitacao_compra")
public class SolicitacaoCompra {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "item_estoque_id", nullable = false)
    private ItemEstoque item;
    @Column(name = "quantidade_solicitada", nullable = false, precision = 12, scale = 3)
    private BigDecimal quantidadeSolicitada;
    @Column(name = "justificativa", nullable = false, length = 1000)
    private String justificativa;
    @Column(name = "fornecedor_sugerido", length = 180)
    private String fornecedorSugerido;
    @Column(name = "responsavel_solicitacao", nullable = false, length = 160)
    private String responsavelSolicitacao;
    @Column(name = "observacao", length = 1000)
    private String observacao;
    @Enumerated(EnumType.STRING) @Column(name = "status", nullable = false, length = 30)
    private StatusCompra status = StatusCompra.PENDENTE_APROVACAO;
    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();
    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm = LocalDateTime.now();
    @Column(name = "quantidade_recebida", nullable = false, precision = 12, scale = 3)
    private BigDecimal quantidadeRecebida = BigDecimal.ZERO;
    @Column(name = "recebido_em")
    private LocalDateTime recebidoEm;
    @Column(name = "responsavel_recebimento", length = 160)
    private String responsavelRecebimento;
    public enum StatusCompra { PENDENTE_APROVACAO, APROVADA, REJEITADA, PEDIDO_REALIZADO, RECEBIDA, CANCELADA }
    @PreUpdate public void antesAtualizar() { atualizadoEm = LocalDateTime.now(); }
    public Long getId(){return id;} public ItemEstoque getItem(){return item;} public void setItem(ItemEstoque item){this.item=item;}
    public BigDecimal getQuantidadeSolicitada(){return quantidadeSolicitada;} public void setQuantidadeSolicitada(BigDecimal q){this.quantidadeSolicitada=q;}
    public String getJustificativa(){return justificativa;} public void setJustificativa(String v){this.justificativa=v;}
    public String getFornecedorSugerido(){return fornecedorSugerido;} public void setFornecedorSugerido(String v){this.fornecedorSugerido=v;}
    public String getResponsavelSolicitacao(){return responsavelSolicitacao;} public void setResponsavelSolicitacao(String v){this.responsavelSolicitacao=v;}
    public String getObservacao(){return observacao;} public void setObservacao(String v){this.observacao=v;}
    public StatusCompra getStatus(){return status;} public void setStatus(StatusCompra v){this.status=v;}
    public LocalDateTime getCriadoEm(){return criadoEm;} public LocalDateTime getAtualizadoEm(){return atualizadoEm;}
    public BigDecimal getQuantidadeRecebida(){return quantidadeRecebida;} public void setQuantidadeRecebida(BigDecimal v){this.quantidadeRecebida=v;}
    public LocalDateTime getRecebidoEm(){return recebidoEm;} public void setRecebidoEm(LocalDateTime v){this.recebidoEm=v;}
    public String getResponsavelRecebimento(){return responsavelRecebimento;} public void setResponsavelRecebimento(String v){this.responsavelRecebimento=v;}
}
