package com.marceloaleixo.centrocirurgico.web;

import com.marceloaleixo.centrocirurgico.entity.EscalaPlantao;
import com.marceloaleixo.centrocirurgico.entity.TrocaPlantao;

import java.time.LocalDate;
import java.util.List;

/** Modelo de apresentação de um dia do calendário, com escalas e trocas associadas. */
public class DiaCalendarioEscala {
    private final LocalDate data;
    private final List<EscalaPlantao> escalas;
    private final List<TrocaPlantao> trocas;
    private final boolean mesAtual;
    private final String feriadoNome;

    public DiaCalendarioEscala(LocalDate data, List<EscalaPlantao> escalas,
                               List<TrocaPlantao> trocas, boolean mesAtual, String feriadoNome) {
        this.data = data;
        this.escalas = escalas == null ? List.of() : List.copyOf(escalas);
        this.trocas = trocas == null ? List.of() : List.copyOf(trocas);
        this.mesAtual = mesAtual;
        this.feriadoNome = feriadoNome;
    }

    public LocalDate getData() { return data; }
    public List<EscalaPlantao> getEscalas() { return escalas; }
    public List<TrocaPlantao> getTrocas() { return trocas; }
    public boolean isMesAtual() { return mesAtual; }
    public String getFeriadoNome() { return feriadoNome; }
    public String getDiaSemana() {
        return data.getDayOfWeek().getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.forLanguageTag("pt-BR"));
    }
    public boolean isFimDeSemana() { return data.getDayOfWeek().getValue() > 5; }
    public boolean isSemEscalas() { return escalas.isEmpty(); }
    public boolean isSemEventos() { return escalas.isEmpty() && trocas.isEmpty(); }
}
