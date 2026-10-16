package com.marceloaleixo.centrocirurgico.web;

public record ResultadoGeracaoEscala(int programados, int ferias, int bancoHoras) {
    public int totalCriados() { return programados + ferias + bancoHoras; }
}
