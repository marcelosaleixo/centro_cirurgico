package com.marceloaleixo.centrocirurgico.controller;

import com.marceloaleixo.centrocirurgico.service.ControleTentativasLoginService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/seguranca")
public class PainelSegurancaController {
    private final ControleTentativasLoginService controle;

    public PainelSegurancaController(ControleTentativasLoginService controle) {
        this.controle = controle;
    }

    @GetMapping
    public String painel(Model model) {
        model.addAttribute("bloqueados", controle.listarBloqueados());
        return "admin/painel-seguranca";
    }

    @PostMapping("/desbloquear/{id}")
    public String desbloquear(@PathVariable Long id,
                              @RequestParam String justificativa,
                              Authentication authentication,
                              RedirectAttributes redirect) {
        try {
            controle.desbloquearManualmente(id, authentication.getName(), justificativa);
            redirect.addFlashAttribute("sucesso", "Conta desbloqueada. A operação foi registrada na auditoria.");
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/admin/seguranca";
    }
}
