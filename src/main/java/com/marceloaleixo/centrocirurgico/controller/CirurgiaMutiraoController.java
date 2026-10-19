package com.marceloaleixo.centrocirurgico.controller;
import com.marceloaleixo.centrocirurgico.service.CirurgiaMutiraoService;
import com.marceloaleixo.centrocirurgico.repository.DiaMutiraoRepository;
import com.marceloaleixo.centrocirurgico.entity.CirurgiaMutirao;
import org.springframework.stereotype.Controller; import org.springframework.ui.Model; import org.springframework.web.bind.annotation.*; import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller @RequestMapping("/mutiroes/dias")
public class CirurgiaMutiraoController {
 private final CirurgiaMutiraoService service; private final DiaMutiraoRepository dias;
 public CirurgiaMutiraoController(CirurgiaMutiraoService s, DiaMutiraoRepository d){service=s;dias=d;}
 @GetMapping("/{diaId}/cirurgias") public String listar(@PathVariable Long diaId, Model model){model.addAttribute("dia",dias.findById(diaId).orElseThrow());model.addAttribute("cirurgias",service.listar(diaId));return "mutiroes/cirurgias";}
 @GetMapping("/{diaId}/cirurgias/nova") public String nova(@PathVariable Long diaId, Model model){model.addAttribute("dia",dias.findById(diaId).orElseThrow());model.addAttribute("cirurgia",new CirurgiaMutirao());model.addAttribute("pacientes",service.pacientes());model.addAttribute("procedimentos",service.procedimentos());return "mutiroes/cirurgia-formulario";}
 @GetMapping("/cirurgias/{id}/editar") public String editar(@PathVariable Long id,Model model){CirurgiaMutirao c=service.buscar(id);model.addAttribute("cirurgia",c);model.addAttribute("dia",c.getDia());model.addAttribute("pacientes",service.pacientes());model.addAttribute("procedimentos",service.procedimentos());return "mutiroes/cirurgia-formulario";}
 @PostMapping("/{diaId}/cirurgias/salvar") public String salvar(@PathVariable Long diaId,@RequestParam(required=false) Long id,@RequestParam Long pacienteId,@RequestParam Long procedimentoId,@RequestParam String medicoResponsavel,@RequestParam java.time.LocalTime horaInicio,@RequestParam java.time.LocalTime horaFim,@RequestParam String status,@RequestParam(required=false) String observacoes,RedirectAttributes ra){try{service.salvar(id,diaId,pacienteId,procedimentoId,medicoResponsavel,horaInicio,horaFim,status,observacoes);ra.addFlashAttribute("sucesso","Cirurgia salva com sucesso.");}catch(Exception e){ra.addFlashAttribute("erro",e.getMessage());return "redirect:/mutiroes/dias/"+diaId+"/cirurgias/nova";}return "redirect:/mutiroes/dias/"+diaId+"/cirurgias";}
 @PostMapping("/cirurgias/{id}/excluir") public String excluir(@PathVariable Long id,RedirectAttributes ra){CirurgiaMutirao c=service.buscar(id);Long diaId=c.getDia().getId();service.excluir(id);ra.addFlashAttribute("sucesso","Cirurgia removida da programação.");return "redirect:/mutiroes/dias/"+diaId+"/cirurgias";}
}
