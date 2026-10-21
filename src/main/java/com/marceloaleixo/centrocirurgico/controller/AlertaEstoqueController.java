package com.marceloaleixo.centrocirurgico.controller;

import com.marceloaleixo.centrocirurgico.entity.ItemEstoque;
import com.marceloaleixo.centrocirurgico.repository.ItemEstoqueRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/estoque/alertas-minimo")
public class AlertaEstoqueController {
    private final ItemEstoqueRepository itemRepository;

    public AlertaEstoqueController(ItemEstoqueRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @GetMapping
    public String listar(Model model) {
        List<ItemEstoque> itensCriticos = itemRepository.findByAtivoTrueOrderByNomeAsc().stream()
            .filter(item -> {
                BigDecimal saldo = item.getEstoqueGeral() == null || item.getEstoqueGeral().getQuantidadeAtual() == null
                    ? BigDecimal.ZERO : item.getEstoqueGeral().getQuantidadeAtual();
                BigDecimal minimo = item.getEstoqueMinimo() == null ? BigDecimal.ZERO : item.getEstoqueMinimo();
                return saldo.compareTo(minimo) <= 0;
            }).collect(Collectors.toList());

        long semSaldo = itensCriticos.stream().filter(item -> item.getEstoqueGeral() == null
            || item.getEstoqueGeral().getQuantidadeAtual() == null
            || item.getEstoqueGeral().getQuantidadeAtual().compareTo(BigDecimal.ZERO) <= 0).count();
        model.addAttribute("itens", itensCriticos);
        model.addAttribute("totalAlertas", itensCriticos.size());
        model.addAttribute("semSaldo", semSaldo);
        model.addAttribute("totalReposicao", itensCriticos.stream().map(item -> {
            BigDecimal saldo = item.getEstoqueGeral() == null || item.getEstoqueGeral().getQuantidadeAtual() == null
                ? BigDecimal.ZERO : item.getEstoqueGeral().getQuantidadeAtual();
            BigDecimal minimo = item.getEstoqueMinimo() == null ? BigDecimal.ZERO : item.getEstoqueMinimo();
            return minimo.subtract(saldo).max(BigDecimal.ZERO);
        }).reduce(BigDecimal.ZERO, BigDecimal::add));
        return "estoque/alertas-minimo";
    }
}
