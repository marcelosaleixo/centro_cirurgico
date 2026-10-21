package com.marceloaleixo.centrocirurgico.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "orcamento_material_procedimento", uniqueConstraints = @UniqueConstraint(name="uk_orcamento_procedimento_item", columnNames={"procedimento_id","item_id"}))
public class OrcamentoMaterialProcedimento {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="procedimento_id", nullable=false) private Procedimento procedimento;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="item_id", nullable=false) private ItemEstoque item;
    @Column(name="quantidade_prevista", nullable=false, precision=12, scale=3) private BigDecimal quantidadePrevista;
    @Column(name="custo_unitario_previsto", nullable=false, precision=14, scale=4) private BigDecimal custoUnitarioPrevisto;
    @Column(length=500) private String observacao;
    public Long getId(){return id;} public Procedimento getProcedimento(){return procedimento;} public void setProcedimento(Procedimento v){procedimento=v;}
    public ItemEstoque getItem(){return item;} public void setItem(ItemEstoque v){item=v;} public BigDecimal getQuantidadePrevista(){return quantidadePrevista;} public void setQuantidadePrevista(BigDecimal v){quantidadePrevista=v;}
    public BigDecimal getCustoUnitarioPrevisto(){return custoUnitarioPrevisto;} public void setCustoUnitarioPrevisto(BigDecimal v){custoUnitarioPrevisto=v;} public String getObservacao(){return observacao;} public void setObservacao(String v){observacao=v;}
}
