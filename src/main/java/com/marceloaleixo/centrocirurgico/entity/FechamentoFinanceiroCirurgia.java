package com.marceloaleixo.centrocirurgico.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "fechamento_financeiro_cirurgia", uniqueConstraints = @UniqueConstraint(name="uk_fechamento_financeiro_cirurgia", columnNames="cirurgia_id"))
public class FechamentoFinanceiroCirurgia {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @OneToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="cirurgia_id", nullable=false) private Cirurgia cirurgia;
    @Column(nullable=false, length=20) private String status = "ABERTO";
    @Column(name="responsavel_abertura", nullable=false, length=120) private String responsavelAbertura;
    @Column(name="responsavel_aprovacao", length=120) private String responsavelAprovacao;
    @Column(name="observacao_abertura", columnDefinition="text") private String observacaoAbertura;
    @Column(name="observacao_aprovacao", columnDefinition="text") private String observacaoAprovacao;
    @Column(name="aberto_em", nullable=false) private OffsetDateTime abertoEm;
    @Column(name="aprovado_em") private OffsetDateTime aprovadoEm;
    @Column(name="reaberto_em") private OffsetDateTime reabertoEm;
    @Column(name="responsavel_reabertura", length=120) private String responsavelReabertura;
    @Column(name="motivo_reabertura", columnDefinition="text") private String motivoReabertura;
    public Long getId(){return id;} public Cirurgia getCirurgia(){return cirurgia;} public void setCirurgia(Cirurgia v){cirurgia=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;}
    public String getResponsavelAbertura(){return responsavelAbertura;} public void setResponsavelAbertura(String v){responsavelAbertura=v;}
    public String getResponsavelAprovacao(){return responsavelAprovacao;} public void setResponsavelAprovacao(String v){responsavelAprovacao=v;}
    public String getObservacaoAbertura(){return observacaoAbertura;} public void setObservacaoAbertura(String v){observacaoAbertura=v;}
    public String getObservacaoAprovacao(){return observacaoAprovacao;} public void setObservacaoAprovacao(String v){observacaoAprovacao=v;}
    public OffsetDateTime getAbertoEm(){return abertoEm;} public void setAbertoEm(OffsetDateTime v){abertoEm=v;}
    public OffsetDateTime getAprovadoEm(){return aprovadoEm;} public void setAprovadoEm(OffsetDateTime v){aprovadoEm=v;}
    public OffsetDateTime getReabertoEm(){return reabertoEm;} public void setReabertoEm(OffsetDateTime v){reabertoEm=v;}
    public String getResponsavelReabertura(){return responsavelReabertura;} public void setResponsavelReabertura(String v){responsavelReabertura=v;}
    public String getMotivoReabertura(){return motivoReabertura;} public void setMotivoReabertura(String v){motivoReabertura=v;}
}
