package com.marceloaleixo.centrocirurgico.entity;

import jakarta.persistence.*;
import java.time.*;
import java.util.*;
import com.marceloaleixo.centrocirurgico.enums.RegimeTrabalho;
import com.marceloaleixo.centrocirurgico.enums.TipoPlantao;

@Entity
@Table(name = "colaborador")
public class Colaborador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 160)
    private String nome;
    @Column(unique = true, length = 11)
    private String cpf;
    @Column(name = "registro_profissional", length = 50)
    private String registroProfissional;
    @Column(unique = true, length = 160)
    private String email;
    @Column(length = 30)
    private String telefone;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "funcao_id", nullable = false)
    private FuncaoProfissional funcao;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipe_id")
    private Equipe equipe;
    @Enumerated(EnumType.STRING)
    @Column(name = "regime_trabalho", nullable = false, length = 20)
    private RegimeTrabalho regimeTrabalho = RegimeTrabalho.DOZE_TRINTA_SEIS;
    @Enumerated(EnumType.STRING)
    @Column(name = "plantao_referencia", nullable = false, length = 10)
    private TipoPlantao plantaoReferencia;
    @Column(name = "data_referencia_plantao", nullable = false)
    private LocalDate dataReferenciaPlantao;
    @Column(nullable = false)
    private boolean ativo = true;
    @Column(name = "criado_em", insertable = false, updatable = false)
    private OffsetDateTime criadoEm;
    @OneToMany(mappedBy = "colaborador")
    private List<EscalaPlantao> escalas = new ArrayList<>();

    public Colaborador() {
    }

    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return this.nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return this.cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getRegistroProfissional() { return this.registroProfissional; }
    public void setRegistroProfissional(String registroProfissional) { this.registroProfissional = registroProfissional; }

    public String getEmail() { return this.email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return this.telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public FuncaoProfissional getFuncao() { return this.funcao; }
    public void setFuncao(FuncaoProfissional funcao) { this.funcao = funcao; }

    public Equipe getEquipe() { return this.equipe; }
    public void setEquipe(Equipe equipe) { this.equipe = equipe; }

    public RegimeTrabalho getRegimeTrabalho() { return this.regimeTrabalho; }
    public void setRegimeTrabalho(RegimeTrabalho regimeTrabalho) { this.regimeTrabalho = regimeTrabalho; }

    public TipoPlantao getPlantaoReferencia() { return this.plantaoReferencia; }
    public void setPlantaoReferencia(TipoPlantao plantaoReferencia) { this.plantaoReferencia = plantaoReferencia; }

    public LocalDate getDataReferenciaPlantao() { return this.dataReferenciaPlantao; }
    public void setDataReferenciaPlantao(LocalDate dataReferenciaPlantao) { this.dataReferenciaPlantao = dataReferenciaPlantao; }

    /**
     * Retorna se o colaborador está ativo.
     * O nome isAtivo() é o padrão JavaBeans para atributos boolean.
     */
    public boolean isAtivo() {
        return this.ativo;
    }

    /**
     * Mantido para compatibilidade com códigos que já chamem getAtivo().
     */
    public boolean getAtivo() {
        return this.ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public OffsetDateTime getCriadoEm() { return this.criadoEm; }

    public List<EscalaPlantao> getEscalas() { return this.escalas; }
    public void setEscalas(List<EscalaPlantao> escalas) { this.escalas = escalas; }

}
