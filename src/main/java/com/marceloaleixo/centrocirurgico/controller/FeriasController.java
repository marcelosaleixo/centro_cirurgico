package com.marceloaleixo.centrocirurgico.controller;

import com.marceloaleixo.centrocirurgico.entity.Colaborador;
import com.marceloaleixo.centrocirurgico.entity.Ferias;
import com.marceloaleixo.centrocirurgico.repository.ColaboradorRepository;
import com.marceloaleixo.centrocirurgico.service.FeriasService;
import com.marceloaleixo.centrocirurgico.web.FeriasForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ferias")
public class FeriasController {
    private final FeriasService service;
    private final ColaboradorRepository colaboradores;
    public FeriasController(FeriasService service, ColaboradorRepository colaboradores) {
        this.service = service; this.colaboradores = colaboradores;
    }
    @GetMapping public String listar(Model model) {
        model.addAttribute("feriasList", service.listarTodas());
        model.addAttribute("aprovadores", colaboradores.findByAtivoTrueOrderByNomeAsc());
        return "ferias/lista";
    }
    @GetMapping("/nova") public String nova(Model model) {
        FeriasForm form = new FeriasForm();
        form.setDataInicio(java.time.LocalDate.now());
        form.setDataFim(java.time.LocalDate.now().plusDays(7));
        model.addAttribute("form", form);
        model.addAttribute("colaboradores", colaboradores.findByAtivoTrueOrderByNomeAsc());
        return "ferias/formulario";
    }
    @PostMapping("/solicitar") public String solicitar(@ModelAttribute("form") FeriasForm form, Model model, RedirectAttributes ra) {
        try {
            Ferias f = new Ferias(); Colaborador c = new Colaborador(); c.setId(form.getColaboradorId());
            f.setColaborador(c); f.setDataInicio(form.getDataInicio()); f.setDataFim(form.getDataFim()); f.setObservacao(form.getObservacao());
            service.solicitar(f); ra.addFlashAttribute("sucesso", "Solicitação de férias registrada para aprovação.");
            return "redirect:/ferias";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("erro", ex.getMessage()); model.addAttribute("colaboradores", colaboradores.findByAtivoTrueOrderByNomeAsc());
            return "ferias/formulario";
        }
    }
    @PostMapping("/{id}/aprovar") public String aprovar(@PathVariable Long id, @RequestParam Long aprovadorId, RedirectAttributes ra) {
        try { service.aprovar(id, aprovadorId); ra.addFlashAttribute("sucesso", "Férias aprovadas."); }
        catch (RuntimeException ex) { ra.addFlashAttribute("erro", ex.getMessage()); }
        return "redirect:/ferias";
    }
    @PostMapping("/{id}/rejeitar") public String rejeitar(@PathVariable Long id, @RequestParam Long aprovadorId, RedirectAttributes ra) {
        try { service.rejeitar(id, aprovadorId); ra.addFlashAttribute("sucesso", "Solicitação de férias rejeitada."); }
        catch (RuntimeException ex) { ra.addFlashAttribute("erro", ex.getMessage()); }
        return "redirect:/ferias";
    }
    @PostMapping("/{id}/cancelar") public String cancelar(@PathVariable Long id, RedirectAttributes ra) {
        try { service.cancelar(id); ra.addFlashAttribute("sucesso", "Férias canceladas."); }
        catch (RuntimeException ex) { ra.addFlashAttribute("erro", ex.getMessage()); }
        return "redirect:/ferias";
    }
}
