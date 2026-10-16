package com.marceloaleixo.centrocirurgico.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.math.BigDecimal;
import com.marceloaleixo.centrocirurgico.enums.RegraFeriado;

@Entity
@Table(name = "feriado", uniqueConstraints = @UniqueConstraint(
        name = "uq_feriado_data_nome", columnNames = {"data_feriado", "nome"}))
public class Feriado {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 150)
    private String nome;
    @Column(name = "data_feriado", nullable = false)
    private LocalDate data;
    @Enumerated(EnumType.STRING)
    @Column(name = "abrangencia", nullable = false, length = 20)
    private AbrangenciaFeriado abrangencia = AbrangenciaFeriado.MUNICIPAL;
    @Column(length = 100)
    private String municipio;
    @Column(length = 2)
    private String uf;
    @Column(name = "recorrente_anual", nullable = false)
    private boolean recorrenteAnual;
    @Column(nullable = false)
    private boolean ativo = true;
    @Column(length = 500)
    private String observacao;
    @Enumerated(EnumType.STRING)
    @Column(name = "regra_operacional", nullable = false, length = 30)
    private RegraFeriado regraOperacional = RegraFeriado.FUNCIONAMENTO_NORMAL;
    @Column(name = "percentual_adicional", precision = 5, scale = 2)
    private BigDecimal percentualAdicional;
    @Column(name = "criado_em", nullable = false, updatable = false)
    private OffsetDateTime criadoEm;

    @PrePersist
    void prePersist() { if (criadoEm == null) criadoEm = OffsetDateTime.now(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public AbrangenciaFeriado getAbrangencia() { return abrangencia; }
    public void setAbrangencia(AbrangenciaFeriado abrangencia) { this.abrangencia = abrangencia; }
    public String getMunicipio() { return municipio; }
    public void setMunicipio(String municipio) { this.municipio = municipio; }
    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }
    public boolean isRecorrenteAnual() { return recorrenteAnual; }
    public void setRecorrenteAnual(boolean recorrenteAnual) { this.recorrenteAnual = recorrenteAnual; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
    public RegraFeriado getRegraOperacional() { return regraOperacional; }
    public void setRegraOperacional(RegraFeriado regraOperacional) { this.regraOperacional = regraOperacional; }
    public BigDecimal getPercentualAdicional() { return percentualAdicional; }
    public void setPercentualAdicional(BigDecimal percentualAdicional) { this.percentualAdicional = percentualAdicional; }
    public OffsetDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(OffsetDateTime criadoEm) { this.criadoEm = criadoEm; }
}
