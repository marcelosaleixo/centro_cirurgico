package com.marceloaleixo.centrocirurgico.web;

import com.marceloaleixo.centrocirurgico.enums.TipoPlantao;
import com.marceloaleixo.centrocirurgico.enums.RegimeTrabalho;
import java.time.LocalDate;

public class ColaboradorForm {
    private Long id;
    private String nome;
    private String cpf;
    private String registroProfissional;
    private String email;
    private String telefone;
    private Long funcaoId;
    private Long equipeId;
    private TipoPlantao plantaoReferencia;
    private RegimeTrabalho regimeTrabalho = RegimeTrabalho.DOZE_TRINTA_SEIS;
    private LocalDate dataReferenciaPlantao;
    private boolean ativo = true;

    public ColaboradorForm() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getRegistroProfissional() { return registroProfissional; }
    public void setRegistroProfissional(String registroProfissional) { this.registroProfissional = registroProfissional; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public Long getFuncaoId() { return funcaoId; }
    public void setFuncaoId(Long funcaoId) { this.funcaoId = funcaoId; }
    public Long getEquipeId() { return equipeId; }
    public void setEquipeId(Long equipeId) { this.equipeId = equipeId; }
    public RegimeTrabalho getRegimeTrabalho() { return regimeTrabalho; }
    public void setRegimeTrabalho(RegimeTrabalho regimeTrabalho) { this.regimeTrabalho = regimeTrabalho; }
    public TipoPlantao getPlantaoReferencia() { return plantaoReferencia; }
    public void setPlantaoReferencia(TipoPlantao plantaoReferencia) { this.plantaoReferencia = plantaoReferencia; }
    public LocalDate getDataReferenciaPlantao() { return dataReferenciaPlantao; }
    public void setDataReferenciaPlantao(LocalDate dataReferenciaPlantao) { this.dataReferenciaPlantao = dataReferenciaPlantao; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}
