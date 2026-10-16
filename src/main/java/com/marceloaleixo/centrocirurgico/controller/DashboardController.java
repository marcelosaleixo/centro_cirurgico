package com.marceloaleixo.centrocirurgico.controller;

import com.marceloaleixo.centrocirurgico.entity.EscalaPlantao;
import com.marceloaleixo.centrocirurgico.entity.ItemEstoque;
import com.marceloaleixo.centrocirurgico.enums.StatusEscala;
import com.marceloaleixo.centrocirurgico.repository.AuditoriaOperacaoRepository;
import com.marceloaleixo.centrocirurgico.repository.ColaboradorRepository;
import com.marceloaleixo.centrocirurgico.repository.EscalaPlantaoRepository;
import com.marceloaleixo.centrocirurgico.repository.ItemEstoqueRepository;
import com.marceloaleixo.centrocirurgico.repository.MovimentacaoEstoqueRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Comparator;

@Controller
public class DashboardController {
    private final ColaboradorRepository colaboradorRepository;
    private final ItemEstoqueRepository itemEstoqueRepository;
    private final EscalaPlantaoRepository escalaPlantaoRepository;
    private final AuditoriaOperacaoRepository auditoriaOperacaoRepository;
    private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;

    public DashboardController(ColaboradorRepository colaboradorRepository,
                               ItemEstoqueRepository itemEstoqueRepository,
                               EscalaPlantaoRepository escalaPlantaoRepository,
                               AuditoriaOperacaoRepository auditoriaOperacaoRepository,
                               MovimentacaoEstoqueRepository movimentacaoEstoqueRepository) {
        this.colaboradorRepository = colaboradorRepository;
        this.itemEstoqueRepository = itemEstoqueRepository;
        this.escalaPlantaoRepository = escalaPlantaoRepository;
        this.auditoriaOperacaoRepository = auditoriaOperacaoRepository;
        this.movimentacaoEstoqueRepository = movimentacaoEstoqueRepository;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            Model model) {
        LocalDate hoje = LocalDate.now();
        LocalDate dataFim = fim == null ? hoje : fim;
        LocalDate dataInicio = inicio == null ? dataFim.minusMonths(5).withDayOfMonth(1) : inicio;
        if (dataInicio.isAfter(dataFim)) {
            LocalDate temporaria = dataInicio;
            dataInicio = dataFim;
            dataFim = temporaria;
        }
        // Evita consultas e gráficos excessivamente grandes por parâmetros arbitrários.
        if (dataInicio.isBefore(dataFim.minusYears(3))) dataInicio = dataFim.minusYears(3);

        long itensBaixoEstoque = itemEstoqueRepository.findByAtivoTrueOrderByNomeAsc().stream()
                .filter(item -> item.getEstoqueGeral() == null
                        || item.getEstoqueGeral().getQuantidadeAtual() == null
                        || item.getEstoqueGeral().getQuantidadeAtual().compareTo(
                        item.getEstoqueMinimo() == null ? BigDecimal.ZERO : item.getEstoqueMinimo()) <= 0)
                .count();

        OffsetDateTime inicioInstante = dataInicio.atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime();
        OffsetDateTime fimInstante = dataFim.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime().minusNanos(1);
        long operacoesPeriodo = auditoriaOperacaoRepository.countByOcorridoEmBetween(inicioInstante, fimInstante);
        long movimentacoesPeriodo = movimentacaoEstoqueRepository.countByDataMovimentacaoBetween(inicioInstante, fimInstante);
        long plantoesPeriodo = escalaPlantaoRepository.countByDataPlantaoBetween(dataInicio, dataFim);
        long plantoesProgramados = escalaPlantaoRepository.countByDataPlantaoBetweenAndStatus(dataInicio, dataFim, StatusEscala.PROGRAMADO);

        List<Map<String, Object>> serieMensal = new ArrayList<>();
        long maiorValor = 1;
        YearMonth mesInicial = YearMonth.from(dataInicio);
        YearMonth mesFinal = YearMonth.from(dataFim);
        for (YearMonth mes = mesInicial; !mes.isAfter(mesFinal); mes = mes.plusMonths(1)) {
            LocalDate primeiroDia = mes.atDay(1).isBefore(dataInicio) ? dataInicio : mes.atDay(1);
            LocalDate ultimoDia = mes.atEndOfMonth().isAfter(dataFim) ? dataFim : mes.atEndOfMonth();
            OffsetDateTime mesInicio = primeiroDia.atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime();
            OffsetDateTime mesFim = ultimoDia.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime().minusNanos(1);
            long auditoria = auditoriaOperacaoRepository.countByOcorridoEmBetween(mesInicio, mesFim);
            long movimentacoes = movimentacaoEstoqueRepository.countByDataMovimentacaoBetween(mesInicio, mesFim);
            long escalas = escalaPlantaoRepository.countByDataPlantaoBetween(primeiroDia, ultimoDia);
            Map<String, Object> linha = new LinkedHashMap<>();
            linha.put("mes", mes.toString());
            linha.put("rotulo", mes.getMonth().getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.forLanguageTag("pt-BR")) + "/" + String.valueOf(mes.getYear()).substring(2));
            linha.put("auditoria", auditoria);
            linha.put("movimentacoes", movimentacoes);
            linha.put("escalas", escalas);
            serieMensal.add(linha);
            maiorValor = Math.max(maiorValor, Math.max(auditoria, Math.max(movimentacoes, escalas)));
        }

