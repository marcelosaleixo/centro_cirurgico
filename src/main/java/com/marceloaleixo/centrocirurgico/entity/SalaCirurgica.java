package com.marceloaleixo.centrocirurgico.entity;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "sala_cirurgica")
public class SalaCirurgica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 80)
    private String nome;
    @Column(length = 160)
    private String localizacao;
    @Column(nullable = false)
    private boolean ativa = true;
    @OneToMany(mappedBy = "salaCirurgica")
    private List<Cirurgia> cirurgias = new ArrayList<>();
    @OneToMany(mappedBy = "salaCirurgica")
    private List<ArmarioSala> armarios = new ArrayList<>();

    public SalaCirurgica() {
    }
    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return this.nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getLocalizacao() { return this.localizacao; }
    public void setLocalizacao(String localizacao) { this.localizacao = localizacao; }
    public boolean isAtiva() { return this.ativa; }
    public void setAtiva(boolean ativa) { this.ativa = ativa; }
    public List<Cirurgia> getCirurgias() { return this.cirurgias; }
    public void setCirurgias(List<Cirurgia> cirurgias) { this.cirurgias = cirurgias; }
    public List<ArmarioSala> getArmarios() { return this.armarios; }
    public void setArmarios(List<ArmarioSala> armarios) { this.armarios = armarios; }
}
