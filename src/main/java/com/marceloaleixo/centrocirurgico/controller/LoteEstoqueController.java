package com.marceloaleixo.centrocirurgico.controller;

import com.marceloaleixo.centrocirurgico.repository.LoteEstoqueRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.time.LocalDate;
import java.math.BigDecimal;

@Controller
@RequestMapping("/estoque/lotes")
public class LoteEstoqueController {
    private final LoteEstoqueRepository repository;
    public LoteEstoqueController(LoteEstoqueRepository repository) { this.repository = repository; }

    @GetMapping
    public String listar(Model model) {
        LocalDate hoje = LocalDate.now();
        model.addAttribute("lotes", repository.findByOrderByDataValidadeAsc());
        model.addAttribute("limiteAlerta", hoje.plusDays(90));
        model.addAttribute("hoje", hoje);
        return "estoque/lotes-validade";
    }
}
