package com.marceloaleixo.centrocirurgico.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "auditoria_alteracoes", indexes = {
        @Index(name = "idx_aud_alt_data", columnList = "ocorrido_em"),
        @Index(name = "idx_aud_alt_entidade", columnList = "entidade, entidade_id")
})
public class AuditoriaAlteracao {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "ocorrido_em", nullable = false)
    private OffsetDateTime ocorridoEm;
    @Column(nullable = false, length = 100)
    private String usuario;
    @Column(nullable = false, length = 100)
    private String entidade;
    @Column(name = "entidade_id", length = 80)
    private String entidadeId;
    @Column(nullable = false, length = 100)
    private String campo;
    @Column(name = "valor_anterior", length = 1000)
    private String valorAnterior;
    @Column(name = "valor_novo", length = 1000)
    private String valorNovo;
    @Column(nullable = false, length = 20)
    private String operacao;

    protected AuditoriaAlteracao() {}
    public AuditoriaAlteracao(String usuario, String entidade, String entidadeId, String campo,
                              String valorAnterior, String valorNovo, String operacao) {
        this.ocorridoEm = OffsetDateTime.now();
        this.usuario = limpar(usuario, 100);
        this.entidade = limpar(entidade, 100);
        this.entidadeId = limpar(entidadeId, 80);
        this.campo = limpar(campo, 100);
        this.valorAnterior = limpar(valorAnterior, 1000);
        this.valorNovo = limpar(valorNovo, 1000);
        this.operacao = limpar(operacao, 20);
    }
    private static String limpar(String v, int max) {
        if (v == null) return null;
        String s = v.replaceAll("[\r\n\t]", " ").trim();
        return s.length() > max ? s.substring(0, max) : s;
    }
    public Long getId() { return id; }
    public OffsetDateTime getOcorridoEm() { return ocorridoEm; }
    public String getUsuario() { return usuario; }
    public String getEntidade() { return entidade; }
    public String getEntidadeId() { return entidadeId; }
    public String getCampo() { return campo; }
    public String getValorAnterior() { return valorAnterior; }
    public String getValorNovo() { return valorNovo; }
    public String getOperacao() { return operacao; }
}
