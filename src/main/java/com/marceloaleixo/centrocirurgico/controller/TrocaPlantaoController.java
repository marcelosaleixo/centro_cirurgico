package com.marceloaleixo.centrocirurgico.controller;

import com.marceloaleixo.centrocirurgico.entity.Colaborador;
import com.marceloaleixo.centrocirurgico.entity.TrocaPlantao;
import com.marceloaleixo.centrocirurgico.repository.ColaboradorRepository;
import com.marceloaleixo.centrocirurgico.service.TrocaPlantaoService;
import com.marceloaleixo.centrocirurgico.web.TrocaPlantaoForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/trocas")
public class TrocaPlantaoController {
    private final TrocaPlantaoService service;
    private final ColaboradorRepository colaboradores;
    public TrocaPlantaoController(TrocaPlantaoService service, ColaboradorRepository colaboradores) {
        this.service = service; this.colaboradores = colaboradores;
    }
    @GetMapping public String listar(Model model) {
        model.addAttribute("trocas", service.listarTodas());
        model.addAttribute("aprovadores", colaboradores.findByAtivoTrueOrderByNomeAsc());
        return "trocas/lista";
    }
    @GetMapping("/nova") public String nova(Model model) {
        model.addAttribute("form", new TrocaPlantaoForm());
        model.addAttribute("colaboradores", colaboradores.findByAtivoTrueOrderByNomeAsc());
        return "trocas/formulario";
    }
    @PostMapping("/solicitar") public String solicitar(@ModelAttribute("form") TrocaPlantaoForm form, Model model, RedirectAttributes ra) {
        try {
            TrocaPlantao t = new TrocaPlantao(); Colaborador s = new Colaborador(); s.setId(form.getSolicitanteId());
            Colaborador a = new Colaborador(); a.setId(form.getAceitanteId());
            t.setSolicitante(s); t.setAceitante(a); t.setDataPlantaoOriginal(form.getDataPlantaoOriginal());
            t.setDataPlantaoDestino(form.getDataPlantaoDestino()); t.setMotivo(form.getMotivo());
            service.solicitar(t); ra.addFlashAttribute("sucesso", "Solicitação de troca registrada para aprovação.");
            return "redirect:/trocas";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("erro", ex.getMessage()); model.addAttribute("colaboradores", colaboradores.findByAtivoTrueOrderByNomeAsc());
            return "trocas/formulario";
        }
    }
    @PostMapping("/{id}/aprovar") public String aprovar(@PathVariable Long id, @RequestParam Long aprovadorId, RedirectAttributes ra) {
        try { service.aprovar(id, aprovadorId); ra.addFlashAttribute("sucesso", "Troca aprovada."); }
        catch (RuntimeException ex) { ra.addFlashAttribute("erro", ex.getMessage()); }
        return "redirect:/trocas";
    }
    @PostMapping("/{id}/rejeitar") public String rejeitar(@PathVariable Long id, @RequestParam Long aprovadorId, RedirectAttributes ra) {
        try { service.rejeitar(id, aprovadorId); ra.addFlashAttribute("sucesso", "Troca rejeitada."); }
        catch (RuntimeException ex) { ra.addFlashAttribute("erro", ex.getMessage()); }
        return "redirect:/trocas";
    }
    @PostMapping("/{id}/cancelar") public String cancelar(@PathVariable Long id, RedirectAttributes ra) {
        try { service.cancelar(id); ra.addFlashAttribute("sucesso", "Troca cancelada."); }
        catch (RuntimeException ex) { ra.addFlashAttribute("erro", ex.getMessage()); }
        return "redirect:/trocas";
    }
}
