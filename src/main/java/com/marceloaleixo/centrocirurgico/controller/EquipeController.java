package com.marceloaleixo.centrocirurgico.controller;

import com.marceloaleixo.centrocirurgico.entity.Equipe;
import com.marceloaleixo.centrocirurgico.repository.ColaboradorRepository;
import com.marceloaleixo.centrocirurgico.service.EquipeService;
import com.marceloaleixo.centrocirurgico.service.AuditoriaAlteracaoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("/equipes")
public class EquipeController {
    private final EquipeService service;
    private final ColaboradorRepository colaboradorRepository;
    private final AuditoriaAlteracaoService auditoria;

    public EquipeController(EquipeService service,
                            ColaboradorRepository colaboradorRepository,
                            AuditoriaAlteracaoService auditoria) {
        this.service = service;
        this.colaboradorRepository = colaboradorRepository;
        this.auditoria = auditoria;
    }

    @GetMapping
    public String listar(Model model) {
        var equipes = service.listarTodas();
        Map<Long, Long> quantidadeColaboradores = new LinkedHashMap<>();

        for (Equipe equipe : equipes) {
            quantidadeColaboradores.put(
                equipe.getId(),
                colaboradorRepository.countByEquipeId(equipe.getId())
            );
        }

        model.addAttribute("equipes", equipes);
        model.addAttribute("quantidadeColaboradores", quantidadeColaboradores);
        return "equipes/lista";
    }

    @GetMapping("/nova")
    public String nova(Model model) {
        model.addAttribute("equipe", new Equipe());
        model.addAttribute("titulo", "Nova equipe");
        return "equipes/formulario";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("equipe", service.buscarPorId(id));
        model.addAttribute("titulo", "Editar equipe");
        return "equipes/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute("equipe") Equipe equipe,
                         RedirectAttributes redirect, Model model) {
        try {
            boolean novo = equipe.getId() == null;
            Map<String, String> antes = novo ? Map.of() : snapshot(service.buscarPorId(equipe.getId()));
            Equipe salva = service.salvar(equipe);
            auditoria.registrarDiferencas("Equipe", salva.getId(), novo ? "CRIACAO" : "EDICAO", antes, snapshot(salva));
            redirect.addFlashAttribute("sucesso", "Equipe salva com sucesso.");
            return "redirect:/equipes";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("titulo", equipe.getId() == null
                    ? "Nova equipe" : "Editar equipe");
            model.addAttribute("erro", ex.getMessage());
            return "equipes/formulario";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            Equipe equipe = service.buscarPorId(id);
            Map<String, String> antes = snapshot(equipe);
            service.excluir(id);
            auditoria.registrarDiferencas("Equipe", id, "EXCLUSAO", antes, Map.of("excluida", "true"));
            redirect.addFlashAttribute("sucesso", "Equipe excluída com sucesso.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirect.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/equipes";
    }
    private Map<String, String> snapshot(Equipe e) {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("nome", e.getNome());
        m.put("descricao", e.getDescricao());
        m.put("ativo", Boolean.toString(e.isAtivo()));
        return m;
    }
}
