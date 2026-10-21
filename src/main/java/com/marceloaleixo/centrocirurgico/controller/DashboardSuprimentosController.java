package com.marceloaleixo.centrocirurgico.controller;

import com.marceloaleixo.centrocirurgico.entity.ItemSolicitacaoMutirao;
import com.marceloaleixo.centrocirurgico.entity.SolicitacaoMutirao;
import com.marceloaleixo.centrocirurgico.repository.SolicitacaoMutiraoRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/mutiroes/dashboard-suprimentos")
public class DashboardSuprimentosController {
    private final SolicitacaoMutiraoRepository repository;

    public DashboardSuprimentosController(SolicitacaoMutiraoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public String dashboard(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            Model model) {
        List<SolicitacaoMutirao> filtradas = filtrar(status, dataInicio, dataFim);
        preencherIndicadores(model, filtradas);
        model.addAttribute("ultimasSolicitacoes", filtradas.stream().limit(8).toList());
        model.addAttribute("filtroStatus", status == null ? "" : status.trim().toUpperCase(Locale.ROOT));
        model.addAttribute("dataInicio", dataInicio);
        model.addAttribute("dataFim", dataFim);
        model.addAttribute("quantidadeFiltrada", filtradas.size());
        return "mutiroes/dashboard-suprimentos";
    }

    @GetMapping("/exportar.csv")
    public void exportarCsv(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            HttpServletResponse response) throws IOException {
        List<SolicitacaoMutirao> filtradas = filtrar(status, dataInicio, dataFim);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=dashboard-suprimentos.csv");
        var out = response.getWriter();
        out.write("\uFEFFID;Mutirão;Solicitante;Status;Criada em;Itens;Quantidade solicitada;Quantidade separada;Quantidade pendente\r\n");
        for (SolicitacaoMutirao s : filtradas) {
            BigDecimal solicitada = BigDecimal.ZERO;
            BigDecimal separada = BigDecimal.ZERO;
            for (ItemSolicitacaoMutirao item : s.getItens()) {
                solicitada = solicitada.add(nuloZero(item.getQuantidadeSolicitada()));
                separada = separada.add(nuloZero(item.getQuantidadeSeparada()));
            }
            String evento = s.getDia() != null && s.getDia().getEvento() != null ? s.getDia().getEvento().getNome() : "Mutirão não informado";
            String criadaEm = s.getCriadoEm() == null ? "" : s.getCriadoEm().toString();
            out.write(csv(s.getId()) + ";" + csv(evento) + ";" + csv(s.getSolicitante()) + ";" + csv(s.getStatus()) + ";" + csv(criadaEm) + ";" + s.getItens().size() + ";" + solicitada.toPlainString() + ";" + separada.toPlainString() + ";" + solicitada.subtract(separada).max(BigDecimal.ZERO).toPlainString() + "\r\n");
        }
        out.flush();
    }

    private List<SolicitacaoMutirao> filtrar(String status, LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("A data inicial não pode ser posterior à data final.");
        }
        String statusNormalizado = status == null ? "" : status.trim().toUpperCase(Locale.ROOT);
        return repository.findAllByOrderByCriadoEmDesc().stream()
                .filter(s -> statusNormalizado.isBlank() || statusNormalizado.equalsIgnoreCase(s.getStatus()))
                .filter(s -> dataInicio == null || (s.getCriadoEm() != null && !s.getCriadoEm().toLocalDate().isBefore(dataInicio)))
                .filter(s -> dataFim == null || (s.getCriadoEm() != null && !s.getCriadoEm().toLocalDate().isAfter(dataFim)))
                .collect(Collectors.toList());
    }

    private void preencherIndicadores(Model model, List<SolicitacaoMutirao> solicitacoes) {
        Map<String, Long> porStatus = new LinkedHashMap<>();
        porStatus.put("RASCUNHO", 0L);
        porStatus.put("ENVIADA", 0L);
        porStatus.put("APROVADA", 0L);
        porStatus.put("EM_SEPARACAO", 0L);
        porStatus.put("ENTREGUE", 0L);
        BigDecimal totalSolicitado = BigDecimal.ZERO;
        BigDecimal totalSeparado = BigDecimal.ZERO;
        BigDecimal totalPendente = BigDecimal.ZERO;
        long itensComPendencia = 0;
        long itensComSeparacaoParcial = 0;
        for (SolicitacaoMutirao s : solicitacoes) {
            String estado = s.getStatus() == null ? "OUTROS" : s.getStatus().toUpperCase(Locale.ROOT);
            porStatus.put(estado, porStatus.getOrDefault(estado, 0L) + 1L);
            for (ItemSolicitacaoMutirao item : s.getItens()) {
                BigDecimal pedida = nuloZero(item.getQuantidadeSolicitada());
                BigDecimal separada = nuloZero(item.getQuantidadeSeparada());
                BigDecimal pendente = pedida.subtract(separada).max(BigDecimal.ZERO);
                totalSolicitado = totalSolicitado.add(pedida);
                totalSeparado = totalSeparado.add(separada);
                totalPendente = totalPendente.add(pendente);
                if (pendente.signum() > 0) itensComPendencia++;
                if (separada.signum() > 0 && separada.compareTo(pedida) < 0) itensComSeparacaoParcial++;
            }
        }
        model.addAttribute("totalSolicitacoes", solicitacoes.size());
        model.addAttribute("porStatus", porStatus);
        model.addAttribute("totalSolicitado", totalSolicitado);
        model.addAttribute("totalSeparado", totalSeparado);
        model.addAttribute("totalPendente", totalPendente);
        model.addAttribute("itensComPendencia", itensComPendencia);
        model.addAttribute("itensComSeparacaoParcial", itensComSeparacaoParcial);
    }

    private static BigDecimal nuloZero(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor;
    }

    private static String csv(Object valor) {
        String texto = valor == null ? "" : valor.toString();
        return "\"" + texto.replace("\"", "\"\"") + "\"";
    }
}