        // Alertas operacionais derivados dos registros existentes, sem alterar dados.
        List<Map<String, Object>> alertas = new ArrayList<>();
        List<ItemEstoque> itensCriticos = itemEstoqueRepository.findByAtivoTrueOrderByNomeAsc().stream()
                .filter(item -> item.getEstoqueGeral() == null
                        || item.getEstoqueGeral().getQuantidadeAtual() == null
                        || item.getEstoqueGeral().getQuantidadeAtual().compareTo(
                        item.getEstoqueMinimo() == null ? BigDecimal.ZERO : item.getEstoqueMinimo()) <= 0)
                .limit(10)
                .toList();
        for (ItemEstoque item : itensCriticos) {
            Map<String, Object> alerta = new LinkedHashMap<>();
            alerta.put("prioridade", "ALTA");
            alerta.put("categoria", "Estoque");
            alerta.put("titulo", "Verificar nível de estoque");
            BigDecimal quantidade = item.getEstoqueGeral() == null ? null : item.getEstoqueGeral().getQuantidadeAtual();
            alerta.put("mensagem", item.getNome() + " — saldo: "
                    + (quantidade == null ? "não informado" : quantidade.stripTrailingZeros().toPlainString())
                    + " " + item.getUnidadeMedida() + "; mínimo: "
                    + (item.getEstoqueMinimo() == null ? "0" : item.getEstoqueMinimo().stripTrailingZeros().toPlainString()));
            alerta.put("link", "/estoque");
            alertas.add(alerta);
        }

        LocalDate inicioJanelaEscalas = hoje.minusDays(7);
        List<EscalaPlantao> plantoesAtrasados = escalaPlantaoRepository
                .findByDataPlantaoBetweenOrderByDataPlantaoAsc(inicioJanelaEscalas, hoje.minusDays(1)).stream()
                .filter(e -> e.getStatus() == StatusEscala.PROGRAMADO)
                .toList();
        for (EscalaPlantao escala : plantoesAtrasados) {
            Map<String, Object> alerta = new LinkedHashMap<>();
            alerta.put("prioridade", "ALTA");
            alerta.put("categoria", "Escalas");
            alerta.put("titulo", "Plantão passado ainda programado");
            alerta.put("mensagem", escala.getColaborador().getNome() + " — "
                    + escala.getDataPlantao().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            alerta.put("link", "/escalas");
            alertas.add(alerta);
        }

        List<EscalaPlantao> proximosPlantoes = escalaPlantaoRepository
                .findByDataPlantaoBetweenOrderByDataPlantaoAsc(hoje, hoje.plusDays(7)).stream()
                .filter(e -> e.getStatus() == StatusEscala.PROGRAMADO)
                .toList();
        if (!proximosPlantoes.isEmpty()) {
            Map<String, Object> alerta = new LinkedHashMap<>();
            alerta.put("prioridade", "INFO");
            alerta.put("categoria", "Escalas");
            alerta.put("titulo", "Plantões programados nos próximos 7 dias");
            alerta.put("mensagem", proximosPlantoes.size() + " plantão(ões) programado(s) aguardam acompanhamento.");
            alerta.put("link", "/escalas");
            alertas.add(alerta);
        }
        alertas.sort(Comparator.comparing(a -> "ALTA".equals(a.get("prioridade")) ? 0 : 1));
        model.addAttribute("alertasOperacionais", alertas);
        model.addAttribute("quantidadeAlertasAlta", alertas.stream().filter(a -> "ALTA".equals(a.get("prioridade"))).count());

        model.addAttribute("colaboradoresAtivos", colaboradorRepository.countByAtivoTrue());
        model.addAttribute("colaboradoresInativos", colaboradorRepository.countByAtivoFalse());
        model.addAttribute("itensAtivos", itemEstoqueRepository.countByAtivoTrue());
        model.addAttribute("itensBaixoEstoque", itensBaixoEstoque);
        model.addAttribute("plantoesRestantesMes", escalaPlantaoRepository.countByDataPlantaoBetween(hoje, hoje.withDayOfMonth(hoje.lengthOfMonth())));
        model.addAttribute("operacoesUltimosSeteDias", auditoriaOperacaoRepository.countByOcorridoEmAfter(OffsetDateTime.now().minusDays(7)));
        model.addAttribute("operacoesPeriodo", operacoesPeriodo);
        model.addAttribute("movimentacoesPeriodo", movimentacoesPeriodo);
        model.addAttribute("plantoesPeriodo", plantoesPeriodo);
        model.addAttribute("plantoesProgramados", plantoesProgramados);
        model.addAttribute("serieMensal", serieMensal);
        model.addAttribute("maiorValorGrafico", maiorValor);
        model.addAttribute("inicio", dataInicio);
        model.addAttribute("fim", dataFim);
        model.addAttribute("dataAtualizacao", OffsetDateTime.now());
        return "dashboard";
    }
}
