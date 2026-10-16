package com.marceloaleixo.centrocirurgico.entity;

import jakarta.persistence.*;
import java.time.*;

@Entity
@Table(name = "paciente")
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 160)
    private String nome;
    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;
    @Column(length = 20)
    private String sexo;
    @Column(unique = true, length = 50)
    private String prontuario;
    @Column(name = "identificador_externo", unique = true, length = 80)
    private String identificadorExterno;
    @Column(name = "criado_em", insertable = false, updatable = false)
    private OffsetDateTime criadoEm;

    public Paciente() {
    }
    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return this.nome; }
    public void setNome(String nome) { this.nome = nome; }
    public LocalDate getDataNascimento() { return this.dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    public String getSexo() { return this.sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }
    public String getProntuario() { return this.prontuario; }
    public void setProntuario(String prontuario) { this.prontuario = prontuario; }
    public String getIdentificadorExterno() { return this.identificadorExterno; }
    public void setIdentificadorExterno(String identificadorExterno) { this.identificadorExterno = identificadorExterno; }
    public OffsetDateTime getCriadoEm() { return this.criadoEm; }
    public void setCriadoEm(OffsetDateTime criadoEm) { this.criadoEm = criadoEm; }
}
