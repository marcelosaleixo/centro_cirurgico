package com.marceloaleixo.centrocirurgico.controller;

import com.marceloaleixo.centrocirurgico.entity.AbrangenciaFeriado;
import com.marceloaleixo.centrocirurgico.entity.Feriado;
import com.marceloaleixo.centrocirurgico.service.FeriadoService;
import com.marceloaleixo.centrocirurgico.enums.RegraFeriado;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/feriados")
public class FeriadoController {
    private final FeriadoService service;
    public FeriadoController(FeriadoService service) { this.service = service; }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("feriados", service.listarTodos());
        return "feriados/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        Feriado f = new Feriado();
        f.setAbrangencia(AbrangenciaFeriado.MUNICIPAL);
        f.setAtivo(true);
        model.addAttribute("feriado", f);
        preparar(model);
        return "feriados/formulario";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("feriado", service.buscar(id));
        preparar(model);
        return "feriados/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute("feriado") Feriado feriado, Model model, RedirectAttributes ra) {
        try {
            service.salvar(feriado);
            ra.addFlashAttribute("sucesso", "Feriado salvo com sucesso.");
            return "redirect:/feriados";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("erro", ex.getMessage());
            preparar(model);
            return "feriados/formulario";
        }
    }

    @PostMapping("/{id}/inativar")
    public String inativar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            service.inativar(id);
            ra.addFlashAttribute("sucesso", "Feriado inativado; histórico preservado.");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/feriados";
    }

    private void preparar(Model model) {
        model.addAttribute("abrangencias", AbrangenciaFeriado.values());
        model.addAttribute("regrasOperacionais", RegraFeriado.values());
    }
}
