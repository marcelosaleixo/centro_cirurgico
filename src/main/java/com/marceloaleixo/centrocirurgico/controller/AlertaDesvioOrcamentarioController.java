package com.marceloaleixo.centrocirurgico.controller;

import com.marceloaleixo.centrocirurgico.repository.ConsumoCirurgiaLoteRepository;
import com.marceloaleixo.centrocirurgico.repository.OrcamentoMaterialProcedimentoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Controller
@RequestMapping("/orcamentos/alertas")
public class AlertaDesvioOrcamentarioController {
    private final OrcamentoMaterialProcedimentoRepository orcamentos;
    private final ConsumoCirurgiaLoteRepository consumos;

    public AlertaDesvioOrcamentarioController(OrcamentoMaterialProcedimentoRepository orcamentos,
                                              ConsumoCirurgiaLoteRepository consumos) {
        this.orcamentos = orcamentos;
        this.consumos = consumos;
    }

    @GetMapping
    public String listar(@RequestParam(defaultValue = "10") BigDecimal tolerancia, Model model) {
        if (tolerancia == null || tolerancia.signum() < 0 || tolerancia.compareTo(new BigDecimal("500")) > 0) {
            tolerancia = new BigDecimal("10");
        }
        final BigDecimal limiteTolerancia = tolerancia;
        var registros = consumos.findTop300ByOrderByRegistradoEmDesc();
        var linhas = orcamentos.findAllByOrderByProcedimentoNomeAscItemNomeAsc().stream().map(o -> {
            BigDecimal realizado = registros.stream()
                    .filter(c -> c.getCirurgia() != null && c.getCirurgia().getProcedimento() != null
                            && c.getCirurgia().getProcedimento().getId().equals(o.getProcedimento().getId())
                            && c.getItem() != null && c.getItem().getId().equals(o.getItem().getId()))
                    .filter(c -> c.getLote() != null && c.getLote().getCustoUnitario() != null)
                    .map(c -> c.getQuantidade().multiply(c.getLote().getCustoUnitario()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal previsto = o.getQuantidadePrevista().multiply(o.getCustoUnitarioPrevisto());
            BigDecimal desvio = realizado.subtract(previsto);
            BigDecimal percentual = previsto.signum() == 0 ? BigDecimal.ZERO
                    : desvio.multiply(new BigDecimal("100")).divide(previsto, 2, RoundingMode.HALF_UP);
            boolean alerta = percentual.compareTo(limiteTolerancia) > 0;
            return new LinhaAlerta(o.getProcedimento().getNome(), o.getItem().getNome(), previsto,
                    realizado, desvio, percentual, alerta);
        }).toList();
        model.addAttribute("linhas", linhas);
        model.addAttribute("tolerancia", tolerancia);
        model.addAttribute("quantidadeAlertas", linhas.stream().filter(LinhaAlerta::alerta).count());
        model.addAttribute("quantidadeLinhas", linhas.size());
        model.addAttribute("limiteHistorico", 300);
        return "orcamentos/alertas-desvio";
    }

    public record LinhaAlerta(String procedimento, String material, BigDecimal previsto,
                              BigDecimal realizado, BigDecimal desvio, BigDecimal percentual, boolean alerta) { }
}
