package com.marceloaleixo.centrocirurgico.service;

import com.marceloaleixo.centrocirurgico.web.BackupOperacaoView;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class MonitoramentoBackupService {
    private final Path statusPath;
    private final Path historyPath;
    private final long maxAgeHours;

    public MonitoramentoBackupService(
            @Value("${app.backup.status-file:/backups/backup-last-status.txt}") String statusFile,
            @Value("${app.backup.history-file:/backups/backup-operations.csv}") String historyFile,
            @Value("${app.backup.max-age-hours:36}") long maxAgeHours) {
        this.statusPath = Path.of(statusFile);
        this.historyPath = Path.of(historyFile);
        this.maxAgeHours = Math.max(1, maxAgeHours);
    }

    public PainelBackup carregar() {
        Map<String, String> status = lerStatus();
        List<BackupOperacaoView> historico = lerHistorico();
        BackupOperacaoView ultimoBackup = historico.stream()
                .filter(r -> "backup".equalsIgnoreCase(r.acao()))
                .max(Comparator.comparing(BackupOperacaoView::instanteFim, Comparator.nullsFirst(Comparator.naturalOrder())))
                .orElse(null);
        BackupOperacaoView ultimoEnvio = historico.stream()
                .filter(r -> "upload-s3".equalsIgnoreCase(r.acao()))
                .max(Comparator.comparing(BackupOperacaoView::instanteFim, Comparator.nullsFirst(Comparator.naturalOrder())))
                .orElse(null);
        BackupOperacaoView ultimaValidacao = historico.stream()
                .filter(r -> "verify".equalsIgnoreCase(r.acao()))
                .max(Comparator.comparing(BackupOperacaoView::instanteFim, Comparator.nullsFirst(Comparator.naturalOrder())))
                .orElse(null);
        Instant instanteBackup = ultimoBackup == null ? null : ultimoBackup.instanteFim();
        boolean backupAtrasado = instanteBackup == null || instanteBackup.isBefore(Instant.now().minus(Duration.ofHours(maxAgeHours)));
        long falhasRecentes = historico.stream().filter(r -> !r.sucesso()).limit(100).count();
        return new PainelBackup(status, historico.stream().sorted(Comparator.comparing(BackupOperacaoView::instanteFim,
                Comparator.nullsFirst(Comparator.naturalOrder())).reversed()).limit(30).toList(),
                ultimoBackup, ultimoEnvio, ultimaValidacao, backupAtrasado, maxAgeHours, falhasRecentes,
                Files.isReadable(statusPath), Files.isReadable(historyPath));
    }

    private Map<String, String> lerStatus() {
        Map<String, String> result = new LinkedHashMap<>();
        if (!Files.isReadable(statusPath)) return result;
        try {
            for (String line : Files.readAllLines(statusPath, StandardCharsets.UTF_8)) {
                int ix = line.indexOf('=');
                if (ix > 0) result.put(line.substring(0, ix).trim(), line.substring(ix + 1).trim());
            }
        } catch (IOException ignored) { /* Painel informa a indisponibilidade pelos indicadores de arquivo. */ }
        return result;
    }

    private List<BackupOperacaoView> lerHistorico() {
        List<BackupOperacaoView> rows = new ArrayList<>();
        if (!Files.isReadable(historyPath)) return rows;
        try (BufferedReader reader = Files.newBufferedReader(historyPath, StandardCharsets.UTF_8)) {
            String line = reader.readLine(); // cabeçalho
            while ((line = reader.readLine()) != null) {
                List<String> cols = parseCsvLine(line);
                if (cols.size() < 9) continue;
                rows.add(new BackupOperacaoView(cols.get(0), cols.get(1), cols.get(2), cols.get(3), cols.get(4), cols.get(5), cols.get(7), cols.get(8)));
            }
        } catch (IOException ignored) { return List.of(); }
        return rows;
    }

    // Parser CSV compatível com aspas duplicadas usadas por run-and-record.sh.
    private List<String> parseCsvLine(String line) {
        List<String> result = new ArrayList<>(); StringBuilder field = new StringBuilder(); boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (quoted && i + 1 < line.length() && line.charAt(i + 1) == '"') { field.append('"'); i++; }
                else quoted = !quoted;
            } else if (c == ',' && !quoted) { result.add(field.toString()); field.setLength(0); }
            else field.append(c);
        }
        result.add(field.toString()); return result;
    }

    public record PainelBackup(Map<String, String> statusAtual, List<BackupOperacaoView> historico,
            BackupOperacaoView ultimoBackup, BackupOperacaoView ultimoEnvioS3,
            BackupOperacaoView ultimaValidacao, boolean backupAtrasado, long limiteHoras,
            long falhasHistorico, boolean arquivoStatusDisponivel, boolean arquivoHistoricoDisponivel) {}
}
