package com.marceloaleixo.centrocirurgico.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "usuarios_sistema")
public class UsuarioSistema {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 100)
    private String username;
    @Column(name = "senha_hash", nullable = false, length = 100)
    private String senhaHash;
    @Column(nullable = false, length = 30)
    private String perfil;
    @Column(nullable = false)
    private boolean ativo = true;
    @Column(name = "tentativas_falhas", nullable = false)
    private int tentativasFalhas = 0;
    @Column(name = "bloqueado_ate")
    private OffsetDateTime bloqueadoAte;

    protected UsuarioSistema() {}
    public UsuarioSistema(String username, String senhaHash, String perfil, boolean ativo) {
        this.username = username; this.senhaHash = senhaHash; this.perfil = perfil; this.ativo = ativo;
    }
    public Long getId(){return id;}
    public String getUsername(){return username;}
    public String getSenhaHash(){return senhaHash;}
    public String getPerfil(){return perfil;}
    public boolean isAtivo(){return ativo;}
    public int getTentativasFalhas(){return tentativasFalhas;}
    public OffsetDateTime getBloqueadoAte(){return bloqueadoAte;}
    public boolean isBloqueadoAgora(){return bloqueadoAte != null && bloqueadoAte.isAfter(OffsetDateTime.now());}
    public void setUsername(String v){username=v;}
    public void setSenhaHash(String v){senhaHash=v;}
    public void setPerfil(String v){perfil=v;}
    public void setAtivo(boolean v){ativo=v;}
    public void setTentativasFalhas(int v){tentativasFalhas=v;}
    public void setBloqueadoAte(OffsetDateTime v){bloqueadoAte=v;}
}
