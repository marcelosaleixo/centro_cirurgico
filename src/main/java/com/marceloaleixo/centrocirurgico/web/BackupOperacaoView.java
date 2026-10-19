package com.marceloaleixo.centrocirurgico.web;

import java.time.Instant;

public record BackupOperacaoView(
        String inicioUtc,
        String fimUtc,
        String acao,
        String status,
        String codigoSaida,
        String duracaoSegundos,
        String banco,
        String mensagem) {
    public boolean sucesso() { return "SUCCESS".equalsIgnoreCase(status); }
    public Instant instanteFim() {
        try { return Instant.parse(fimUtc); } catch (Exception ex) { return null; }
    }
}
