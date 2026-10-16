package com.marceloaleixo.centrocirurgico.controller;

import com.marceloaleixo.centrocirurgico.entity.FuncaoProfissional;
import com.marceloaleixo.centrocirurgico.service.FuncaoProfissionalService;
import com.marceloaleixo.centrocirurgico.service.AuditoriaAlteracaoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@RequestMapping("/funcoes")
public class FuncaoProfissionalController {
    private final FuncaoProfissionalService service;
    private final AuditoriaAlteracaoService auditoria;

    public FuncaoProfissionalController(FuncaoProfissionalService service, AuditoriaAlteracaoService auditoria) {
        this.service = service;
        this.auditoria = auditoria;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("funcoes", service.listarTodos());
        return "funcoes/lista";
    }

    @GetMapping("/nova")
    public String nova(Model model) {
        model.addAttribute("funcao", new FuncaoProfissional());
        model.addAttribute("titulo", "Nova função profissional");
        return "funcoes/formulario";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("funcao", service.buscarPorId(id));
        model.addAttribute("titulo", "Editar função profissional");
        return "funcoes/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute("funcao") FuncaoProfissional funcao,
                         RedirectAttributes redirect, Model model) {
        try {
            boolean novo = funcao.getId() == null;
            Map<String, String> antes = novo ? Map.of() : snapshot(service.buscarPorId(funcao.getId()));
            FuncaoProfissional salva = service.salvar(funcao);
            auditoria.registrarDiferencas("FuncaoProfissional", salva.getId(), novo ? "CRIACAO" : "EDICAO", antes, snapshot(salva));
            redirect.addFlashAttribute("sucesso", "Função profissional salva com sucesso.");
            return "redirect:/funcoes";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("titulo", funcao.getId() == null ? "Nova função profissional" : "Editar função profissional");
            model.addAttribute("erro", ex.getMessage());
            return "funcoes/formulario";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            FuncaoProfissional funcao = service.buscarPorId(id);
            Map<String, String> antes = snapshot(funcao);
            service.excluir(id);
            auditoria.registrarDiferencas("FuncaoProfissional", id, "EXCLUSAO", antes, Map.of("excluida", "true"));
            redirect.addFlashAttribute("sucesso", "Função excluída com sucesso.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirect.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/funcoes";
    }
    private Map<String, String> snapshot(FuncaoProfissional f) {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("nome", f.getNome());
        m.put("descricao", f.getDescricao());
        m.put("ativo", Boolean.toString(f.isAtivo()));
        return m;
    }
}
