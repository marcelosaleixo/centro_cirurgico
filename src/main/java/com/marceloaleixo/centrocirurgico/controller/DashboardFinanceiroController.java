package com.marceloaleixo.centrocirurgico.controller;

import com.marceloaleixo.centrocirurgico.entity.ConsumoCirurgiaLote;
import com.marceloaleixo.centrocirurgico.repository.ConsumoCirurgiaLoteRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Controller
@RequestMapping("/relatorios/dashboard-financeiro")
public class DashboardFinanceiroController {
    private final ConsumoCirurgiaLoteRepository consumoRepository;

    public DashboardFinanceiroController(ConsumoCirurgiaLoteRepository consumoRepository) {
        this.consumoRepository = consumoRepository;
    }

    @GetMapping
    public String dashboard(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            Model model) {
        if (inicio != null && fim != null && inicio.isAfter(fim)) {
            throw new IllegalArgumentException("A data inicial não pode ser posterior à data final.");
        }
        OffsetDateTime de = inicio == null ? null : inicio.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime ate = fim == null ? null : fim.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);
        List<ConsumoCirurgiaLote> registros = consumoRepository.relatorio(null, de, ate);

        BigDecimal quantidadeTotal = BigDecimal.ZERO;
        BigDecimal custoTotal = BigDecimal.ZERO;
        boolean custosCompletos = !registros.isEmpty();
        Map<Long, Map<String, Object>> porCirurgia = new LinkedHashMap<>();
        Map<String, BigDecimal> porMaterial = new HashMap<>();
        for (ConsumoCirurgiaLote c : registros) {
            BigDecimal qtd = c.getQuantidade() == null ? BigDecimal.ZERO : c.getQuantidade();
            quantidadeTotal = quantidadeTotal.add(qtd);
            BigDecimal unitario = c.getLote().getCustoUnitario();
            if (unitario == null) custosCompletos = false;
            BigDecimal custoLinha = unitario == null ? BigDecimal.ZERO : qtd.multiply(unitario);
            custoTotal = custoTotal.add(custoLinha);
            Long id = c.getCirurgia().getId();
            Map<String, Object> resumo = porCirurgia.computeIfAbsent(id, k -> {
                Map<String, Object> m = new HashMap<>();
                m.put("id", id);
                m.put("data", c.getCirurgia().getDataCirurgia());
                m.put("paciente", c.getCirurgia().getPaciente().getNome());
                m.put("procedimento", c.getCirurgia().getProcedimento().getNome());
                m.put("registros", 0);
                m.put("custo", BigDecimal.ZERO);
                return m;
            });
            resumo.put("registros", (Integer) resumo.get("registros") + 1);
            resumo.put("custo", ((BigDecimal) resumo.get("custo")).add(custoLinha));
            porMaterial.merge(c.getItem().getNome(), custoLinha, BigDecimal::add);
        }
        List<Map<String, Object>> cirurgias = new ArrayList<>(porCirurgia.values());
        cirurgias.sort((a, b) -> ((BigDecimal) b.get("custo")).compareTo((BigDecimal) a.get("custo")));
        List<Map.Entry<String, BigDecimal>> materiais = new ArrayList<>(porMaterial.entrySet());
        materiais.sort(Map.Entry.<String, BigDecimal>comparingByValue().reversed());

        model.addAttribute("inicio", inicio);
        model.addAttribute("fim", fim);
        model.addAttribute("totalRegistros", registros.size());
        model.addAttribute("totalCirurgias", porCirurgia.size());
        model.addAttribute("quantidadeTotal", quantidadeTotal);
        model.addAttribute("custoTotal", custoTotal);
        model.addAttribute("custosCompletos", custosCompletos);
        model.addAttribute("cirurgias", cirurgias);
        model.addAttribute("materiais", materiais.stream().limit(10).toList());
        return "relatorios/dashboard-financeiro";
    }
}
