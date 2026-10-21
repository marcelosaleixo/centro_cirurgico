package com.marceloaleixo.centrocirurgico.controller;

import com.marceloaleixo.centrocirurgico.repository.CirurgiaRepository;
import com.marceloaleixo.centrocirurgico.repository.ItemEstoqueRepository;
import com.marceloaleixo.centrocirurgico.service.RastreabilidadeCirurgiaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;

@Controller
@RequestMapping("/estoque/rastreabilidade-cirurgias")
public class RastreabilidadeCirurgiaController {
    private final RastreabilidadeCirurgiaService service;
    private final CirurgiaRepository cirurgiaRepository;
    private final ItemEstoqueRepository itemRepository;
    public RastreabilidadeCirurgiaController(RastreabilidadeCirurgiaService service, CirurgiaRepository cirurgiaRepository, ItemEstoqueRepository itemRepository) {
        this.service = service; this.cirurgiaRepository = cirurgiaRepository; this.itemRepository = itemRepository;
    }
    @GetMapping
    public String listar(@RequestParam(required = false) Long cirurgiaId, Model model) {
        model.addAttribute("cirurgias", cirurgiaRepository.findTop200ByOrderByDataCirurgiaDescHoraInicioDesc());
        model.addAttribute("itens", itemRepository.findByAtivoTrueOrderByNomeAsc());
        model.addAttribute("consumos", service.listarConsumos(cirurgiaId));
        model.addAttribute("cirurgiaSelecionada", cirurgiaId);
        return "estoque/rastreabilidade-cirurgias";
    }
    @PostMapping
    public String registrar(@RequestParam Long cirurgiaId, @RequestParam Long itemId,
            @RequestParam BigDecimal quantidade, @RequestParam String observacao, RedirectAttributes ra) {
        try {
            service.registrar(cirurgiaId, itemId, quantidade, observacao);
            ra.addFlashAttribute("sucesso", "Consumo registrado e vinculado à cirurgia com rastreabilidade por lote.");
            return "redirect:/estoque/rastreabilidade-cirurgias?cirurgiaId=" + cirurgiaId;
        } catch (Exception e) {
            ra.addFlashAttribute("erro", e.getMessage() == null ? "Não foi possível registrar o consumo." : e.getMessage());
            return "redirect:/estoque/rastreabilidade-cirurgias?cirurgiaId=" + cirurgiaId;
        }
    }
}
