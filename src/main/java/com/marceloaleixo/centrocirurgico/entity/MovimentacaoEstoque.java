package com.marceloaleixo.centrocirurgico.entity;

import jakarta.persistence.*;
import java.time.*;
import java.math.BigDecimal;
import com.marceloaleixo.centrocirurgico.enums.LocalizacaoEstoque;
import com.marceloaleixo.centrocirurgico.enums.TipoMovimentacao;

@Entity
@Table(name = "movimentacao_estoque")
public class MovimentacaoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private ItemEstoque item;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private TipoMovimentacao tipo;
    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal quantidade;
    @Enumerated(EnumType.STRING)
    @Column(name = "origem_tipo", length = 20)
    private LocalizacaoEstoque origemTipo;
    @Column(name = "origem_id")
    private Long origemId;
    @Enumerated(EnumType.STRING)
    @Column(name = "destino_tipo", length = 20)
    private LocalizacaoEstoque destinoTipo;
    @Column(name = "destino_id")
    private Long destinoId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "colaborador_id")
    private Colaborador colaborador;
    @Column(name = "data_movimentacao", insertable = false, updatable = false)
    private OffsetDateTime dataMovimentacao;
    @Column(length = 500)
    private String motivo;

    public MovimentacaoEstoque() {
    }
    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }
    public ItemEstoque getItem() { return this.item; }
    public void setItem(ItemEstoque item) { this.item = item; }
    public TipoMovimentacao getTipo() { return this.tipo; }
    public void setTipo(TipoMovimentacao tipo) { this.tipo = tipo; }
    public BigDecimal getQuantidade() { return this.quantidade; }
    public void setQuantidade(BigDecimal quantidade) { this.quantidade = quantidade; }
    public LocalizacaoEstoque getOrigemTipo() { return this.origemTipo; }
    public void setOrigemTipo(LocalizacaoEstoque origemTipo) { this.origemTipo = origemTipo; }
    public Long getOrigemId() { return this.origemId; }
    public void setOrigemId(Long origemId) { this.origemId = origemId; }
    public LocalizacaoEstoque getDestinoTipo() { return this.destinoTipo; }
    public void setDestinoTipo(LocalizacaoEstoque destinoTipo) { this.destinoTipo = destinoTipo; }
    public Long getDestinoId() { return this.destinoId; }
    public void setDestinoId(Long destinoId) { this.destinoId = destinoId; }
    public Colaborador getColaborador() { return this.colaborador; }
    public void setColaborador(Colaborador colaborador) { this.colaborador = colaborador; }
    public OffsetDateTime getDataMovimentacao() { return this.dataMovimentacao; }
    public void setDataMovimentacao(OffsetDateTime dataMovimentacao) { this.dataMovimentacao = dataMovimentacao; }
    public String getMotivo() { return this.motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
