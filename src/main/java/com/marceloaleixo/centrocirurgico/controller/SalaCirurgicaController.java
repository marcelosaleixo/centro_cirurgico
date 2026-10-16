package com.marceloaleixo.centrocirurgico.controller;

import com.marceloaleixo.centrocirurgico.entity.SalaCirurgica;
import com.marceloaleixo.centrocirurgico.repository.SalaCirurgicaRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/salas-cirurgicas")
public class SalaCirurgicaController {
    private final SalaCirurgicaRepository repository;

    public SalaCirurgicaController(SalaCirurgicaRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("salas", repository.findAll().stream()
                .sorted((a, b) -> a.getNome().compareToIgnoreCase(b.getNome())).toList());
        return "salas-cirurgicas/lista";
    }

    @GetMapping("/nova")
    public String nova(Model model) {
        SalaCirurgica sala = new SalaCirurgica();
        sala.setAtiva(true);
        model.addAttribute("sala", sala);
        model.addAttribute("edicao", false);
        return "salas-cirurgicas/formulario";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        SalaCirurgica sala = repository.findById(id).orElse(null);
        if (sala == null) {
            redirect.addFlashAttribute("erro", "Sala cirúrgica não encontrada.");
            return "redirect:/salas-cirurgicas";
        }
        model.addAttribute("sala", sala);
        model.addAttribute("edicao", true);
        return "salas-cirurgicas/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute("sala") SalaCirurgica sala, RedirectAttributes redirect) {
        String nome = sala.getNome() == null ? "" : sala.getNome().trim();
        String localizacao = sala.getLocalizacao() == null ? "" : sala.getLocalizacao().trim();
        if (nome.isBlank() || nome.length() > 80) {
            redirect.addFlashAttribute("erro", "Informe o nome da sala (até 80 caracteres).");
            return sala.getId() == null ? "redirect:/salas-cirurgicas/nova" : "redirect:/salas-cirurgicas/" + sala.getId() + "/editar";
        }
        if (localizacao.length() > 160) {
            redirect.addFlashAttribute("erro", "A localização deve ter no máximo 160 caracteres.");
            return sala.getId() == null ? "redirect:/salas-cirurgicas/nova" : "redirect:/salas-cirurgicas/" + sala.getId() + "/editar";
        }
        if (repository.findAll().stream().anyMatch(s -> s.getNome().equalsIgnoreCase(nome) && !java.util.Objects.equals(s.getId(), sala.getId()))) {
            redirect.addFlashAttribute("erro", "Já existe uma sala cirúrgica com esse nome.");
            return sala.getId() == null ? "redirect:/salas-cirurgicas/nova" : "redirect:/salas-cirurgicas/" + sala.getId() + "/editar";
        }
        sala.setNome(nome);
        sala.setLocalizacao(localizacao.isBlank() ? null : localizacao);
        repository.save(sala);
        redirect.addFlashAttribute("sucesso", "Sala cirúrgica salva com sucesso.");
        return "redirect:/salas-cirurgicas";
    }

    @PostMapping("/{id}/alternar-status")
    public String alternarStatus(@PathVariable Long id, RedirectAttributes redirect) {
        SalaCirurgica sala = repository.findById(id).orElse(null);
        if (sala == null) {
            redirect.addFlashAttribute("erro", "Sala cirúrgica não encontrada.");
            return "redirect:/salas-cirurgicas";
        }
        sala.setAtiva(!sala.isAtiva());
        try {
            repository.save(sala);
            redirect.addFlashAttribute("sucesso", "Status da sala atualizado.");
        } catch (DataIntegrityViolationException ex) {
            redirect.addFlashAttribute("erro", "Não foi possível atualizar a sala. Verifique se há registros vinculados.");
        }
        return "redirect:/salas-cirurgicas";
    }
}
