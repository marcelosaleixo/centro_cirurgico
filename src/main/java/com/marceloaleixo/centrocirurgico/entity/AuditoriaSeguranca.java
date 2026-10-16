package com.marceloaleixo.centrocirurgico.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "auditoria_seguranca")
public class AuditoriaSeguranca {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="ocorrido_em", nullable=false)
    private OffsetDateTime ocorridoEm;
    @Column(nullable=false, length=100)
    private String usuario;
    @Column(nullable=false, length=60)
    private String acao;
    @Column(length=1000)
    private String detalhes;
    @Column(nullable=false)
    private boolean sucesso;

    protected AuditoriaSeguranca() {}
    public AuditoriaSeguranca(String usuario, String acao, String detalhes, boolean sucesso) {
        this.ocorridoEm = OffsetDateTime.now();
        this.usuario = usuario;
        this.acao = acao;
        this.detalhes = detalhes;
        this.sucesso = sucesso;
    }
    public Long getId(){return id;}
    public OffsetDateTime getOcorridoEm(){return ocorridoEm;}
    public String getUsuario(){return usuario;}
    public String getAcao(){return acao;}
    public String getDetalhes(){return detalhes;}
    public boolean isSucesso(){return sucesso;}
}
