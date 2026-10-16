package com.marceloaleixo.centrocirurgico.controller;

import com.marceloaleixo.centrocirurgico.entity.BancoHoras;
import com.marceloaleixo.centrocirurgico.repository.ColaboradorRepository;
import com.marceloaleixo.centrocirurgico.service.BancoHorasService;
import com.marceloaleixo.centrocirurgico.web.BancoHorasForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/banco-horas")
public class BancoHorasController {
    private final BancoHorasService service; private final ColaboradorRepository colaboradorRepository;
    public BancoHorasController(BancoHorasService service, ColaboradorRepository colaboradorRepository){this.service=service;this.colaboradorRepository=colaboradorRepository;}
    @GetMapping public String listar(Model model){model.addAttribute("lancamentos",service.listar());return "banco-horas/lista";}
    @GetMapping("/novo") public String novo(Model model){BancoHorasForm f=new BancoHorasForm();model.addAttribute("form",f);preparar(model);return "banco-horas/formulario";}
    @GetMapping("/{id}/editar") public String editar(@PathVariable Long id,Model model){BancoHoras b=service.buscar(id);BancoHorasForm f=new BancoHorasForm();f.setId(b.getId());f.setColaboradorId(b.getColaborador().getId());f.setDataBanco(b.getDataBanco());f.setQuantidadeHoras(b.getQuantidadeHoras());f.setMotivo(b.getMotivo());model.addAttribute("form",f);preparar(model);return "banco-horas/formulario";}
    @PostMapping("/salvar") public String salvar(@ModelAttribute("form") BancoHorasForm form,Model model,RedirectAttributes ra){try{service.lancar(form);ra.addFlashAttribute("sucesso","Lançamento de banco de horas salvo. A escala existente foi atualizada quando aplicável; a próxima geração também reconhecerá essa data.");return "redirect:/banco-horas";}catch(IllegalArgumentException|IllegalStateException ex){model.addAttribute("erro",ex.getMessage());preparar(model);return "banco-horas/formulario";}}
    @PostMapping("/{id}/cancelar") public String cancelar(@PathVariable Long id,RedirectAttributes ra){try{service.cancelar(id);ra.addFlashAttribute("sucesso","Lançamento cancelado.");}catch(RuntimeException ex){ra.addFlashAttribute("erro",ex.getMessage());}return "redirect:/banco-horas";}
    private void preparar(Model model){model.addAttribute("colaboradores",colaboradorRepository.findByAtivoTrueOrderByNomeAsc());}
}
