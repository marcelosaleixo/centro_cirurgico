package com.marceloaleixo.centrocirurgico.web;

import com.marceloaleixo.centrocirurgico.entity.Colaborador;

import java.math.BigDecimal;

public class ResumoFechamentoRh {
    private final Colaborador colaborador;
    private int plantõesProgramados;
    private int plantõesRealizados;
    private int ferias;
    private int bancoHoras;
    private int cancelados;
    private int finaisDeSemana;
    private int feriadosIdentificados;
    private long minutosPrevistos;
    private long minutosRealizados;
    private String statusConferencia = "PENDENTE";
    private String observacaoConferencia = "";

    public ResumoFechamentoRh(Colaborador colaborador) { this.colaborador = colaborador; }
    public Colaborador getColaborador() { return colaborador; }
    public int getPlantoesProgramados() { return plantõesProgramados; }
    public int getPlantoesRealizados() { return plantõesRealizados; }
    public int getFerias() { return ferias; }
    public int getBancoHoras() { return bancoHoras; }
    public int getCancelados() { return cancelados; }
    public int getFinaisDeSemana() { return finaisDeSemana; }
    public int getFeriadosIdentificados() { return feriadosIdentificados; }
    public long getMinutosPrevistos() { return minutosPrevistos; }
    public long getMinutosRealizados() { return minutosRealizados; }
    public String getHorasPrevistasFormatadas() { return formatar(minutosPrevistos); }
    public String getHorasRealizadasFormatadas() { return formatar(minutosRealizados); }
    public String getStatusConferencia() { return statusConferencia; }
    public void setStatusConferencia(String status) { this.statusConferencia = status; }
    public String getObservacaoConferencia() { return observacaoConferencia; }
    public void setObservacaoConferencia(String observacao) { this.observacaoConferencia = observacao == null ? "" : observacao; }
    public void registrarProgramado(long minutos) { plantõesProgramados++; minutosPrevistos += Math.max(0, minutos); }
    public void registrarRealizado(long minutos) { plantõesRealizados++; minutosRealizados += Math.max(0, minutos); }
    public void registrarFerias() { ferias++; }
    public void registrarBancoHoras() { bancoHoras++; }
    public void registrarCancelado() { cancelados++; }
    public void registrarFimDeSemana() { finaisDeSemana++; }
    public void registrarFeriado() { feriadosIdentificados++; }
    private String formatar(long minutos) { return String.format("%02d:%02d", minutos / 60, minutos % 60); }
}
