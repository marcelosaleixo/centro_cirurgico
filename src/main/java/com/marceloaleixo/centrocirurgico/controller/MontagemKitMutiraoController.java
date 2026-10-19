package com.marceloaleixo.centrocirurgico.controller;

import com.marceloaleixo.centrocirurgico.entity.*;
import com.marceloaleixo.centrocirurgico.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping("/mutiroes/dias/{diaId}/montagens-kits")
public class MontagemKitMutiraoController {
 private final DiaMutiraoRepository dias; private final KitCirurgicoRepository kits; private final MontagemKitMutiraoRepository montagens;
 public MontagemKitMutiraoController(DiaMutiraoRepository dias,KitCirurgicoRepository kits,MontagemKitMutiraoRepository montagens){this.dias=dias;this.kits=kits;this.montagens=montagens;}
 @GetMapping public String listar(@PathVariable Long diaId,Model model,RedirectAttributes r){DiaMutirao dia=dias.findById(diaId).orElse(null);if(dia==null){r.addFlashAttribute("erro","Dia de mutirão não encontrado.");return "redirect:/mutiroes";}model.addAttribute("dia",dia);model.addAttribute("montagens",montagens.findByDiaIdOrderByCriadoEmDesc(diaId));model.addAttribute("kits",kits.findAllByOrderByNomeAsc().stream().filter(KitCirurgico::isAtivo).toList());return "mutiroes/montagens-kits-lista";}
 @PostMapping("/criar") public String criar(@PathVariable Long diaId,@RequestParam Long kitId,@RequestParam(defaultValue="1") Integer quantidadeKits,@RequestParam(required=false) String responsavel,RedirectAttributes r){DiaMutirao dia=dias.findById(diaId).orElse(null);KitCirurgico kit=kits.findWithItensById(kitId).orElse(null);if(dia==null||kit==null||!kit.isAtivo()||quantidadeKits==null||quantidadeKits<1||quantidadeKits>1000){r.addFlashAttribute("erro","Selecione um dia, um modelo ativo e uma quantidade válida (1 a 1000).");return "redirect:/mutiroes";}MontagemKitMutirao m=new MontagemKitMutirao();m.setDia(dia);m.setKit(kit);m.setQuantidadeKits(quantidadeKits);m.setResponsavelMontagem(responsavel==null?null:responsavel.trim());m.setStatus("PENDENTE");for(ItemKitCirurgico origem:kit.getItens()){ItemConferenciaKitMutirao item=new ItemConferenciaKitMutirao();item.setMontagem(m);item.setItemModelo(origem);item.setDescricao(origem.getDescricao());item.setCategoria(origem.getCategoria());item.setUnidade(origem.getUnidade());item.setQuantidadePrevista(origem.getQuantidade().multiply(BigDecimal.valueOf(quantidadeKits)));item.setQuantidadeConferida(null);m.getItens().add(item);}montagens.save(m);r.addFlashAttribute("sucesso","Montagem criada com itens copiados do modelo. Nenhuma baixa de estoque foi realizada.");return "redirect:/mutiroes/dias/"+diaId+"/montagens-kits";}
 @GetMapping("/../{montagemId}/conferir") public String rotaConferirAlternativa(){return "redirect:/mutiroes";}
}
