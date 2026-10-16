package com.marceloaleixo.centrocirurgico.controller;

import com.marceloaleixo.centrocirurgico.service.AuditoriaOperacaoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/auditoria-operacoes")
public class AuditoriaOperacoesController {
    private final AuditoriaOperacaoService service;
    public AuditoriaOperacoesController(AuditoriaOperacaoService service) { this.service = service; }
    @GetMapping
    public String listar(@RequestParam(defaultValue = "0") int pagina, Model model) {
        var resultado = service.listar(pagina);
        model.addAttribute("registros", resultado.getContent());
        model.addAttribute("paginaAtual", resultado.getNumber());
        model.addAttribute("totalPaginas", resultado.getTotalPages());
        model.addAttribute("temAnterior", resultado.hasPrevious());
        model.addAttribute("temProxima", resultado.hasNext());
        return "admin/auditoria-operacoes";
    }
}
