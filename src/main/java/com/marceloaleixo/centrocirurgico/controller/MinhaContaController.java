package com.marceloaleixo.centrocirurgico.controller;

import com.marceloaleixo.centrocirurgico.service.AlteracaoSenhaService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/minha-conta")
public class MinhaContaController {
    private final AlteracaoSenhaService service;
    private final com.marceloaleixo.centrocirurgico.service.AuditoriaSegurancaService auditoria;

    public MinhaContaController(AlteracaoSenhaService service,
                                com.marceloaleixo.centrocirurgico.service.AuditoriaSegurancaService auditoria) {
        this.service = service;
        this.auditoria = auditoria;
    }

    @GetMapping("/senha")
    public String formulario(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        return "conta/alterar-senha";
    }

    @PostMapping("/senha")
    public String alterar(@RequestParam String senhaAtual,
                          @RequestParam String novaSenha,
                          @RequestParam String confirmarSenha,
                          Authentication authentication,
                          RedirectAttributes redirect) {
        try {
            service.alterar(authentication.getName(), senhaAtual, novaSenha, confirmarSenha);
            auditoria.registrar(authentication.getName(), "SENHA_ALTERADA", "Senha alterada pelo próprio usuário", true);
            redirect.addFlashAttribute("sucesso", "Senha alterada com sucesso.");
            return "redirect:/minha-conta/senha";
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
            return "redirect:/minha-conta/senha";
        }
    }
}
