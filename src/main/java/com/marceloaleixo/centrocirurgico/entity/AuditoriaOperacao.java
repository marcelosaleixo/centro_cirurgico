package com.marceloaleixo.centrocirurgico.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "auditoria_operacoes")
public class AuditoriaOperacao {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "ocorrido_em", nullable = false)
    private OffsetDateTime ocorridoEm;
    @Column(nullable = false, length = 100)
    private String usuario;
    @Column(nullable = false, length = 10)
    private String metodo;
    @Column(nullable = false, length = 300)
    private String recurso;
    @Column(name = "codigo_http", nullable = false)
    private int codigoHttp;
    @Column(nullable = false)
    private boolean sucesso;

    protected AuditoriaOperacao() {}
    public AuditoriaOperacao(String usuario, String metodo, String recurso, int codigoHttp) {
        this.ocorridoEm = OffsetDateTime.now();
        this.usuario = limitar(usuario == null || usuario.isBlank() ? "anônimo" : usuario, 100);
        this.metodo = limitar(metodo, 10);
        this.recurso = limitar(recurso, 300);
        this.codigoHttp = codigoHttp;
        this.sucesso = codigoHttp >= 200 && codigoHttp < 400;
    }
    private static String limitar(String valor, int tamanho) {
        if (valor == null) return "";
        String limpo = valor.replaceAll("[\r\n]", " ").trim();
        return limpo.length() > tamanho ? limpo.substring(0, tamanho) : limpo;
    }
    public Long getId() { return id; }
    public OffsetDateTime getOcorridoEm() { return ocorridoEm; }
    public String getUsuario() { return usuario; }
    public String getMetodo() { return metodo; }
    public String getRecurso() { return recurso; }
    public int getCodigoHttp() { return codigoHttp; }
    public boolean isSucesso() { return sucesso; }
}
